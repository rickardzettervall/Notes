package tech.zettervall.notes;

import android.content.DialogInterface;
import android.graphics.Canvas;
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
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import tech.zettervall.mNotes.R;
import tech.zettervall.notes.adapters.NoteAdapter;
import tech.zettervall.notes.models.Note;
import tech.zettervall.notes.utils.RecyclerViewUtil;
import tech.zettervall.notes.viewmodels.TrashFragmentViewModel;

public class TrashFragment extends BaseListFragment {

    private static final String TAG = TrashFragment.class.getSimpleName();
    private TrashFragmentViewModel mTrashFragmentViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_notelist, container, false);

        // Initialize ViewModel
        mTrashFragmentViewModel = ViewModelProviders.of(this).get(TrashFragmentViewModel.class);

        // Find Views
        mRecyclerView = rootView.findViewById(R.id.fragment_notelist_recyclerview);
        mFab = rootView.findViewById(R.id.fragment_notelist_fab);
        emptyTextView = rootView.findViewById(R.id.fragment_notelist_is_empty_textview);
        emptyTextView.setText(R.string.trash_is_empty);
        mRootView = rootView.findViewById(R.id.fragment_notelist_root);

        // Set Adapter / LayoutManager / Decoration
        mNoteAdapter = new NoteAdapter(this);
        mLayoutManager = RecyclerViewUtil.getDefaultLinearLayoutManager(getActivity());
        mRecyclerView.setAdapter(mNoteAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);
        RecyclerViewUtil.setRecyclerViewDecoration(mLayoutManager, mRecyclerView);
        mItemToucherHelperCallback =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                        Note note = mNoteAdapter.getCurrentList().get(viewHolder.getAdapterPosition());
                        switch (direction) {
                            case ItemTouchHelper.LEFT: // Delete
                                mTrashFragmentViewModel.deleteNote(note);
                                String deletedMessage = note.getTitle() != null && !note.getTitle().isEmpty() ?
                                        getString(R.string.note_deleted_detailed, note.getTitle()) :
                                        getString(R.string.note_deleted);

                                Snackbar.make(mRootView, deletedMessage, Snackbar.LENGTH_LONG)
                                        .setAction(getString(R.string.undo), (View v) ->
                                                mTrashFragmentViewModel.insertNote(note)
                                        ).show();
                                break;
                            case ItemTouchHelper.RIGHT: // Restore
                                note.setTrash(false);
                                mTrashFragmentViewModel.updateNote(note);
                                String restoredMessage = note.getTitle() != null && !note.getTitle().isEmpty() ?
                                        getString(R.string.note_restored_detailed, note.getTitle()) :
                                        getString(R.string.note_restored);

                                Snackbar.make(mRootView, restoredMessage, Snackbar.LENGTH_LONG)
                                        .setAction(getString(R.string.undo), (View v) -> {
                                            note.setTrash(true);
                                            mTrashFragmentViewModel.updateNote(note);
                                        }).show();
                                break;
                        }
                    }

                    @Override
                    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                        BaseListFragment.drawChildCanvas(getActivity(), viewHolder, c, actionState, dX, true);
                        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                    }
                };
        new ItemTouchHelper(mItemToucherHelperCallback).attachToRecyclerView(mRecyclerView);

        // Hide FAB
        mFab.hide();

        // Set title
        getActivity().setTitle(getString(R.string.action_trash));

        // Subscribe Observers
        subscribeObservers();

        return rootView;
    }

    @Override
    public void subscribeObservers() {
        mTrashFragmentViewModel.getTrash().observe(getViewLifecycleOwner(), super::updateAdapter);
    }

    @Override
    public void refreshObservers(@Nullable String query) {
        mTrashFragmentViewModel.getTrash().removeObservers(getViewLifecycleOwner());
        mTrashFragmentViewModel.setNotes(query);
        subscribeObservers();
    }

    @Override
    public void onNoteClick(int index) {
        try {
            // Get Note
            Note note = mNoteAdapter.getCurrentList().get(index);
            // Send Note to callback interface
            callback.onNoteClick(note);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_trash, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        DialogInterface.OnClickListener dialogClickListener;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        switch (item.getItemId()) {
            case R.id.action_empty_trash:
                dialogClickListener = this::emptyTrashDialogClickListener;
                builder.setTitle(getString(R.string.confirm_empty_trash))
                        .setPositiveButton(getString(R.string.accept), dialogClickListener)
                        .setNegativeButton(getString(R.string.cancel), dialogClickListener)
                        .setMessage(getString(R.string.confirm_empty_trash_message))
                        .show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * ClickListener for Dialog confirmation to empty trash.
     */
    private void emptyTrashDialogClickListener(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
                mTrashFragmentViewModel.emptyTrash();
                Toast.makeText(getActivity(),
                        getString(R.string.deleted_all_notes),
                        Toast.LENGTH_SHORT).show();
                break;
            case DialogInterface.BUTTON_NEGATIVE:
                break;
        }
    }
}
