package tech.zettervall.notes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import java.util.ArrayList;

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
    private boolean isFavorite;

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
                    if (note.getTitle() != null) {
                        mDataBinding.titleTv.setText(note.getTitle());
                    }
                    if (note.getText() != null) {
                        mDataBinding.textTv.setText(note.getText());
                    }
                    mDataBinding.createdTv.setText(getString(R.string.creation_date,
                            DateTimeHelper.getDateStringFromEpoch(note.getCreationEpoch())));
                    mDataBinding.updatedTv.setText(getString(R.string.modified_date,
                            DateTimeHelper.getDateStringFromEpoch(note.getModifiedEpoch())));
                }

                /* Set private Note Object to use for editing and
                 * saving changes to the db. */
                mNote = note;
            }
        });
    }

    /**
     * Save Note, but only if the user actually entered a
     * title or text, or if a previous Note was changed.
     */
    private void saveNote() { // TODO: set tags, favorite and notification based on user choices
        if (!mDataBinding.titleTv.getText().toString().isEmpty() ||
                !mDataBinding.textTv.getText().toString().isEmpty()) {
            if (mNote == null) {
                // Create new Note
                mNote = new Note(mDataBinding.titleTv.getText().toString(),
                        mDataBinding.textTv.getText().toString(),
                        new ArrayList<String>(),
                        DateTimeHelper.getCurrentEpoch(),
                        DateTimeHelper.getCurrentEpoch(),
                        -1,
                        false,
                        false);
                mNoteID = (int) mNoteViewModel.insertNote(mNote);
            } else if (!mNote.getTitle().equals(mDataBinding.titleTv.getText().toString()) ||
                    !mNote.getText().equals(mDataBinding.textTv.getText().toString())) {
                // Update existing Note
                mNote.setTitle(mDataBinding.titleTv.getText().toString());
                mNote.setText(mDataBinding.textTv.getText().toString());
                mNote.setModifiedEpoch(DateTimeHelper.getCurrentEpoch());
                mNoteID = (int) mNoteViewModel.insertNote(mNote);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (!Constants.deleteNote) { // SAVE
            saveNote();
        } else if(mNote != null) { // DELETE
            Constants.deleteNote = false;
            mNote.setTrash(true);
            mNoteViewModel.insertNote(mNote);
            Toast.makeText(getActivity(), "Note deleted", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if (mNoteID != null) {
            outState.putInt(Constants.NOTE_ID, mNoteID);
        }
        super.onSaveInstanceState(outState);
    }
}
