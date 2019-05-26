package tech.zettervall.notes;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import tech.zettervall.notes.viewmodels.NotesViewModel;

public class NoteListFragment extends Fragment implements NoteAdapter.OnNoteClickListener {

    private static final String TAG = NoteListFragment.class.getSimpleName();
    private NoteListFragmentClickListener callback;
    private NotesViewModel mNotesViewModel;
    private LinearLayoutManager mLayoutManager;
    private NoteAdapter mNoteAdapter;
    private RecyclerView mRecyclerView;
    private FloatingActionButton mFab;
    private boolean mIsTablet;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_notelist, container, false);

        // Initialize ViewModel
        mNotesViewModel = ViewModelProviders.of(this).get(NotesViewModel.class);

        // Retrieve saved fields
        mIsTablet = getResources().getBoolean(R.bool.isTablet);

        // Find Views
        mRecyclerView = rootView.findViewById(R.id.notes_list_rv);
        mFab = rootView.findViewById(R.id.notes_list_fab);

        // Set Adapter / LayoutManager / Decoration
        mNoteAdapter = new NoteAdapter(this);
        mLayoutManager = RecyclerViewHelper.getDefaultLinearLayoutManager(getActivity());
        mRecyclerView.setAdapter(mNoteAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);
        RecyclerViewHelper.setRecyclerViewDecoration(mLayoutManager, mRecyclerView);
        if(!mIsTablet) { // PHONE
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
        mNotesViewModel.getNotes().observe(this, new Observer<PagedList<Note>>() {
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
        void onNoteClick(int _id);

        void onNoteListFragmentFabClick();
    }

    @Override
    public void onNoteClick(int index) {
        try {
            // Get Note ID
            int id = mNoteAdapter.getCurrentList().get(index).get_id();
            // Send index to callback interface
            callback.onNoteClick(id);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
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
