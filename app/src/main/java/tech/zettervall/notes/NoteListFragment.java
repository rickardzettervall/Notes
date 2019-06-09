package tech.zettervall.notes;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
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
import tech.zettervall.notes.viewmodels.NoteListViewModel;

public class NoteListFragment extends Fragment implements NoteAdapter.OnNoteClickListener {

    private static final String TAG = NoteListFragment.class.getSimpleName();
    private NoteListFragmentClickListener callback;
    private NoteListViewModel mNoteListViewModel;
    private LinearLayoutManager mLayoutManager;
    private NoteAdapter mNoteAdapter;
    private RecyclerView mRecyclerView;
    private FloatingActionButton mFab;
    private boolean mIsTablet;

    // Used for SearchView to restore state on configuration changes
    private boolean mSearchIconified;
    private String mSearchQuery;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_notelist, container, false);

        // Initialize ViewModel
        mNoteListViewModel = ViewModelProviders.of(this).get(NoteListViewModel.class);

        // Enable Toolbar MenuItem handling
        setHasOptionsMenu(true);

        // Retrieve saved fields
        mIsTablet = getResources().getBoolean(R.bool.isTablet);
        if (savedInstanceState != null) {
            mSearchQuery = savedInstanceState.getString(Constants.SEARCH_QUERY);
            mSearchIconified = savedInstanceState.getBoolean(Constants.SEARCH_ICONIFIED);
        }

        // Find Views
        mRecyclerView = rootView.findViewById(R.id.notes_list_rv);
        mFab = rootView.findViewById(R.id.notes_list_fab);

        // Set Adapter / LayoutManager / Decoration
        mNoteAdapter = new NoteAdapter(this, getActivity());
        mLayoutManager = RecyclerViewHelper.getDefaultLinearLayoutManager(getActivity());
        mRecyclerView.setAdapter(mNoteAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);
        RecyclerViewHelper.setRecyclerViewDecoration(mLayoutManager, mRecyclerView);
        if (!mIsTablet) { // PHONE
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
        } else { // TABLET
            mFab.hide();
        }

        // Set FAB OnClickListener
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onNoteListFragmentFabClick();
            }
        });

        // Subscribe Observers
        subscribeObservers();

        return rootView;
    }

    /**
     * Subscribe Observers.
     */
    private void subscribeObservers() {
        mNoteListViewModel.getNotes().observe(this, new Observer<PagedList<Note>>() {
            @Override
            public void onChanged(PagedList<Note> notes) {
                mNoteAdapter.submitList(notes);
            }
        });
    }

    /**
     * Callback interface for sending data back to Activity.
     */
    public interface NoteListFragmentClickListener {
        void onNoteClick(Note note);

        void onNoteListFragmentFabClick();
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
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString(Constants.SEARCH_QUERY, mSearchQuery);
        outState.putBoolean(Constants.SEARCH_ICONIFIED, mSearchIconified);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        // Get SearchView
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        if (mSearchQuery != null && !mSearchIconified) {
            searchView.setIconified(false);
            searchView.setQuery(mSearchQuery, false);
        }

        // Set Query
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            private void setResults(String query) {
                mSearchQuery = query;
                mSearchIconified = false;
                mNoteListViewModel.getNotes().removeObservers(getViewLifecycleOwner());
                mNoteListViewModel.setNotesSearch(query);
                subscribeObservers();
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                setResults(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                setResults(newText);
                return false;
            }
        });

        // Set Close Behaviour
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                mSearchIconified = true;
                // Restore List to all Notes
                mNoteListViewModel.getNotes().removeObservers(getViewLifecycleOwner());
                mNoteListViewModel.setNotes();
                subscribeObservers();
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sort:
                // TODO: sort
                break;
        }
        return false;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Force Activity to implement callback interface
        try {
            callback = (NoteListFragmentClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    " must implement 'NoteListFragmentClickListener'");
        }
    }
}
