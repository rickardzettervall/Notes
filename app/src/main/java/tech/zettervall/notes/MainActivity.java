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
import androidx.preference.PreferenceManager;

import android.view.Gravity;
import android.view.MenuItem;

import tech.zettervall.mNotes.R;

/**
 * 1. make it possible to add notes and display them in the main recyclerview
 * 2. allow settings to be changed, theme
 * 3. allow user to set notification reminder for a note
 */
public class MainActivity extends BaseActivity implements
        NoteListFragment.NoteListFragmentClickListener,
        NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private Toolbar mToolbar;
    private DrawerLayout mNavDrawerLayout;
    private NavigationView mNavView;
    private Integer mNoteID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve saved fields
        if (savedInstanceState != null) {
            mNoteID = savedInstanceState.getInt(Constants.NOTE_ID);
        }

        // Set ContentView
        setContentView(R.layout.activity_main);

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
        setNoteListFragment(new NoteListFragment());
        if (mIsTablet) { // TABLET
            if (mNoteID != null && mNoteID != -1) {
                setNoteFragment(getNoteFragmentWithBundle(mNoteID));
            } else {
                setNoteFragment(new NoteFragment());
            }
        }
    }

    @Override
    public NoteFragment getNoteFragmentWithBundle(int noteID) {
        // Set local mNoteID to allow configuration changes
        mNoteID = noteID;
        return super.getNoteFragmentWithBundle(noteID);
    }

    /**
     * Create Fragment for new Note.
     */
    private void newNote() {
        mNoteID = -1;
        if (!mIsTablet) { // PHONE
            startActivity(new Intent(this, NoteActivity.class));
        } else { // TABLET
            setNoteFragment(new NoteFragment());
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mNoteID != null) {
            outState.putInt(Constants.NOTE_ID, mNoteID);
        }
    }

    @Override
    public void onNoteListFragmentFabClick() {
        newNote();
    }

    @Override
    public void onNoteClick(int _id) {
        if (!mIsTablet) { // PHONE
            Intent intent = new Intent(this, NoteActivity.class);
            intent.putExtra(Constants.NOTE_ID, _id);
            startActivity(intent);
        } else { // TABLET
            setNoteFragment(getNoteFragmentWithBundle(_id));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case android.R.id.home:
//                mNavDrawerLayout.openDrawer(GravityCompat.START);
//                return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    protected void onResume() {
        super.onResume();
        mNavDrawerLayout.closeDrawer(GravityCompat.START, false);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        super.onNavigationItemSelected(menuItem);
        return true;
    }
}
