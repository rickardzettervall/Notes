package tech.zettervall.notes;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
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
import androidx.lifecycle.ViewModelProviders;

import org.parceler.Parcels;

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
    private static final String FAVORITE_STATUS = "favorite_status";
    private FragmentNoteBinding mDataBinding;
    private NoteViewModel mNoteViewModel;
    private Note mNote;
    private boolean mTrash, mFavoriteStatusChanged, mIsTablet;

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
            if(getArguments().getBoolean(Constants.NOTE_FAVORITE)) {
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
        if ((!mDataBinding.titleTv.getText().toString().isEmpty() ||
                !mDataBinding.textTv.getText().toString().isEmpty()) &&
                !mDataBinding.titleTv.getText().toString().equals(mNote.getTitle()) ||
                !mDataBinding.textTv.getText().toString().equals(mNote.getText()) ||
                mFavoriteStatusChanged) {
            // Change Note values and update modified time stamp
            mNote.setTitle(mDataBinding.titleTv.getText().toString());
            mNote.setText(mDataBinding.textTv.getText().toString());
            mNote.setModifiedEpoch(DateTimeHelper.getCurrentEpoch());
            if (mNote.getId() > 0) { // Existing Note
                mNoteViewModel.updateNote(mNote);
            } else { // New Note
                mNote.setId((int) mNoteViewModel.insertNote(mNote));
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
            mNoteViewModel.updateNote(mNote);
            // Message to user
            String toastMessage = mNote.getTitle() != null && !mNote.getTitle().isEmpty() ?
                    getString(R.string.note_trashed_detailed, mNote.getTitle()) :
                    getString(R.string.note_trashed);
            Toast.makeText(getActivity(), toastMessage, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelable(Constants.NOTE, Parcels.wrap(mNote));
        outState.putBoolean(FAVORITE_STATUS, mFavoriteStatusChanged);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (mNote != null && mNote.isFavorite()) {
            MenuItem favoritize = menu.findItem(R.id.action_favoritize);
            setFavoritizedIcon(favoritize);
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
            case R.id.action_delete:
                DialogInterface.OnClickListener dialogClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case DialogInterface.BUTTON_POSITIVE:
                                        mTrash = true;
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
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(getString(R.string.confirm_deletion))
                        .setPositiveButton(getString(R.string.confirm), dialogClickListener)
                        .setNegativeButton(getString(R.string.abort), dialogClickListener).show();
                break;
        }
        return false;
    }
}
