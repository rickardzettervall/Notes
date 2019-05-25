package tech.zettervall.notes;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.PreferenceManager;

import android.util.Log;
import android.view.View;
import android.view.MenuItem;

import tech.zettervall.mNotes.R;
import tech.zettervall.notes.viewmodels.NotesViewModel;

/**
 * 1. make it possible to add notes and display them in the main recyclerview
 * 2. allow settings to be changed, theme
 * 3. allow user to set notification reminder for a note
 */
public class MainActivity extends BaseActivity implements
        NoteListFragment.NoteListFragmentClickListener,
        NoteFragment.NoteFragmentClickListener,
        NavigationView.OnNavigationItemSelectedListener,
        View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String FRAGMENT_NOTELIST = "fragment_notelist";
    private static final String FRAGMENT_NOTE = "fragment_note";
    private NotesViewModel mNotesViewModel;
    private Toolbar mToolbar;
    private DrawerLayout mNavDrawerLayout;
    private NavigationView mNavView;
    private FragmentManager mFragmentManager;
    private boolean mEnableDarkTheme;
    private boolean mIsTablet;
    private Integer mNoteID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve saved fields
        if (savedInstanceState != null) {
            mNoteID = savedInstanceState.getInt(Constants.NOTE_ID);
        }
        mEnableDarkTheme = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(getString(R.string.enable_dark_theme_key), false);
        mIsTablet = getResources().getBoolean(R.bool.isTablet);

        // Set Theme
        setTheme();

        // Fragment handling
        mFragmentManager = getSupportFragmentManager();

        // Set ContentView
        setContentView(R.layout.activity_main);

        // Initialize ViewModel
        mNotesViewModel = ViewModelProviders.of(this).get(NotesViewModel.class);

        // Find Views
        mToolbar = findViewById(R.id.toolbar);
        mNavDrawerLayout = findViewById(R.id.drawer_layout);
        mNavView = findViewById(R.id.nav_view);

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
        if (mIsTablet) {
            setNoteListFragment(new NoteListFragment());
            if (mNoteID != null && mNoteID != -1) {
                setNoteFragment(createNoteFragmentWithBundle(mNoteID));
            } else {
                setNoteFragment(new NoteFragment());
            }
        }
    }

    private NoteFragment createNoteFragmentWithBundle(int noteID) {
        // Set local mNoteID to allow configuration changes
        mNoteID = noteID;

        // Create Bundle and Fragment
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.NOTE_ID, noteID);
        NoteFragment noteFragment = new NoteFragment();
        noteFragment.setArguments(bundle);
        return noteFragment;
    }

    private void setNoteListFragment(NoteListFragment noteListFragment) {
        mFragmentManager.beginTransaction()
                .replace(R.id.frame_list, noteListFragment, FRAGMENT_NOTELIST)
                .commit();
    }

    private void setNoteFragment(NoteFragment noteFragment) {
        mFragmentManager.beginTransaction()
                .replace(R.id.frame_note, noteFragment, FRAGMENT_NOTE)
                .commit();
    }

    private void setTheme() {
        if (mEnableDarkTheme) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    /**
     * Create Fragment for new Note.
     */
    private void newNoteFragment() {
        mNoteID = -1;
        setNoteFragment(new NoteFragment());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mNoteID != null) {
            outState.putInt(Constants.NOTE_ID, mNoteID);
        }
    }

    @Override
    public void onNoteFragmentFabClick() {

    }

    @Override
    public void onNoteListFragmentFabClick() {
        newNoteFragment();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                // Start NoteActivity
                startActivity(new Intent(this, NoteActivity.class));
                break;
        }
    }

    @Override
    public void onNoteClick(int _id) {
        setNoteFragment(createNoteFragmentWithBundle(_id));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mNavDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_create_new_note:
                if (mIsTablet) {
                    newNoteFragment();
                } else {
                    startActivity(new Intent(this, NoteActivity.class));
                }
                break;
            case R.id.nav_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.nav_about:
                startActivity(new Intent(this, AboutActivity.class));
                break;
            case R.id.nav_change_theme:
                if (mEnableDarkTheme) {
                    PreferenceManager.getDefaultSharedPreferences(this)
                            .edit().putBoolean(getString(R.string.enable_dark_theme_key), false)
                            .apply();
                } else {
                    PreferenceManager.getDefaultSharedPreferences(this)
                            .edit().putBoolean(getString(R.string.enable_dark_theme_key), true)
                            .apply();
                }
                setTheme();
                recreate();
                break;
        }
        mNavDrawerLayout.closeDrawers();
        return true;
    }
}
