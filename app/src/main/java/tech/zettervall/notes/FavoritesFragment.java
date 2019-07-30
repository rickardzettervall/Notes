package tech.zettervall.notes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.paging.PagedList;
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
        mRecyclerView = rootView.findViewById(R.id.notes_list_rv);
        mFab = rootView.findViewById(R.id.notes_list_fab);
        emptyTextView = rootView.findViewById(R.id.notes_list_is_empty_tv);

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

        // Set FAB OnClickListener
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onFragmentFabClick(true);
            }
        });

        // Set title
        getActivity().setTitle(R.string.action_favorites);

        // Subscribe Observers
        subscribeObservers();

        return rootView;
    }

    @Override
    public void subscribeObservers() {
        mFavoritesFragmentViewModel.getFavorites().observe(getViewLifecycleOwner(), new Observer<PagedList<Note>>() {
            @Override
            public void onChanged(PagedList<Note> notes) {
                mNoteAdapter.submitList(notes);
                emptyTextView.setVisibility(notes.isEmpty() ? View.VISIBLE : View.GONE);
            }
        });
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
