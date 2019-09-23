package tech.zettervall.notes;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.parceler.Parcels;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import tech.zettervall.mNotes.R;
import tech.zettervall.mNotes.databinding.FragmentNoteBinding;
import tech.zettervall.notes.adapters.TagSelectAdapter;
import tech.zettervall.notes.models.Note;
import tech.zettervall.notes.models.Tag;
import tech.zettervall.notes.services.NotificationJobService;
import tech.zettervall.notes.utils.BitmapUtil;
import tech.zettervall.notes.utils.DateTimeUtil;
import tech.zettervall.notes.utils.KeyboardUtil;
import tech.zettervall.notes.utils.RecyclerViewUtil;
import tech.zettervall.notes.viewmodels.NoteFragmentViewModel;

import static android.content.Context.JOB_SCHEDULER_SERVICE;

/**
 * Fragment for editing a Note, uses ViewModel to fetch data from db.
 */
public class NoteFragment extends Fragment implements TagSelectAdapter.OnTagClickListener,
        View.OnClickListener {

    private static final String TAG = NoteFragment.class.getSimpleName();
    static final int REQUEST_TAKE_PHOTO = 1;
    private boolean mTrash, mFinalDeletion, mRestore, mIsTablet;
    private long mReminderDateTimeEpoch;
    private FragmentNoteBinding mDataBinding;
    private NoteFragmentViewModel mNoteFragmentViewModel;
    private Note mNote;
    private Calendar mReminderCalender, mDateTimePickerCalender;
    private JobScheduler mJobScheduler;
    private TagSelectAdapter mTagSelectAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mDataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_note, container, false);
        View rootView = mDataBinding.getRoot();

        // Initialize ViewModel
        mNoteFragmentViewModel = ViewModelProviders.of(this).get(NoteFragmentViewModel.class);

        // Enable Toolbar MenuItem handling.
        setHasOptionsMenu(true);

        // Get System Scheduler
        mJobScheduler = (JobScheduler) getActivity().getSystemService(JOB_SCHEDULER_SERVICE);

        // Get Note
        if (savedInstanceState != null) { // Existing Note but configuration changed
            mNote = Parcels.unwrap(savedInstanceState.getParcelable(Constants.NOTE));
        } else if (getArguments() != null) { // Clicked Note or new Note from Favorites/NotesByTag Fragment
            Note note = Parcels.unwrap(getArguments().getParcelable(Constants.NOTE));
            Tag tag = Parcels.unwrap(getArguments().getParcelable(Constants.TAG));
            boolean favorite = getArguments().getBoolean(Constants.NOTE_FAVORITE);
            if (favorite) { // FAB clicked in FavoritesFragment
                mNote = newNote(true, null);
            } else if (tag != null) { // FAB clicked in NotesByTagFragment
                mNote = newNote(false, tag);
            } else if (note != null) { // Note clicked in any Fragment
                mNote = note;
            }
        } else { // New Note
            mNote = newNote(false, null);
        }

        // Show Reminder
        showReminder(getActivity(), mNote.getNotificationEpoch());

        // Get Tablet bool
        mIsTablet = getResources().getBoolean(R.bool.isTablet);

        // Set GUI fields
        mDataBinding.fragmentNoteTitleEdittext.setText(mNote.getTitle());
        mDataBinding.fragmentNoteTextEdittext.setText(mNote.getText());
        mDataBinding.fragmentNoteCreatedTextview.setText(getString(R.string.creation_date,
                mNote.getCreationString(getActivity())));
        if (mNote.getModifiedEpoch() != -1) {
            mDataBinding.fragmentNoteUpdatedTextview.setText(getString(R.string.modified_date,
                    mNote.getModifiedString(getActivity())));
        } else {
            mDataBinding.fragmentNoteUpdatedTextview.setVisibility(View.GONE);
        }

        // Disable editing for trashed Notes
        if (mNote.isTrash()) {
            mDataBinding.fragmentNoteTitleEdittext.setEnabled(false);
            mDataBinding.fragmentNoteTextEdittext.setEnabled(false);
        }

        // Hide / Show FAB depending on device
        if (mIsTablet) {
            mDataBinding.fragmentNoteFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveNote();
                    Toast.makeText(getActivity(), getString(R.string.note_saved), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            mDataBinding.fragmentNoteFab.hide();
        }

        // Set title
        if (!mIsTablet && mNote.isTrash()) {
            getActivity().setTitle(R.string.note_trash);
            // Hide keyboard
            KeyboardUtil.hideKeyboard(getActivity());
        }

        // Set focus on text field if user has set title
        if (!mNote.getTitle().isEmpty()) {
            mDataBinding.fragmentNoteTextEdittext.requestFocus();
            KeyboardUtil.hideKeyboard(getActivity());
        }

        // Tags sanity check
        tagsSanityCheck();

        // Set Tags TextView
        updateTagsUi();

        return rootView;
    }

    /**
     * Show or Hide Reminder layout.
     */
    private void showReminder(final Context context, long notificationEpoch) {
        if (notificationEpoch > 0) {
            mDataBinding.fragmentNoteReminderLinearlayout.setVisibility(View.VISIBLE);
            mDataBinding.fragmentNoteReminderTextview.setText(getString(R.string.reminder_set,
                    DateTimeUtil.getDateStringFromEpoch(notificationEpoch, context)));
            mDataBinding.fragmentNoteReminderRemoveButton.setOnClickListener(this);
        } else {
            mDataBinding.fragmentNoteReminderLinearlayout.setVisibility(View.GONE);
        }
    }

    /**
     * Create new Note.
     *
     * @param isFavorite Set favorite on creation
     * @param tag        Set Tag
     */
    private Note newNote(boolean isFavorite, @Nullable Tag tag) {
        List<Integer> tagIDs = new ArrayList<>();
        if (tag != null) {
            tagIDs.add(tag.getId());
        }
        return new Note(mDataBinding.fragmentNoteTitleEdittext.getText().toString(),
                mDataBinding.fragmentNoteTextEdittext.getText().toString(),
                null,
                tagIDs,
                DateTimeUtil.getCurrentEpoch(),
                -1,
                -1,
                false,
                isFavorite);
    }

    /**
     * Save Note.
     */
    private void saveNote() {
        mNote.setTrash(mTrash); // Set Trash state

        // Decides when to update modified time stamp
        if (!mDataBinding.fragmentNoteTitleEdittext.getText().toString().equals(mNote.getTitle()) ||
                !mDataBinding.fragmentNoteTextEdittext.getText().toString().equals(mNote.getText())) {
            // Update modified time stamp
            mNote.setModifiedEpoch(DateTimeUtil.getCurrentEpoch());
        }

        // Update title/text in Note Object
        mNote.setTitle(mDataBinding.fragmentNoteTitleEdittext.getText().toString().trim());
        mNote.setText(mDataBinding.fragmentNoteTextEdittext.getText().toString().trim());

        // Save to db
        if (mNote.getId() > 0) { // Existing Note
            mNoteFragmentViewModel.updateNote(mNote);
        } else if (!mNote.getTitle().isEmpty() ||
                !mNote.getText().isEmpty()) { // New Note
            mNote.setId((int) mNoteFragmentViewModel.insertNote(mNote));
        }
    }

    /**
     * Change MenuItem icon.
     */
    private void setFavoritizedIcon(MenuItem item) {
        item.setIcon(R.drawable.ic_star);
        item.setTitle(R.string.action_unfavoritize);
    }

    /**
     * Change MenuItem icon.
     */
    private void setUnfavoritizedIcon(MenuItem item) {
        item.setIcon(R.drawable.ic_star_border);
        item.setTitle(R.string.action_favoritize);
    }

    /**
     * Schedule Reminder (Notification) for Note.
     *
     * @param context Activity context
     */
    private void scheduleReminderJob(Context context) {
        // Set bundle
        PersistableBundle bundle = new PersistableBundle();
        if (mNote.getId() == 0) {
            saveNote();
        }
        bundle.putInt(Constants.NOTE_ID, mNote.getId());

        // Build job
        ComponentName jobService = new ComponentName(context.getPackageName(),
                NotificationJobService.class.getName());
        JobInfo.Builder jobBuilder = new JobInfo.Builder(mNote.getId(), jobService)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setMinimumLatency(
                        Math.abs(DateTimeUtil.getCurrentEpoch() - mReminderDateTimeEpoch))
                .setPersisted(true)
                .setExtras(bundle);
        JobInfo jobInfo = jobBuilder.build();

        // Schedule job
        mJobScheduler.schedule(jobInfo);

        // Show Reminder
        showReminder(getActivity(), mNote.getNotificationEpoch());
    }

    /**
     * Cancel Reminder job (Notification) for Note.
     */
    private void cancelReminderJob() {
        if (mJobScheduler != null) {
            mNote.setNotificationEpoch(-1);
            mJobScheduler.cancel(mNote.getId());
        }
    }

    /**
     * Pick date/time for Notification.
     */
    private void dateTimePicker() {
        mDateTimePickerCalender = Calendar.getInstance();
        mReminderCalender = Calendar.getInstance();
        if (mNote.getNotificationEpoch() > 0) {
            mDateTimePickerCalender.setTimeInMillis(mNote.getNotificationEpoch());
        }
        new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                mReminderCalender.set(year, monthOfYear, dayOfMonth);
                new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        mReminderCalender.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        mReminderCalender.set(Calendar.MINUTE, minute);
                        mReminderDateTimeEpoch = DateTimeUtil.
                                getEpochWithZeroSeconds(mReminderCalender.getTime().getTime());
                        // Display for user
                        Toast.makeText(getActivity(), getString(R.string.reminder_set,
                                DateTimeUtil.getDateStringFromEpoch(mReminderDateTimeEpoch,
                                        getActivity())), Toast.LENGTH_LONG).show();
                        // Set Reminder for Note
                        mNote.setNotificationEpoch(mReminderDateTimeEpoch);
                        scheduleReminderJob(getActivity());
                    }
                }, mDateTimePickerCalender.get(Calendar.HOUR_OF_DAY),
                        mDateTimePickerCalender.get(Calendar.MINUTE),
                        DateTimeUtil.use24h(getActivity())).show();
            }
        }, mDateTimePickerCalender.get(Calendar.YEAR),
                mDateTimePickerCalender.get(Calendar.MONTH),
                mDateTimePickerCalender.get(Calendar.DATE)).show();
    }

    @Override
    public void onClick(View v) {
        if (v == mDataBinding.fragmentNoteReminderRemoveButton) { // Remove Button for reminder
            mDataBinding.fragmentNoteReminderLinearlayout.setVisibility(View.GONE);
            cancelReminderJob();
            Toast.makeText(getContext(), getString(R.string.reminder_removed),
                    Toast.LENGTH_SHORT).show();
        } else if (v == mDataBinding.fragmentNotePhotoImageview) { // Photo ImageView
            Intent intent = new Intent(getActivity(), PhotoActivity.class);
            intent.putExtra(Constants.PHOTO_PATH, mNote.getPhotoPath());
            intent.putExtra(Constants.NOTE_TITLE,
                    mDataBinding.fragmentNoteTitleEdittext.getText().toString().trim());
            startActivity(intent);
        } else if (v == mDataBinding.fragmentNoteRemovePhotoImageview) { // Photo remove Button
            DialogInterface.OnClickListener dialogClickListener =
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    deleteImageFile();
                                    break;
                                case DialogInterface.BUTTON_NEGATIVE:
                                    break;
                            }
                        }
                    };
            AlertDialog.Builder deleteBuilder = new AlertDialog.Builder(getActivity());
            deleteBuilder.setTitle(getString(R.string.confirm_photo_delete))
                    .setPositiveButton(getString(R.string.confirm), dialogClickListener)
                    .setNegativeButton(getString(R.string.abort), dialogClickListener)
                    .show();
        }
    }

    /**
     * Save / Trash / Restore / Delete Note.
     */
    @Override
    public void onPause() {
        super.onPause();
        if (!mNote.isTrash()) { // Not Trashed
            if (!mTrash) { // Don't trash, just save
                saveNote();
            } else { // Trash Note
                mNote.setTrash(true);
                mNoteFragmentViewModel.updateNote(mNote);
                // Remove notification
                if (mNote.getNotificationEpoch() > 0) {
                    cancelReminderJob();
                }
                // Message to user
                String toastMessage = mNote.getTitle() != null && !mNote.getTitle().isEmpty() ?
                        getString(R.string.note_trashed_detailed, mNote.getTitle()) :
                        getString(R.string.note_trashed);
                Toast.makeText(getActivity(), toastMessage, Toast.LENGTH_SHORT).show();
            }
        } else { // Trashed Note
            if (mFinalDeletion) { // Final deletion
                mNoteFragmentViewModel.deleteNote(mNote);
                deleteImageFile(); // Delete photo
            } else if (mRestore) { // Restore trashed note
                mNote.setTrash(false);
                mNoteFragmentViewModel.updateNote(mNote);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Reset Notification Epoch if it has passed
        if (DateTimeUtil.getCurrentEpoch() > mNote.getNotificationEpoch()) {
            mNote.setNotificationEpoch(-1);
        }
        // Set Photo
        if (mNote.getPhotoPath() != null && !mNote.getPhotoPath().isEmpty()) {
            Bitmap photo = BitmapUtil.getBitmap(mNote.getPhotoPath());
            if (photo != null) {
                mDataBinding.fragmentNotePhotoLayout.setVisibility(View.VISIBLE);
                mDataBinding.fragmentNotePhotoImageview.setImageBitmap(photo);
                mDataBinding.fragmentNotePhotoImageview.setOnClickListener(this);
                mDataBinding.fragmentNoteRemovePhotoImageview.setOnClickListener(this);
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelable(Constants.NOTE, Parcels.wrap(mNote));
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (!mNote.isTrash()) {
            inflater.inflate(R.menu.menu_note, menu);
            if (mNote != null && mNote.isFavorite()) {
                MenuItem favoritize = menu.findItem(R.id.action_favoritize);
                setFavoritizedIcon(favoritize);
            }
        } else { // TRASHED
            inflater.inflate(R.menu.menu_note_trashed, menu);
        }
    }

    /**
     * Click event for Tag Adapter within AlertDialog for selecting Tags.
     *
     * @param index Index of clicked Tag
     */
    @Override
    public void onTagClick(int index) {
        List<Integer> noteTagIDs = mNote.getTagIDs();
        Integer clickedTagID = mTagSelectAdapter.getTags().get(index).getId();
        if (mTagSelectAdapter.getCheckedTags()[index]) { // Uncheck
            mTagSelectAdapter.setCheckedState(index, false);
            noteTagIDs.remove(clickedTagID); // Remove Tag
        } else { // Check
            mTagSelectAdapter.setCheckedState(index, true);
            noteTagIDs.add(clickedTagID); // Add Tag
        }

        // Update Note Tags
        mNote.setTagIDs(noteTagIDs);

        // Set Tags TextView
        updateTagsUi();
    }

    /**
     * Check that no old tags persist in Note.
     */
    private void tagsSanityCheck() {
        List<Integer> tagsIdListNote = mNote.getTagIDs();
        List<Integer> tagsIdListDb = new ArrayList<>();
        List<Tag> tagsList = mNoteFragmentViewModel.getTags();
        for (Tag tag : tagsList) {
            tagsIdListDb.add(tag.getId());
        }
        for (int tagID : tagsIdListNote) {
            if (!tagsIdListDb.contains(tagID)) {
                tagsIdListNote.remove(tagID);
            }
        }
        mNote.setTagIDs(tagsIdListNote);
    }

    /**
     * Update Tags TextView.
     */
    private void updateTagsUi() {
        StringBuilder tagsString = new StringBuilder();
        if (!mNote.getTagIDs().isEmpty()) {
            List<Tag> tags = mNoteFragmentViewModel.getTags();
            List<String> tagTitles = new ArrayList<>();
            for (Tag tag : tags) {
                if (mNote.getTagIDs().contains(tag.getId())) {
                    tagTitles.add(tag.getTitle());
                }
            }
            Collections.sort(tagTitles);
            for (int i = 0; i < tagTitles.size(); i++) {
                tagsString.append("#").append(tagTitles.get(i));
                if (i < tagTitles.size() - 1) {
                    tagsString.append(" ");
                }
            }
            mDataBinding.fragmentNoteTagsTextview.setVisibility(View.VISIBLE);
            mDataBinding.fragmentNoteTagsTextview.setText(tagsString.toString());
        } else {
            mDataBinding.fragmentNoteTagsTextview.setVisibility(View.GONE);
        }
    }

    /**
     * Delete photo from storage.
     */
    private void deleteImageFile() {
        if (mNote.getPhotoPath() != null && !mNote.getPhotoPath().isEmpty()) {
            File file = new File(mNote.getPhotoPath());
            boolean deleted = file.delete();
            if (deleted) {
                mDataBinding.fragmentNotePhotoLayout.setVisibility(View.GONE);
                mDataBinding.fragmentNotePhotoImageview.setImageBitmap(null);
                Toast.makeText(getActivity(),
                        getString(R.string.note_photo_removed),
                        Toast.LENGTH_SHORT).show();
            }
            mNote.setPhotoPath(null); // Reset path
        }
    }

    /**
     * Create Image File for saving photo.
     *
     * @return Image
     * @throws IOException
     */
    private File createImageFile() throws IOException {
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                String.valueOf(mNote.getCreationEpoch()),  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        mNote.setPhotoPath(image.getAbsolutePath()); // Save path to Note
        return image;
    }

    /**
     * Launch Intent for taking photo.
     */
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                // Error occurred while creating the File
                e.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getActivity(),
                        "tech.zettervall.notes.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_favoritize:
                if (mNote.isFavorite()) { // Note is in favorites
                    mNote.setFavorite(false);
                    setUnfavoritizedIcon(item);
                    Toast.makeText(getActivity(), getString(R.string.note_favorites_removed),
                            Toast.LENGTH_SHORT).show();
                } else { // Note is not in favorites
                    mNote.setFavorite(true);
                    setFavoritizedIcon(item);
                    Toast.makeText(getActivity(), getString(R.string.note_favorites_added),
                            Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.action_tags:
                // Inflate View
                View dialogView = View.inflate(getActivity(), R.layout.dialog_tag_select, null);

                // Set Adapter / LayoutManager
                RecyclerView recyclerView = dialogView.findViewById(R.id.dialog_tag_select_list_recyclerview);
                LinearLayoutManager layoutManager = RecyclerViewUtil.getDefaultLinearLayoutManager(getActivity());
                mTagSelectAdapter = new TagSelectAdapter(this, mNoteFragmentViewModel.getTags());
                recyclerView.setAdapter(mTagSelectAdapter);
                recyclerView.setLayoutManager(layoutManager);

                // Pre-check CheckBoxes for used Tags
                if (!mNote.getTagIDs().isEmpty()) {
                    for (int i = 0; i < mTagSelectAdapter.getTags().size(); i++) {
                        if (mNote.getTagIDs().contains(mTagSelectAdapter.getTags().get(i).getId())) {
                            mTagSelectAdapter.setCheckedState(i, true);
                        }
                    }
                }

                AlertDialog.Builder tagsBuilder = new AlertDialog.Builder(getActivity());
                tagsBuilder.setTitle(getString(R.string.action_tags))
                        .setView(dialogView)
                        .setPositiveButton(R.string.confirm_done, null)
                        .show();
                break;
            case R.id.action_photo:
                if (mDataBinding.fragmentNoteTitleEdittext.getText().toString().isEmpty() &&
                        mDataBinding.fragmentNoteTextEdittext.getText().toString().isEmpty()) {
                    Toast.makeText(getActivity(), getString(R.string.error_missing_title_text),
                            Toast.LENGTH_SHORT).show();
                } else {
                    dispatchTakePictureIntent(); // Only allow photo for non-empty Note
                }
                break;
            case R.id.action_reminder:
                if (mDataBinding.fragmentNoteTitleEdittext.getText().toString().isEmpty() &&
                        mDataBinding.fragmentNoteTextEdittext.getText().toString().isEmpty()) {
                    Toast.makeText(getActivity(), getString(R.string.error_missing_title_text),
                            Toast.LENGTH_SHORT).show();
                } else {
                    dateTimePicker(); // Only allow reminder for non-empty Note
                }
                break;
            case R.id.action_delete:
                DialogInterface.OnClickListener dialogClickListenerDelete =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case DialogInterface.BUTTON_POSITIVE:
                                        if (!mNote.isTrash()) { // Regular Note, trash it
                                            mTrash = true;
                                        } else { // Already trashed Note, Final deletion!
                                            mFinalDeletion = true;
                                            String message = !mNote.getTitle().isEmpty() ?
                                                    getString(R.string.note_deleted_detailed,
                                                            mNote.getTitle()) :
                                                    getString(R.string.note_deleted);
                                            Toast.makeText(getActivity(), message,
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                        if (!mIsTablet) { // PHONE
                                            getActivity().finish();
                                        } else { // TABLET
                                            onPause();
                                            // Reload with new Fragment
                                            getActivity().getSupportFragmentManager().beginTransaction()
                                                    .replace(R.id.activity_note_framelayout,
                                                            new NoteFragment(),
                                                            Constants.FRAGMENT_NOTE)
                                                    .commit();
                                        }
                                        break;
                                    case DialogInterface.BUTTON_NEGATIVE:
                                        break;
                                }
                            }
                        };
                AlertDialog.Builder deleteBuilder = new AlertDialog.Builder(getActivity());
                deleteBuilder.setTitle(mNote.isTrash() ? getString(R.string.confirm_deletion) :
                        getString(R.string.confirm_trash))
                        .setPositiveButton(getString(R.string.confirm), dialogClickListenerDelete)
                        .setNegativeButton(getString(R.string.abort), dialogClickListenerDelete)
                        .setMessage(mNote.isTrash ? getString(R.string.confirm_deletion_message) :
                                getString(R.string.confirm_trash_message))
                        .show();
                break;
            case R.id.action_restore:
                DialogInterface.OnClickListener dialogClickListenerRestore =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case DialogInterface.BUTTON_POSITIVE:
                                        mTrash = false;
                                        mRestore = true;
                                        if (!mIsTablet) { // PHONE
                                            getActivity().finish();
                                        } else { // TABLET
                                            onPause();
                                            // Reload Fragment
                                            getActivity().getSupportFragmentManager().beginTransaction()
                                                    .replace(R.id.activity_note_framelayout,
                                                            BaseActivity.getNoteFragment(mNote, false, null),
                                                            Constants.FRAGMENT_NOTE)
                                                    .commit();
                                        }
                                        String message = !mNote.getTitle().isEmpty() ?
                                                getString(R.string.note_restored_detailed,
                                                        mNote.getTitle()) :
                                                getString(R.string.note_restored);
                                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                                        break;
                                    case DialogInterface.BUTTON_NEGATIVE:
                                        break;
                                }
                            }
                        };
                AlertDialog.Builder restoreBuilder = new AlertDialog.Builder(getActivity());
                restoreBuilder.setTitle(getString(R.string.confirm_restore))
                        .setPositiveButton(getString(R.string.confirm), dialogClickListenerRestore)
                        .setNegativeButton(getString(R.string.abort), dialogClickListenerRestore)
                        .setMessage(getString(R.string.confirm_restore_message))
                        .show();
                break;
        }
        return false;
    }
}
