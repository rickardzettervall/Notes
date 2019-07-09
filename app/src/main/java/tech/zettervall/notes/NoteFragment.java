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

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Calendar;

import tech.zettervall.mNotes.R;
import tech.zettervall.mNotes.databinding.FragmentNoteBinding;
import tech.zettervall.notes.models.Note;
import tech.zettervall.notes.services.NotificationJobService;
import tech.zettervall.notes.utils.DateTimeHelper;
import tech.zettervall.notes.utils.Keyboard;
import tech.zettervall.notes.viewmodels.NoteViewModel;

import static android.content.Context.JOB_SCHEDULER_SERVICE;

/**
 * Fragment for editing a Note, used ViewModel to fetch data from db.
 */
public class NoteFragment extends Fragment {

    private static final String TAG = NoteFragment.class.getSimpleName();
    private static final String FAVORITE_STATUS = "favorite_status";
    private static final String REMINDER_STATUS = "reminder_status";
    private boolean mTrash, mDeleted, mFavoriteStatusChanged, mReminderChanged, mIsTablet;
    private long mReminderDateTimeEpoch;
    private FragmentNoteBinding mDataBinding;
    private NoteViewModel mNoteViewModel;
    private Note mNote;
    private Calendar mReminderCalender, mDateTimePickerCalender;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mDataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_note, container, false);
        View rootView = mDataBinding.getRoot();

        // Initialize ViewModel
        mNoteViewModel = ViewModelProviders.of(this).get(NoteViewModel.class);

        // Enable Toolbar MenuItem handling
        setHasOptionsMenu(true);

        // Get Note
        if (savedInstanceState != null) { // Existing Note but configuration changed
            mNote = Parcels.unwrap(savedInstanceState.getParcelable(Constants.NOTE));
        } else if (getArguments() != null) { // Clicked Note or new Note from Favorites Fragment
            if (getArguments().getBoolean(Constants.NOTE_FAVORITE)) {
                mNote = newNote(true);
            } else {
                mNote = Parcels.unwrap(getArguments().getParcelable(Constants.NOTE));
            }
        } else { // New Note
            mNote = newNote(false);
        }

        // Get Tablet bool
        mIsTablet = getResources().getBoolean(R.bool.isTablet);

        // Set GUI fields
        mDataBinding.titleTv.setText(mNote.getTitle());
        mDataBinding.textTv.setText(mNote.getText());
        mDataBinding.createdTv.setText(getString(R.string.creation_date,
                mNote.getCreationString(getActivity())));
        if (mNote.getModifiedEpoch() != -1) {
            mDataBinding.updatedTv.setText(getString(R.string.modified_date,
                    mNote.getModifiedString(getActivity())));
        } else {
            mDataBinding.updatedTv.setVisibility(View.GONE);
        }

        // Disable editing for trashed Notes
        if (mNote.isTrash()) {
            mDataBinding.titleTv.setEnabled(false);
            mDataBinding.textTv.setEnabled(false);
        }

        // Hide / Show FAB depending on device
        if (mIsTablet) {
            mDataBinding.fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveNote();
                    Toast.makeText(getActivity(), "Note saved", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            mDataBinding.fab.hide();
        }

        // Set title
        if (mNote.isTrash()) {
            getActivity().setTitle(R.string.note_trash);
            // Hide keyboard
            Keyboard.hideKeyboard(getActivity());
        }

        return rootView;
    }

    /**
     * Create new Note.
     *
     * @param isFavorite Set favorite on creation
     */
    private Note newNote(boolean isFavorite) {
        return new Note(mDataBinding.titleTv.getText().toString(),
                mDataBinding.textTv.getText().toString(),
                new ArrayList<String>(),
                DateTimeHelper.getCurrentEpoch(),
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
        if ((!mDataBinding.titleTv.getText().toString().isEmpty() ||
                !mDataBinding.textTv.getText().toString().isEmpty()) &&
                !mDataBinding.titleTv.getText().toString().equals(mNote.getTitle()) ||
                !mDataBinding.textTv.getText().toString().equals(mNote.getText()) ||
                mFavoriteStatusChanged || mReminderChanged) {
            // Change Note values and update modified time stamp
            mNote.setTitle(mDataBinding.titleTv.getText().toString());
            mNote.setText(mDataBinding.textTv.getText().toString());
            mNote.setModifiedEpoch(DateTimeHelper.getCurrentEpoch());
        }

        if (mNote.getId() > 0) { // Existing Note
            mNoteViewModel.updateNote(mNote);
        } else { // New Note
            mNote.setId((int) mNoteViewModel.insertNote(mNote));
        }
    }

    private void setFavoritizedIcon(MenuItem item) {
        item.setIcon(R.drawable.ic_star);
        item.setTitle(R.string.action_unfavoritize);
    }

    private void setUnfavoritizedIcon(MenuItem item) {
        item.setIcon(R.drawable.ic_star_border);
        item.setTitle(R.string.action_favoritize);
    }

    private void scheduleReminderJob(Context context) {
        // Set bundle
        PersistableBundle bundle = new PersistableBundle();
        if (mNote.getId() == 0) {
            saveNote();
        }
        bundle.putInt(Constants.NOTE_ID, mNote.getId());
        bundle.putString(Constants.NOTE_TITLE, mDataBinding.titleTv.getText().toString());
        bundle.putString(Constants.NOTE_TEXT, mDataBinding.textTv.getText().toString());

        // Get System Scheduler
        JobScheduler jobScheduler =
                (JobScheduler) context.getSystemService(JOB_SCHEDULER_SERVICE);

        // Build job
        ComponentName jobService = new ComponentName(context.getPackageName(),
                NotificationJobService.class.getName());
        JobInfo.Builder jobBuilder = new JobInfo.Builder(mNote.getId(), jobService)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setMinimumLatency(
                        Math.abs(DateTimeHelper.getCurrentEpoch() - mReminderDateTimeEpoch))
                .setPersisted(true)
                .setExtras(bundle);
        JobInfo jobInfo = jobBuilder.build();

        // Schedule job
        jobScheduler.schedule(jobInfo);
    }

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
                        mReminderDateTimeEpoch = DateTimeHelper.
                                getEpochWithZeroSeconds(mReminderCalender.getTime().getTime());
                        // Display for user
                        Toast.makeText(getActivity(), getString(R.string.reminder_set,
                                DateTimeHelper.getDateStringFromEpoch(mReminderDateTimeEpoch,
                                        getActivity())), Toast.LENGTH_LONG).show();
                        // Set Reminder for Note
                        mNote.setNotificationEpoch(mReminderDateTimeEpoch);
                        scheduleReminderJob(getActivity());
                        mReminderChanged = true;
                    }
                }, mDateTimePickerCalender.get(Calendar.HOUR_OF_DAY),
                        mDateTimePickerCalender.get(Calendar.MINUTE),
                        DateTimeHelper.use24h(getActivity())).show();
            }
        }, mDateTimePickerCalender.get(Calendar.YEAR),
                mDateTimePickerCalender.get(Calendar.MONTH),
                mDateTimePickerCalender.get(Calendar.DATE)).show();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (!mDeleted) {
            if (!mTrash) { // SAVE
                saveNote();
            } else if (mNote != null) { // TRASH
                mNote.setTrash(true);
                mNoteViewModel.updateNote(mNote);
                // Message to user
                String toastMessage = mNote.getTitle() != null && !mNote.getTitle().isEmpty() ?
                        getString(R.string.note_trashed_detailed, mNote.getTitle()) :
                        getString(R.string.note_trashed);
                Toast.makeText(getActivity(), toastMessage, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelable(Constants.NOTE, Parcels.wrap(mNote));
        outState.putBoolean(FAVORITE_STATUS, mFavoriteStatusChanged);
        outState.putBoolean(REMINDER_STATUS, mReminderChanged);
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
                mFavoriteStatusChanged = true; // Used to determine if to save
                break;
            case R.id.action_reminder:
                // todo: set date/time
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
                                            mNoteViewModel.deleteNote(mNote);
                                            mDeleted = true;
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
                                            // TODO: What happens for tablet users?
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
                                        if (!mIsTablet) { // PHONE
                                            getActivity().finish();
                                        } else { // TABLET
                                            // TODO: What happens for tablet users?
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
