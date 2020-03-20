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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.PagedList;

import com.google.android.material.navigation.NavigationView;

import org.parceler.Parcels;

import java.util.List;

import tech.zettervall.mNotes.BuildConfig;
import tech.zettervall.mNotes.R;
import tech.zettervall.notes.models.Note;
import tech.zettervall.notes.models.Tag;
import tech.zettervall.notes.utils.ColorStateListUtil;
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
        mMainActivityViewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);

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

        // Set Navigation Drawer item background/text/icon color (for currently checked item)
        if (!mEnableDarkTheme) { // Light Theme
            mNavView.setItemBackground(getResources().getDrawable(R.color.selector_navitem));
        } else { // Dark Theme
            mNavView.setItemTextColor(ColorStateListUtil.getNavigationDrawerNightColorStateList(this));
            mNavView.setItemIconTintList(ColorStateListUtil.getNavigationDrawerNightColorStateList(this));
        }

        // Set Fragments
        if (savedInstanceState == null) {
            setAllNotesFragment(new AllNotesFragment());
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
            mMainActivityViewModel.getNotificationNote().observe(this, (Note note) -> {
                // Reset Notification
                note.setNotificationEpoch(-1);
                // Simulate click
                onNoteClick(note);
                // Remove Extra to prevent loop
                getIntent().removeExtra(Constants.NOTE_ID);
            });
        }

        // Subscribe Observers
        subscribeObservers();
    }

    /**
     * Observers for updating number of notes in each navigation view category.
     */
    private void subscribeObservers() {
        mMainActivityViewModel.getNotes().observe(this, (PagedList<Note> notes) -> {
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
            updateTagsInDrawer(mMainActivityViewModel.getTagsList());
        });
        mMainActivityViewModel.getFavorites().observe(this, (PagedList<Note> notes) ->
                mFavoritesCounterTextView.setText(getNotesCounterValue(notes.size())));
        mMainActivityViewModel.getReminders().observe(this, (PagedList<Note> notes) ->
                mRemindersCounterTextView.setText(getNotesCounterValue(notes.size())));
        mMainActivityViewModel.getTags().observe(this, (PagedList<Tag> tags) ->
                updateTagsInDrawer(tags));
    }

    /**
     * Update Navigation Drawer SubMenu of Tags
     */
    private void updateTagsInDrawer(final List<Tag> tags) {
        mTagsItem.getSubMenu().clear();
        for (int i = 0; i < tags.size(); i++) {
            final Tag tag = tags.get(i);
            final int index = i;
            String tagString = "#" + tag.getTitle();
            MenuItem menuItem = mTagsItem.getSubMenu().add(tagString)
                    .setActionView(R.layout.nav_view_counter);
            TextView counterTextView = menuItem.getActionView()
                    .findViewById(R.id.nav_view_counter_textview);
            counterTextView.setText(String.valueOf(mNotesTagsCount.get(tag.getId())));
            menuItem.setOnMenuItemClickListener((MenuItem item) -> {
                resetNavDrawerChecked();
                item.setCheckable(true);
                item.setChecked(true);
                mMainActivityViewModel.setSelectedNavItemTag(true);
                mMainActivityViewModel.setSelectedNavItemIndex(index);
                setNotesByTagFragment(getNotesByTagFragment(tag));
                mNavDrawerLayout.closeDrawer(GravityCompat.START);
                return true;
            });
        }

        // Set current Navigation Drawer item as checked
        setNavDrawerItemChecked(mMainActivityViewModel.isSelectedNavItemTag(),
                mMainActivityViewModel.getSelectedNavItemIndex());
    }

    /**
     * Ensure counter can't go above 999.
     */
    private String getNotesCounterValue(int count) {
        return count > 999 ? "999" : String.valueOf(count);
    }

    /**
     * Reset checked status of Navigation Drawer items.
     */
    private void resetNavDrawerChecked() {
        for (int i = 0; i < mNavView.getMenu().size(); i++) {
            mNavView.getMenu().getItem(i).setChecked(false);
        }
        for (int i = 0; i < mTagsItem.getSubMenu().size(); i++) {
            mTagsItem.getSubMenu().getItem(i).setChecked(false);
        }
    }

    /**
     * Set Navigation Drawer item as checked.
     *
     * @param isTag Set to true if index should be used in list of tags
     * @param index Index in Navigation Drawer
     */
    private void setNavDrawerItemChecked(boolean isTag, int index) {
        if (isTag) {
            mTagsItem.getSubMenu().getItem(index).setCheckable(true).setChecked(true);
        } else {
            mNavView.getMenu().getItem(index).setCheckable(true).setChecked(true);
        }
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
        // Set current Navigation Drawer item as checked
        setNavDrawerItemChecked(mMainActivityViewModel.isSelectedNavItemTag(),
                mMainActivityViewModel.getSelectedNavItemIndex());
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
        resetNavDrawerChecked();
        int index = 0;
        for (int i = 0; i < mNavView.getMenu().size(); i++) {
            if (mNavView.getMenu().getItem(i) == menuItem) {
                index = i;
                break;
            }
        }
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        switch (menuItem.getItemId()) {
            case R.id.nav_all_notes: // All Notes Fragment
                menuItem.setChecked(true);
                mMainActivityViewModel.setSelectedNavItemIndex(index);
                mMainActivityViewModel.setSelectedNavItemTag(false);

                // Set Fragment
                Fragment allNotesFragment = getSupportFragmentManager()
                        .findFragmentByTag(Constants.FRAGMENT_ALL_NOTES);
                setAllNotesFragment(fragments.contains(allNotesFragment) ?
                        (AllNotesFragment) allNotesFragment : new AllNotesFragment());
                break;
            case R.id.nav_favorites: // Favorites Fragment
                menuItem.setChecked(true);
                mMainActivityViewModel.setSelectedNavItemIndex(index);
                mMainActivityViewModel.setSelectedNavItemTag(false);

                // Set Fragment
                Fragment favoritesFragment = getSupportFragmentManager()
                        .findFragmentByTag(Constants.FRAGMENT_FAVORITES);
                setFavoritesFragment(fragments.contains(favoritesFragment) ?
                        (FavoritesFragment) favoritesFragment : new FavoritesFragment());
                break;
            case R.id.nav_reminders: // Reminders Fragment
                menuItem.setChecked(true);
                mMainActivityViewModel.setSelectedNavItemIndex(index);
                mMainActivityViewModel.setSelectedNavItemTag(false);

                // Set Fragment
                Fragment remindersFragment = getSupportFragmentManager()
                        .findFragmentByTag(Constants.FRAGMENT_REMINDERS);
                setRemindersFragment(fragments.contains(remindersFragment) ?
                        (RemindersFragment) remindersFragment : new RemindersFragment());
                break;
            case R.id.nav_tags: // Tags Fragment
                menuItem.setChecked(true);
                mMainActivityViewModel.setSelectedNavItemIndex(index);
                mMainActivityViewModel.setSelectedNavItemTag(false);

                // Set Fragment
                Fragment tagsFragment = getSupportFragmentManager()
                        .findFragmentByTag(Constants.FRAGMENT_TAGS);
                setTagsFragment(fragments.contains(tagsFragment) ?
                        (TagsFragment) tagsFragment : new TagsFragment());
                break;
            case R.id.nav_trash: // Trash Fragment
                menuItem.setChecked(true);
                mMainActivityViewModel.setSelectedNavItemIndex(index);
                mMainActivityViewModel.setSelectedNavItemTag(false);

                // Set Fragment
                Fragment trashFragment = getSupportFragmentManager().
                        findFragmentByTag(Constants.FRAGMENT_TRASH);
                setTrashFragment(fragments.contains(trashFragment) ?
                        (TrashFragment) trashFragment : new TrashFragment());
                break;
            case R.id.nav_settings: // Settings Activity
                startActivity(new Intent(this, SettingsActivity.class));
                break;
        }
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
