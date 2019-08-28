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
import androidx.recyclerview.widget.RecyclerView;

import tech.zettervall.mNotes.R;
import tech.zettervall.notes.adapters.TagAdapter;
import tech.zettervall.notes.models.Tag;
import tech.zettervall.notes.utils.KeyboardUtil;
import tech.zettervall.notes.utils.RecyclerViewUtil;
import tech.zettervall.notes.viewmodels.TagsFragmentViewModel;

public class TagsFragment extends BaseListFragment implements TagAdapter.OnTagClickListener {

    private static final String TAG = TagsFragment.class.getSimpleName();
    private TagsFragmentViewModel mTagsFragmentViewModel;
    private TagAdapter mTagAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_taglist, container, false);

        // Initialize ViewModel
        mTagsFragmentViewModel = ViewModelProviders.of(this).get(TagsFragmentViewModel.class);

        // Find Views
        mRecyclerView = rootView.findViewById(R.id.fragment_taglist_recyclerview);
        mFab = rootView.findViewById(R.id.fragment_taglist_fab);

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
                final EditText tagTitleEditText = dialogView.findViewById(R.id.dialog_tag_new_edittext);
                DialogInterface.OnClickListener dialogClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case DialogInterface.BUTTON_POSITIVE:
                                        String str = tagTitleEditText.getText().toString()
                                                .replaceAll("#", "");
                                        mTagsFragmentViewModel.insertTag(new Tag(str.trim()));
                                        break;
                                    case DialogInterface.BUTTON_NEGATIVE:
                                        KeyboardUtil.hideKeyboard(getActivity());
                                        break;
                                }
                            }
                        };
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(getString(R.string.tag_new))
                        .setView(dialogView)
                        .setPositiveButton(R.string.confirm, dialogClickListener)
                        .setNegativeButton(R.string.abort, dialogClickListener)
                        .setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                KeyboardUtil.hideKeyboard(getActivity());
                            }
                        })
                        .show();

                // Set focus and show keyboard
                tagTitleEditText.requestFocus();
                KeyboardUtil.showKeyboard(getActivity());
            }
        });

        // Set title
        getActivity().setTitle(getString(R.string.action_tags));

        // Subscribe Observers
        subscribeObservers();

        return rootView;
    }

    @Override
    public void subscribeObservers() {
        super.subscribeObservers();
        mTagsFragmentViewModel.getTags().observe(getViewLifecycleOwner(), new Observer<PagedList<Tag>>() {
            @Override
            public void onChanged(PagedList<Tag> tags) {
                mTagAdapter.submitList(tags);
            }
        });
    }

    @Override
    public void onTagDeleteClick(int index) {
        final Tag tag = mTagAdapter.getCurrentList().get(index);
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        // Delete Tag and update associated Notes
                        mTagsFragmentViewModel.deleteTag(tag);
                        Toast.makeText(getActivity(), getString(R.string.tag_deleted_toast,
                                tag.getTitle()), Toast.LENGTH_SHORT).show();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        KeyboardUtil.hideKeyboard(getActivity());
                        break;
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.tag_delete_title))
                .setMessage(getString(R.string.tag_delete_message))
                .setPositiveButton(R.string.confirm, dialogClickListener)
                .setNegativeButton(R.string.abort, dialogClickListener)
                .show();
    }

    @Override
    public void onTagClick(int index) {
        final Tag tag = mTagAdapter.getCurrentList().get(index);
        final View dialogView = View.inflate(getActivity(), R.layout.dialog_tag_edit, null);
        final EditText tagTitleEditText = dialogView.findViewById(R.id.dialog_tag_edit_edittext);
        tagTitleEditText.setText(tag.getTitle());
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        // Update Tag
                        tag.setTitle(tagTitleEditText.getText().toString().trim());
                        mTagsFragmentViewModel.updateTag(tag);
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        KeyboardUtil.hideKeyboard(getActivity());
                        break;
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.tag_edit))
                .setView(dialogView)
                .setPositiveButton(R.string.confirm, dialogClickListener)
                .setNegativeButton(R.string.abort, dialogClickListener)
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        KeyboardUtil.hideKeyboard(getActivity());
                    }
                })
                .show();

        // Set focus and show keyboard
        tagTitleEditText.requestFocus();
        KeyboardUtil.showKeyboard(getActivity());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        // Hide unused menu items
        menu.findItem(R.id.action_search).setVisible(false);
        menu.findItem(R.id.action_sort).setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
