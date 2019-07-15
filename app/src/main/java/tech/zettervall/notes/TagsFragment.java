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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import tech.zettervall.mNotes.R;
import tech.zettervall.notes.adapters.NoteAdapter;
import tech.zettervall.notes.adapters.TagAdapter;
import tech.zettervall.notes.models.Note;
import tech.zettervall.notes.models.Tag;
import tech.zettervall.notes.utils.RecyclerViewUtil;
import tech.zettervall.notes.viewmodels.TagListViewModel;

public class TagsFragment extends BaseListFragment implements TagAdapter.OnTagClickListener {

    private static final String TAG = TagsFragment.class.getSimpleName();
    private TagListViewModel mTagListViewModel;
    private LinearLayoutManager mLayoutManager;
    private TagAdapter mTagAdapter;
    private RecyclerView mRecyclerView;
    private FloatingActionButton mFab;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_taglist, container, false);

        // Initialize ViewModel
        mTagListViewModel = ViewModelProviders.of(this).get(TagListViewModel.class);

        // Find Views
        mRecyclerView = rootView.findViewById(R.id.tags_list_rv);
        mFab = rootView.findViewById(R.id.tags_list_fab);

        // Set Adapter / LayoutManager / Decoration
        mTagAdapter = new TagAdapter(this);
        mLayoutManager = RecyclerViewUtil.getDefaultLinearLayoutManager(getActivity());
        mRecyclerView.setAdapter(mTagAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);
        RecyclerViewUtil.setRecyclerViewDecoration(mLayoutManager, mRecyclerView);

        // Hide FAB
        mFab.hide();

        // Set title
        getActivity().setTitle(getString(R.string.action_tags));

        // Subscribe Observers
        subscribeObservers();

        return rootView;
    }

    /**
     * Subscribe Observers.
     */
    @Override
    public void subscribeObservers() {
        mTagListViewModel.getTags().observe(this, new Observer<PagedList<Tag>>() {
            @Override
            public void onChanged(PagedList<Tag> tags) {
                mTagAdapter.submitList(tags);
            }
        });
    }

    @Override
    public void onTagClick(int index) {
        // todo: what to do on click?
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_trash, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
