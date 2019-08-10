package tech.zettervall.notes;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.PersistableBundle;
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
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.parceler.Parcels;

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
import tech.zettervall.notes.utils.DateTimeUtil;
import tech.zettervall.notes.utils.KeyboardUtil;
import tech.zettervall.notes.utils.RecyclerViewUtil;
import tech.zettervall.notes.viewmodels.NoteFragmentViewModel;

import static android.content.Context.JOB_SCHEDULER_SERVICE;

/**
 * Fragment for editing a Note, uses ViewModel to fetch data from db.
 */
public class NoteFragment extends Fragment implements TagSelectAdapter.OnTagClickListener {

    private static final String TAG = NoteFragment.class.getSimpleName();
    private boolean mTrash, mIsTrash, mFinalDeletion, mRestore, mIsTablet;
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
        mDataBinding.fragmentNoteTitleTextview.setText(mNote.getTitle());
        mDataBinding.fragmentNoteTextTextview.setText(mNote.getText());
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
            mDataBinding.fragmentNoteTitleTextview.setEnabled(false);
            mDataBinding.fragmentNoteTextTextview.setEnabled(false);
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

        // Set title / trashed state
        if (!mIsTablet && mNote.isTrash()) {
            mIsTrash = true;
            getActivity().setTitle(R.string.note_trash);
            // Hide keyboard
            KeyboardUtil.hideKeyboard(getActivity());
        }

        // Set focus on text field if user has set title
        if (!mNote.getTitle().isEmpty()) {
            mDataBinding.fragmentNoteTextTextview.requestFocus();
        }

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
            mDataBinding.fragmentNoteReminderRemoveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDataBinding.fragmentNoteReminderLinearlayout.setVisibility(View.GONE);
                    cancelReminderJob();
                    Toast.makeText(context, getString(R.string.reminder_removed),
                            Toast.LENGTH_SHORT).show();
                }
            });
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
        return new Note(mDataBinding.fragmentNoteTitleTextview.getText().toString(),
                mDataBinding.fragmentNoteTextTextview.getText().toString(),
                tagIDs,
                DateTimeUtil.getCurrentEpoch(),
                -1,
                -1,
                false,
                isFavorite);
    }

    /**
     * Save Note, but only if the user actually entered a
     * title/text or change other parameters.
     */
    private void saveNote() {
        mNote.setTrash(mTrash);
        if (!mDataBinding.fragmentNoteTitleTextview.getText().toString().equals(mNote.getTitle()) ||
                !mDataBinding.fragmentNoteTextTextview.getText().toString().equals(mNote.getText())) {
            // Change Note title/text and update modified time stamp
            mNote.setTitle(mDataBinding.fragmentNoteTitleTextview.getText().toString());
            mNote.setText(mDataBinding.fragmentNoteTextTextview.getText().toString());
            mNote.setModifiedEpoch(DateTimeUtil.getCurrentEpoch());
        }

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

    /**
     * Save / Trash / Restore Note.
     */
    @Override
    public void onPause() {
        super.onPause();
        if (!mFinalDeletion && !mIsTrash) {
            if (!mTrash) { // SAVE
                saveNote();
            } else if (mNote != null) { // TRASH
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
        } else if (mIsTrash && mRestore) { // Restore trashed Note
            mNote.setTrash(false);
            mNoteFragmentViewModel.updateNote(mNote);
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
     * CLick event for Tag Adapter within AlertDialog for selecting Tags.
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
     * Update Tags TextView.
     */
    private void updateTagsUi() {
        StringBuilder tagsString = new StringBuilder();
        if (!mNote.getTagIDs().isEmpty()) {
            mDataBinding.fragmentNoteTagsTextview.setVisibility(View.VISIBLE);
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
        } else {
            mDataBinding.fragmentNoteTagsTextview.setVisibility(View.GONE);
        }
        mDataBinding.fragmentNoteTagsTextview.setText(tagsString.toString());
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

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(getString(R.string.action_tags));
                builder.setView(dialogView);
                builder.setPositiveButton(R.string.confirm_done, null);
                builder.show();
                break;
            case R.id.action_reminder:
                dateTimePicker();
                break;
            case R.id.action_delete:
                DialogInterface.OnClickListener dialogClickListenerDelete =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case DialogInterface.BUTTON_POSITIVE:
                                        if (!mNote.isTrash()) {
                                            mTrash = true;
                                        } else { // TRASHED (final deletion)
                                            mNoteFragmentViewModel.deleteNote(mNote);
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
                deleteBuilder.setTitle(getString(R.string.confirm_deletion))
                        .setPositiveButton(getString(R.string.confirm), dialogClickListenerDelete)
                        .setNegativeButton(getString(R.string.abort), dialogClickListenerDelete);
                if (mNote.isTrash()) {
                    deleteBuilder.setMessage(getString(R.string.confirm_deletion_message));
                }
                deleteBuilder.show();
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
                        .setNegativeButton(getString(R.string.abort), dialogClickListenerRestore);
                restoreBuilder.show();
                break;
        }
        return false;
    }
}
