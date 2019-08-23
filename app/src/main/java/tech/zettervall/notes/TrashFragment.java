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

        // Set Adapter / LayoutManager / Decoration
        mNoteAdapter = new NoteAdapter(this);
        mLayoutManager = RecyclerViewUtil.getDefaultLinearLayoutManager(getActivity());
        mRecyclerView.setAdapter(mNoteAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);
        RecyclerViewUtil.setRecyclerViewDecoration(mLayoutManager, mRecyclerView);

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
        mTrashFragmentViewModel.getTrash().observe(getViewLifecycleOwner(), new Observer<PagedList<Note>>() {
            @Override
            public void onChanged(PagedList<Note> notes) {
                mNoteAdapter.submitList(notes);
                emptyTextView.setVisibility(notes.isEmpty() ? View.VISIBLE : View.GONE);
            }
        });
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
                dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                mTrashFragmentViewModel.emptyTrash();
                                Toast.makeText(getActivity(),
                                        "Trash emptied",
                                        Toast.LENGTH_SHORT).show();
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };
                builder.setMessage(getString(R.string.confirm_empty_trash))
                        .setPositiveButton(getString(R.string.confirm), dialogClickListener)
                        .setNegativeButton(getString(R.string.abort), dialogClickListener).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
