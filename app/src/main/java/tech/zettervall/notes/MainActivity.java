package tech.zettervall.notes;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.paging.PagedList;

import com.google.android.material.navigation.NavigationView;

import org.parceler.Parcels;

import tech.zettervall.mNotes.R;
import tech.zettervall.notes.models.Note;
import tech.zettervall.notes.viewmodels.MainActivityViewModel;

public class MainActivity extends BaseActivity implements
        BaseListFragment.ListFragmentClickListener,
        NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private Toolbar mToolbar;
    private DrawerLayout mNavDrawerLayout;
    private NavigationView mNavView;
    private MainActivityViewModel mMainActivityViewModel;
    private TextView mAllNotesCounterTextView, mFavoritesCounterTextView, mRemindersCounterTextView,
            mTrashCounterTextView;
    private boolean startedByNotification;

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
        MenuItem allNotesItem = mNavView.getMenu().findItem(R.id.nav_all_notes),
                favoritesItem = mNavView.getMenu().findItem(R.id.nav_favorites),
                remindersItem = mNavView.getMenu().findItem(R.id.nav_reminders),
                trashItem = mNavView.getMenu().findItem(R.id.nav_trash);
        mAllNotesCounterTextView = allNotesItem.getActionView()
                .findViewById(R.id.nav_view_counter_textview);
        mFavoritesCounterTextView = favoritesItem.getActionView()
                .findViewById(R.id.nav_view_counter_textview);
        mRemindersCounterTextView = remindersItem.getActionView()
                .findViewById(R.id.nav_view_counter_textview);
        mTrashCounterTextView = trashItem.getActionView()
                .findViewById(R.id.nav_view_counter_textview);



        // Set ToolBar
        setSupportActionBar(mToolbar);

        // Set Drawer
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(
                this, mNavDrawerLayout, mToolbar, R.string.nav_drawer_open, R.string.nav_drawer_close);
        mNavDrawerLayout.addDrawerListener(drawerToggle);
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
            mMainActivityViewModel.setNote(noteID);

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
     * Observers for updating number of notes in each navigation view catergory.
     */
    private void subscribeObservers() {
        mMainActivityViewModel.getNotes().observe(this, new Observer<PagedList<Note>>() {
            @Override
            public void onChanged(PagedList<Note> notes) {
                mAllNotesCounterTextView.setText(String.valueOf(notes.size()));
            }
        });
        mMainActivityViewModel.getFavorites().observe(this, new Observer<PagedList<Note>>() {
            @Override
            public void onChanged(PagedList<Note> notes) {
                mFavoritesCounterTextView.setText(String.valueOf(notes.size()));
            }
        });
        mMainActivityViewModel.getReminders().observe(this, new Observer<PagedList<Note>>() {
            @Override
            public void onChanged(PagedList<Note> notes) {
                mRemindersCounterTextView.setText(String.valueOf(notes.size()));
            }
        });
        mMainActivityViewModel.getTrash().observe(this, new Observer<PagedList<Note>>() {
            @Override
            public void onChanged(PagedList<Note> notes) {
                mTrashCounterTextView.setText(String.valueOf(notes.size()));
            }
        });
    }

    /**
     * Create new Note.
     *
     * @param setFavorite when true, Note will be favorite on creation
     */
    @Override
    public void onFragmentFabClick(boolean setFavorite) {
        if (!mIsTablet) { // PHONE
            Intent intent = new Intent(this, NoteActivity.class);
            intent.putExtra(Constants.NOTE_FAVORITE, setFavorite);
            startActivity(intent);
        } else { // TABLET
            setNoteFragment(new NoteFragment());
        }
    }

    @Override
    public void onNoteClick(Note note) {
        if (!mIsTablet) { // PHONE
            Intent intent = new Intent(this, NoteActivity.class);
            intent.putExtra(Constants.NOTE, Parcels.wrap(note));
            startActivity(intent);
        } else { // TABLET
            // TODO: FIX TABLET VERSION LATER
//            setNoteFragment(getNoteFragmentWithBundledNote(note.getId()));
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
