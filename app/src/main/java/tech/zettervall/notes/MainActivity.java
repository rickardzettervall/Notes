package tech.zettervall.notes;

import android.content.Intent;
import android.os.Bundle;
import android.util.SparseIntArray;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.paging.PagedList;

import com.google.android.material.navigation.NavigationView;

import org.parceler.Parcels;

import java.util.List;

import tech.zettervall.mNotes.BuildConfig;
import tech.zettervall.mNotes.R;
import tech.zettervall.notes.models.Note;
import tech.zettervall.notes.models.Tag;
import tech.zettervall.notes.viewmodels.MainActivityViewModel;

public class MainActivity extends BaseActivity implements
        BaseListFragment.ListFragmentClickListener,
        NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private Toolbar mToolbar;
    private DrawerLayout mNavDrawerLayout;
    private NavigationView mNavView;
    private MainActivityViewModel mMainActivityViewModel;
    private SparseIntArray mNotesTagsCount = new SparseIntArray();
    private TextView mAllNotesCounterTextView, mFavoritesCounterTextView, mRemindersCounterTextView,
            mNavHeaderVersionTextView;
    private boolean startedByNotification;
    private MenuItem mTagsItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize ViewModel
        mMainActivityViewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);

        // Set ContentView
        setContentView(R.layout.activity_main);

        // Find Views
        mToolbar = findViewById(R.id.toolbar);
        mNavDrawerLayout = findViewById(R.id.drawer_layout);
        mNavView = findViewById(R.id.nav_view);
        mNavHeaderVersionTextView = mNavView.getHeaderView(0).findViewById(R.id.nav_header_version_textview);
        MenuItem allNotesItem = mNavView.getMenu().findItem(R.id.nav_all_notes),
                favoritesItem = mNavView.getMenu().findItem(R.id.nav_favorites),
                remindersItem = mNavView.getMenu().findItem(R.id.nav_reminders);
        mTagsItem = mNavView.getMenu().findItem(R.id.nav_tags_header);
        mAllNotesCounterTextView = allNotesItem.getActionView()
                .findViewById(R.id.nav_view_counter_textview);
        mFavoritesCounterTextView = favoritesItem.getActionView()
                .findViewById(R.id.nav_view_counter_textview);
        mRemindersCounterTextView = remindersItem.getActionView()
                .findViewById(R.id.nav_view_counter_textview);

        // Set ToolBar
        setSupportActionBar(mToolbar);

        // Set Drawer
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(
                this, mNavDrawerLayout, mToolbar, R.string.cont_nav_drawer_open, R.string.cont_nav_drawer_close);
        mNavDrawerLayout.addDrawerListener(drawerToggle);
        mNavHeaderVersionTextView.setText(getString(R.string.app_version, BuildConfig.VERSION_NAME));
        drawerToggle.syncState();

        // Set Listeners
        mNavView.setNavigationItemSelectedListener(this);

        // Set Fragments
        if (savedInstanceState == null) {
            setNoteListFragment(new AllNotesFragment());
            if (mIsTablet) { // TABLET
                setNoteFragment(new NoteFragment());
            }
        }

        // Clicked Notification
        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(Constants.NOTE_ID)) {
            int noteID = getIntent().getIntExtra(Constants.NOTE_ID, 0);

            startedByNotification = true;

            // Set Note
            mMainActivityViewModel.setNotificationNote(noteID);

            // Observer
            mMainActivityViewModel.getNotificationNote().observe(this, new Observer<Note>() {
                @Override
                public void onChanged(Note note) {
                    // Reset Notification
                    note.setNotificationEpoch(-1);
                    // Simulate click
                    onNoteClick(note);
                    // Remove Extra to prevent loop
                    getIntent().removeExtra(Constants.NOTE_ID);
                }
            });
        }

        // Subscribe Observers
        subscribeObservers();
    }

    /**
     * Observers for updating number of notes in each navigation view category.
     */
    private void subscribeObservers() {
        mMainActivityViewModel.getNotes().observe(this, new Observer<PagedList<Note>>() {
            @Override
            public void onChanged(PagedList<Note> notes) {
                mAllNotesCounterTextView.setText(getNotesCounterValue(notes.size()));
                mNotesTagsCount.clear();
                for (int i = 0; i < notes.size(); i++) {
                    try {
                        // Get number of Notes in each Tag category and put them into SparseIntArray
                        for (Integer j : notes.get(i).getTagIDs()) {
                            mNotesTagsCount.put(j, mNotesTagsCount.get(j) + 1);
                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
                updateTagCounters(mMainActivityViewModel.getTagsList());
            }
        });
        mMainActivityViewModel.getFavorites().observe(this, new Observer<PagedList<Note>>() {
            @Override
            public void onChanged(PagedList<Note> notes) {
                mFavoritesCounterTextView.setText(getNotesCounterValue(notes.size()));
            }
        });
        mMainActivityViewModel.getReminders().observe(this, new Observer<PagedList<Note>>() {
            @Override
            public void onChanged(PagedList<Note> notes) {
                mRemindersCounterTextView.setText(getNotesCounterValue(notes.size()));
            }
        });
        mMainActivityViewModel.getTags().observe(this, new Observer<PagedList<Tag>>() {
            @Override
            public void onChanged(PagedList<Tag> tags) {
                updateTagCounters(tags);
            }
        });
    }

    /**
     * Update Navigation Drawer SubMenu of Tags
     */
    private void updateTagCounters(final List<Tag> tags) {
        mTagsItem.getSubMenu().clear();
        for (final Tag tag : tags) {
            String tagString = "#" + tag.getTitle();
            MenuItem menuItem = mTagsItem.getSubMenu().add(tagString)
                    .setActionView(R.layout.nav_view_counter);
            TextView counterTextView = menuItem.getActionView()
                    .findViewById(R.id.nav_view_counter_textview);
            counterTextView.setText(String.valueOf(mNotesTagsCount.get(tag.getId())));
            menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    setNotesByTagFragment(getNotesByTagFragment(tag));
                    mNavDrawerLayout.closeDrawer(GravityCompat.START);
                    return true;
                }
            });
        }
    }

    /**
     * Ensure counter can't go above 999.
     */
    private String getNotesCounterValue(int count) {
        return count > 999 ? "999" : String.valueOf(count);
    }

    /**
     * Create new Note.
     *
     * @param setFavorite when true, Note will be favorite on creation
     * @param tag         Used to set Tag for Note on creation
     */
    @Override
    public void onFragmentFabClick(boolean setFavorite, @Nullable Tag tag) {
        if (!mIsTablet) { // PHONE
            Intent intent = new Intent(this, NoteActivity.class);
            intent.putExtra(Constants.NOTE_FAVORITE, setFavorite);
            intent.putExtra(Constants.TAG, Parcels.wrap(tag));
            startActivity(intent);
        } else { // TABLET
            if (setFavorite || tag != null) {
                setNoteFragment(getNoteFragment(null, setFavorite, tag));
            } else {
                setNoteFragment(new NoteFragment());
            }
        }
    }

    @Override
    public void onNoteClick(Note note) {
        if (!mIsTablet) { // PHONE
            Intent intent = new Intent(this, NoteActivity.class);
            intent.putExtra(Constants.NOTE, Parcels.wrap(note));
            startActivity(intent);
        } else { // TABLET
            setNoteFragment(getNoteFragment(note, false, null));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mNavDrawerLayout.closeDrawer(GravityCompat.START, false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_search:
                // IMPLEMENT IN FRAGMENT
                break;
            case R.id.action_sort:
                // IMPLEMENT IN FRAGMENT
                break;
        }
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        super.onNavigationItemSelected(menuItem);
        mNavDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        /* This observer is only needed once (when user clicks the notification),
         * therefor we remove it directly after it's used. */
        if (startedByNotification) {
            mMainActivityViewModel.getNotificationNote().removeObservers(this);
        }
    }
}
