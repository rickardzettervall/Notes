package tech.zettervall.notes;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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
    private MenuItem mFavoritizeMenuItem;
    private boolean mFavorite, mTrash;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mDataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_note, container, false);
        View rootView = mDataBinding.getRoot();

        // Initialize ViewModel
        mNoteViewModel = ViewModelProviders.of(this).get(NoteViewModel.class);

        // Enable Toolbar MenuItems handling
        setHasOptionsMenu(true);

        // Get Arguments
        if (getArguments() != null) {
            mNoteID = getArguments().getInt(Constants.NOTE_ID);
        }

        // Get SavedState
        if (savedInstanceState != null) {
            mFavorite = savedInstanceState.getBoolean(Constants.NOTE_IS_FAVORITE);
            mNoteID = savedInstanceState.getInt(Constants.NOTE_ID);
        }

        // TODO: FOR TESTING
        if(mNoteID != null) Toast.makeText(getActivity(), mNoteID.toString(), Toast.LENGTH_SHORT).show();

        if (mNoteID != null) {
            mNoteViewModel.setNote(mNoteID);
            subscribeObservers();
        }

        // Hide / Show FAB depending on device
        if (getResources().getBoolean(R.bool.isTablet)) {
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

                    if (note.isFavorite()) {
                        mFavorite = true;
                        setFavoritizedIcon(mFavoritizeMenuItem);
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
                        mFavorite);
                mNoteID = (int) mNoteViewModel.insertNote(mNote);
            } else {
                // Update existing Note
                mNote.setTitle(mDataBinding.titleTv.getText().toString());
                mNote.setText(mDataBinding.textTv.getText().toString());
                mNote.setModifiedEpoch(DateTimeHelper.getCurrentEpoch());
                mNote.setFavorite(mFavorite);
                mNoteID = (int) mNoteViewModel.insertNote(mNote);
            }
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

    @Override
    public void onPause() {
        super.onPause();
        if (!mTrash) { // SAVE
            saveNote();
        } else if (mNote != null) { // TRASH
            mNote.setTrash(true);
            mNoteViewModel.insertNote(mNote);
            String toastMessage = mNote.getTitle() != null && !mNote.getTitle().isEmpty() ?
                    getString(R.string.note_trashed_detailed, mNote.getTitle()) :
                    getString(R.string.note_trashed);
            Toast.makeText(getActivity(), toastMessage, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if (mNoteID != null) {
            outState.putInt(Constants.NOTE_ID, mNoteID);
        }
        outState.putBoolean(Constants.NOTE_IS_FAVORITE, mFavorite);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        mFavoritizeMenuItem = menu.findItem(R.id.action_favoritize);
        /* Set Note if ID exists.
         * Subscribe to Observers here instead of in onCreate
         * because MenuItem must be loaded first for it to be changed
         * within the Observer. */
//        if (mNoteID != null) {
//            mNoteViewModel.setNote(mNoteID);
//            subscribeObservers();
//        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_favoritize:
                if (mFavorite) { // Note is in favorites
                    mFavorite = false;
                    setUnfavoritizedIcon(item);

                } else { // Note is not in favorites
                    mFavorite = true;
                    setFavoritizedIcon(item);
                }
                break;
            case R.id.action_delete:
                DialogInterface.OnClickListener dialogClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case DialogInterface.BUTTON_POSITIVE:
                                        mTrash = true;
                                        getActivity().finish();
                                        break;
                                    case DialogInterface.BUTTON_NEGATIVE:
                                        break;
                                }
                            }
                        };
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(getString(R.string.confirm_deletion))
                        .setPositiveButton(getString(R.string.confirm), dialogClickListener)
                        .setNegativeButton(getString(R.string.abort), dialogClickListener).show();
                break;
        }
        return false;
    }
}
