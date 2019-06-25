package tech.zettervall.notes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import tech.zettervall.mNotes.R;
import tech.zettervall.notes.adapters.NoteAdapter;
import tech.zettervall.notes.models.Note;
import tech.zettervall.notes.utils.RecyclerViewHelper;
import tech.zettervall.notes.viewmodels.FavoritesViewModel;

public class FavoritesFragment extends BaseListFragment {

    private static final String TAG = FavoritesFragment.class.getSimpleName();
    private FavoritesViewModel mFavoritesViewModel;
    private LinearLayoutManager mLayoutManager;
    private NoteAdapter mNoteAdapter;
    private RecyclerView mRecyclerView;
    private FloatingActionButton mFab;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_notelist, container, false);

        // Initialize ViewModel
        mFavoritesViewModel = ViewModelProviders.of(this).get(FavoritesViewModel.class);

        // Find Views
        mRecyclerView = rootView.findViewById(R.id.notes_list_rv);
        mFab = rootView.findViewById(R.id.notes_list_fab);

        // Set Adapter / LayoutManager / Decoration
        mNoteAdapter = new NoteAdapter(this, getActivity());
        mLayoutManager = RecyclerViewHelper.getDefaultLinearLayoutManager(getActivity());
        mRecyclerView.setAdapter(mNoteAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);
        RecyclerViewHelper.setRecyclerViewDecoration(mLayoutManager, mRecyclerView);
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

        // Set FAB OnClickListener
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onNoteListFragmentFabClick(true);
            }
        });

        // Set title
        getActivity().setTitle(R.string.action_favorites);

        // Subscribe Observers
        subscribeObservers();

        return rootView;
    }

    /**
     * Subscribe Observers.
     */
    @Override
    public void subscribeObservers() {
        mFavoritesViewModel.getFavorites().observe(this, new Observer<PagedList<Note>>() {
            @Override
            public void onChanged(PagedList<Note> notes) {
                mNoteAdapter.submitList(notes);
            }
        });
    }

    /**
     * Reload Observers, primarily for when user changes sorting.
     */
    @Override
    public void refreshObservers(@Nullable String query) {
        mFavoritesViewModel.getFavorites().removeObservers(getViewLifecycleOwner());
        mFavoritesViewModel.setNotes(query);
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
