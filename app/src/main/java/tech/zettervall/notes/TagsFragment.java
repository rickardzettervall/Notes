package tech.zettervall.notes;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
import tech.zettervall.notes.adapters.TagSelectAdapter;
import tech.zettervall.notes.models.Note;
import tech.zettervall.notes.models.Tag;
import tech.zettervall.notes.utils.RecyclerViewUtil;
import tech.zettervall.notes.viewmodels.TagListViewModel;

public class TagsFragment extends BaseListFragment implements TagAdapter.OnTagClickListener {

    private static final String TAG = TagsFragment.class.getSimpleName();
    private TagListViewModel mTagListViewModel;
    private TagAdapter mTagAdapter;

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
                // Inflate View
                final View dialogView = View.inflate(getActivity(), R.layout.dialog_tag_new, null);

                DialogInterface.OnClickListener dialogClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case DialogInterface.BUTTON_POSITIVE:
                                        EditText newTag = dialogView.findViewById(R.id.new_tag_edittext);
                                        String str = newTag.getText().toString()
                                                .replaceAll("#", "");
                                        mTagListViewModel.insertTag(new Tag(str));
                                        break;
                                    case DialogInterface.BUTTON_NEGATIVE:
                                        break;
                                }
                            }
                        };

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(getString(R.string.action_tag_new));
                builder.setView(dialogView);
                builder.setPositiveButton(R.string.confirm_done, dialogClickListener);
                builder.setNegativeButton(R.string.abort, dialogClickListener);
                builder.show();
            }
        });

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
        mTagListViewModel.getTags().observe(getViewLifecycleOwner(), new Observer<PagedList<Tag>>() {
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
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
