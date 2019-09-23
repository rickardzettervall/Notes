package tech.zettervall.notes;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import tech.zettervall.mNotes.R;
import tech.zettervall.notes.adapters.NoteAdapter;
import tech.zettervall.notes.models.Note;
import tech.zettervall.notes.models.Tag;

public abstract class BaseListFragment extends Fragment
        implements NoteAdapter.OnNoteClickListener, ListObservers {

    private static final String TAG = BaseListFragment.class.getSimpleName();
    protected ListFragmentClickListener callback;
    // Adapter
    protected LinearLayoutManager mLayoutManager;
    protected RecyclerView mRecyclerView;
    protected NoteAdapter mNoteAdapter;
    // Common Views
    protected FloatingActionButton mFab;
    protected TextView emptyTextView;
    private SharedPreferences mSharedPreferences;
    // Used for SearchView to restore state on configuration changes
    private boolean mSearchIconified;
    private String mSearchQuery;

    @VisibleForTesting
    public NoteAdapter getNoteAdapter() {
        return mNoteAdapter;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Set SharedPreferences
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        // Enable Toolbar MenuItem handling
        setHasOptionsMenu(true);

        // Retrieve saved fields
        if (savedInstanceState != null) {
            mSearchQuery = savedInstanceState.getString(Constants.SEARCH_QUERY);
            mSearchIconified = savedInstanceState.getBoolean(Constants.SEARCH_ICONIFIED);
        }

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    /**
     * IMPLEMENT IN FRAGMENT.
     * Click event for NoteAdapter.
     */
    @Override
    public void onNoteClick(int index) {
    }

    /**
     * IMPLEMENT IN FRAGMENT.
     * Subscribe Observers.
     */
    @Override
    public void subscribeObservers() {
    }

    /**
     * IMPLEMENT IN FRAGMENT.
     * Reload Observers, primarily for when user changes sorting.
     */
    @Override
    public void refreshObservers(@Nullable String query) {
    }

    /**
     * Lambda for Observer.
     */
    protected void updateAdapter(PagedList<Note> notes) {
        mNoteAdapter.submitList(notes);
        emptyTextView.setVisibility(notes.isEmpty() ? View.VISIBLE : View.GONE);
    }

    /**
     * Lambda for FAB Click.
     */
    protected void fabClick(View v) {
        callback.onFragmentFabClick(false, null);
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
                if (!query.isEmpty()) {
                    emptyTextView.setText(R.string.search_is_empty);
                } else {
                    emptyTextView.setText(R.string.list_is_empty);
                }
                refreshObservers(query);
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
                // Refresh List of Notes
                refreshObservers(null);
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sort:

                // Inflate View
                View dialogView = View.inflate(getActivity(), R.layout.dialog_sort, null);

                // Sort type (RadioGroup)
                RadioGroup sortTypeGroup = dialogView.findViewById(R.id.dialog_sort_type_radiogroup);
                // Restore choice
                int sortTypeChecked = mSharedPreferences.getInt(Constants.SORT_TYPE_KEY,
                        Constants.SORT_TYPE_DEFAULT);
                int checkedRadioButton = 0;
                switch (sortTypeChecked) {
                    case Constants.SORT_TYPE_ALPHABETICALLY:
                        checkedRadioButton = R.id.dialog_sort_type_alphabetically_radiobutton;
                        break;
                    case Constants.SORT_TYPE_CREATION_DATE:
                        checkedRadioButton = R.id.dialog_sort_type_creation_date_radiobutton;
                        break;
                    case Constants.SORT_TYPE_MODIFIED_DATE:
                        checkedRadioButton = R.id.dialog_sort_type_modified_date_radiobutton;
                        break;
                }
                sortTypeGroup.check(checkedRadioButton);
                // Set Listener
                sortTypeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        switch (checkedId) {
                            case R.id.dialog_sort_type_alphabetically_radiobutton: // Alphabetically
                                mSharedPreferences.edit()
                                        .putInt(Constants.SORT_TYPE_KEY,
                                                Constants.SORT_TYPE_ALPHABETICALLY).apply();
                                break;
                            case R.id.dialog_sort_type_creation_date_radiobutton: // Creation date
                                mSharedPreferences.edit()
                                        .putInt(Constants.SORT_TYPE_KEY,
                                                Constants.SORT_TYPE_CREATION_DATE).apply();
                                break;
                            case R.id.dialog_sort_type_modified_date_radiobutton: // Modified date
                                mSharedPreferences.edit()
                                        .putInt(Constants.SORT_TYPE_KEY,
                                                Constants.SORT_TYPE_MODIFIED_DATE).apply();
                                break;
                        }

                        // Refresh List of Notes
                        refreshObservers(null);
                    }
                });

                // Sort with favorites on top (Checkbox)
                CheckBox sortFavoritesOnTop = dialogView.findViewById(R.id.dialog_sort_favorites_on_top_checkbox);
                boolean sortFavoritesBool = mSharedPreferences.getBoolean(
                        Constants.SORT_FAVORITES_ON_TOP_KEY,
                        Constants.SORT_FAVORITES_ON_TOP_DEFAULT);
                sortFavoritesOnTop.setChecked(sortFavoritesBool);
                sortFavoritesOnTop.setOnCheckedChangeListener(
                        new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                mSharedPreferences.edit().putBoolean(
                                        Constants.SORT_FAVORITES_ON_TOP_KEY, isChecked).apply();

                                // Refresh List of Notes
                                refreshObservers(null);
                            }
                        });

                DialogInterface.OnClickListener dialogClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case DialogInterface.BUTTON_POSITIVE: // Ascending
                                        mSharedPreferences.edit()
                                                .putInt(Constants.SORT_DIRECTION_KEY,
                                                        Constants.SORT_DIRECTION_ASC).apply();
                                        break;
                                    case DialogInterface.BUTTON_NEGATIVE: // Descending
                                        mSharedPreferences.edit()
                                                .putInt(Constants.SORT_DIRECTION_KEY,
                                                        Constants.SORT_DIRECTION_DESC).apply();
                                        break;
                                }

                                // Refresh List of Notes
                                refreshObservers(null);
                            }
                        };

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(getString(R.string.sort_by));
                builder.setView(dialogView);
                builder.setPositiveButton(R.string.sort_by_ascending, dialogClickListener);
                builder.setPositiveButtonIcon(getResources().getDrawable(R.drawable.ic_ascending));
                builder.setNegativeButton(R.string.sort_by_descending, dialogClickListener);
                builder.setNegativeButtonIcon(getResources().getDrawable(R.drawable.ic_descending));
                builder.show();
                break;
        }
        return false;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Force Activity to implement callback interface
        try {
            callback = (ListFragmentClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    " must implement 'ListFragmentClickListener'");
        }
    }

    /**
     * Callback interface for sending data back to Activity.
     */
    public interface ListFragmentClickListener {
        void onNoteClick(Note note);

        void onFragmentFabClick(boolean setFavorite, @Nullable Tag tag);
    }
}
