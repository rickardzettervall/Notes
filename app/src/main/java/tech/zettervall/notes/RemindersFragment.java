package tech.zettervall.notes;

import android.graphics.Canvas;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import tech.zettervall.mNotes.R;
import tech.zettervall.notes.adapters.NoteAdapter;
import tech.zettervall.notes.models.Note;
import tech.zettervall.notes.utils.RecyclerViewUtil;
import tech.zettervall.notes.viewmodels.RemindersFragmentViewModel;

public class RemindersFragment extends BaseListFragment {

    private static final String TAG = RemindersFragment.class.getSimpleName();
    private RemindersFragmentViewModel mRemindersFragmentViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_notelist, container, false);

        // Initialize ViewModel
        mRemindersFragmentViewModel = ViewModelProviders.of(this).get(RemindersFragmentViewModel.class);

        // Find Views
        mRecyclerView = rootView.findViewById(R.id.fragment_notelist_recyclerview);
        mFab = rootView.findViewById(R.id.fragment_notelist_fab);
        emptyTextView = rootView.findViewById(R.id.fragment_notelist_is_empty_textview);
        mRootView = rootView.findViewById(R.id.fragment_notelist_root);

        // Set Adapter / LayoutManager / Decoration
        mNoteAdapter = new NoteAdapter(this);
        mLayoutManager = RecyclerViewUtil.getDefaultLinearLayoutManager(getActivity());
        mRecyclerView.setAdapter(mNoteAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);
        RecyclerViewUtil.setRecyclerViewDecoration(mLayoutManager, mRecyclerView);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    mFab.hide();
                } else {
                    mFab.show();
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        mItemToucherHelperCallback =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                        try {
                            Note note = mNoteAdapter.getCurrentList().get(viewHolder.getAdapterPosition());
                            note.setTrash(true);
                            mRemindersFragmentViewModel.updateNote(note);

                            String message = note.getTitle() != null && !note.getTitle().isEmpty() ?
                                    getString(R.string.note_trashed_detailed, note.getTitle()) :
                                    getString(R.string.note_trashed);

                            Snackbar.make(mRootView, message, Snackbar.LENGTH_LONG)
                                    .setAction(getString(R.string.undo), (View v) -> {
                                        note.setTrash(false);
                                        mRemindersFragmentViewModel.updateNote(note);
                                    }).show();
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                        BaseListFragment.drawChildCanvas(getActivity(), viewHolder, c, actionState, dX, false);
                        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                    }
                };
        new ItemTouchHelper(mItemToucherHelperCallback).attachToRecyclerView(mRecyclerView);

        // Set FAB OnClickListener
        mFab.setOnClickListener(super::fabClick);

        // Set title
        getActivity().setTitle(R.string.action_reminders);

        // Subscribe Observers
        subscribeObservers();

        return rootView;
    }

    @Override
    public void subscribeObservers() {
        super.subscribeObservers();
        mRemindersFragmentViewModel.getReminders().observe(getViewLifecycleOwner(), super::updateAdapter);
    }

    @Override
    public void refreshObservers(@Nullable String query) {
        mRemindersFragmentViewModel.getReminders().removeObservers(getViewLifecycleOwner());
        mRemindersFragmentViewModel.setNotes(query);
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
}
