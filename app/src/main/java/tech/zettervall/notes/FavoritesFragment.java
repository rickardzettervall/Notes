package tech.zettervall.notes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import tech.zettervall.mNotes.R;
import tech.zettervall.notes.adapters.NoteAdapter;
import tech.zettervall.notes.models.Note;
import tech.zettervall.notes.utils.RecyclerViewUtil;
import tech.zettervall.notes.viewmodels.FavoritesFragmentViewModel;

public class FavoritesFragment extends BaseListFragment {

    private static final String TAG = FavoritesFragment.class.getSimpleName();
    private FavoritesFragmentViewModel mFavoritesFragmentViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_notelist, container, false);

        // Initialize ViewModel
        mFavoritesFragmentViewModel = ViewModelProviders.of(this).get(FavoritesFragmentViewModel.class);

        // Find Views
        mRecyclerView = rootView.findViewById(R.id.fragment_notelist_recyclerview);
        mFab = rootView.findViewById(R.id.fragment_notelist_fab);
        emptyTextView = rootView.findViewById(R.id.fragment_notelist_is_empty_textview);

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
                            mFavoritesFragmentViewModel.updateNote(note);
                            String toastMessage = note.getTitle() != null && !note.getTitle().isEmpty() ?
                                    getString(R.string.note_trashed_detailed, note.getTitle()) :
                                    getString(R.string.note_trashed);
                            Toast.makeText(getActivity(), toastMessage, Toast.LENGTH_SHORT).show();
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                };
        new ItemTouchHelper(mItemToucherHelperCallback).attachToRecyclerView(mRecyclerView);

        // Set FAB OnClickListener
        mFab.setOnClickListener(this::fabClick);

        // Set title
        getActivity().setTitle(R.string.action_favorites);

        // Subscribe Observers
        subscribeObservers();

        return rootView;
    }

    @Override
    protected void fabClick(View v) {
        callback.onFragmentFabClick(true, null);
    }

    @Override
    public void subscribeObservers() {
        mFavoritesFragmentViewModel.getFavorites().observe(getViewLifecycleOwner(), super::updateAdapter);
    }

    @Override
    public void refreshObservers(@Nullable String query) {
        mFavoritesFragmentViewModel.getFavorites().removeObservers(getViewLifecycleOwner());
        mFavoritesFragmentViewModel.setNotes(query);
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
