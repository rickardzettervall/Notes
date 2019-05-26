package tech.zettervall.notes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import tech.zettervall.mNotes.R;
import tech.zettervall.mNotes.databinding.FragmentNoteBinding;
import tech.zettervall.notes.models.Note;
import tech.zettervall.notes.utils.DateTimeHelper;
import tech.zettervall.notes.viewmodels.NoteViewModel;

/**
 * Fragment for editing a Note, used ViewModel to fetch data from db.
 */
public class NoteFragment extends Fragment {

    private static final String TAG = NoteFragment.class.getSimpleName();
    private FragmentNoteBinding mDataBinding;
    private NoteViewModel mNoteViewModel;
    private Integer mNoteID;
    private Note mNote;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mDataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_note, container, false);
        View rootView = mDataBinding.getRoot();

        // Initialize ViewModel
        mNoteViewModel = ViewModelProviders.of(this).get(NoteViewModel.class);

        // Get Arguments / SavedState
        if (getArguments() != null) {
            mNoteID = getArguments().getInt(Constants.NOTE_ID);
        } else if (savedInstanceState != null) {
            mNoteID = savedInstanceState.getInt(Constants.NOTE_ID);
        }

        // Set Note if ID exists
        if (mNoteID != null) {
            mNoteViewModel.setNote(mNoteID);
            subscribeObservers();
        }

        return rootView;
    }

    private void subscribeObservers() {
        mNoteViewModel.getNote().observe(this, new Observer<Note>() {
            @Override
            public void onChanged(Note note) {
                if (note != null) {
                    if (note.getHeadline() != null) {
                        mDataBinding.headlineTv.setText(note.getHeadline());
                    }
                    if (note.getText() != null) {
                        mDataBinding.textTv.setText(note.getText());
                    }
                }

                /* Set private Note Object to use for editing and
                 * saving changes to the db. */
                mNote = note;
            }
        });
    }

    /**
     * Save Note, but only if the user actually entered a
     * headline or text, or if a previous Note was changed.
     */
    private void saveNote() {
        if (!mDataBinding.headlineTv.getText().toString().isEmpty() ||
                !mDataBinding.textTv.getText().toString().isEmpty()) {
            if (mNote == null) {
                // Create new Note
                mNote = new Note(Constants.TYPE_PLAIN,
                        mDataBinding.headlineTv.getText().toString(),
                        mDataBinding.textTv.getText().toString());
                mNoteID = (int) mNoteViewModel.insertNote(mNote);
            } else if (!mNote.getHeadline().equals(mDataBinding.headlineTv.getText().toString()) ||
                    !mNote.getText().equals(mDataBinding.textTv.getText().toString())) {
                // Update existing Note
                mNote.setHeadline(mDataBinding.headlineTv.getText().toString());
                mNote.setText(mDataBinding.textTv.getText().toString());
                mNote.setDate(DateTimeHelper.getCurrentDateTime());
                mNoteID = (int) mNoteViewModel.insertNote(mNote);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        saveNote();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if (mNoteID != null) {
            outState.putInt(Constants.NOTE_ID, mNoteID);
        }
        super.onSaveInstanceState(outState);
    }
}
