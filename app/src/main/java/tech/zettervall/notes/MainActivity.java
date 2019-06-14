package tech.zettervall.notes;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.view.Menu;
import android.view.MenuItem;

import org.parceler.Parcels;

import tech.zettervall.mNotes.R;
import tech.zettervall.notes.models.Note;

/**
 * 1. make it possible to add notes and display them in the main recyclerview
 * 2. allow settings to be changed, theme
 * 3. allow user to set notification reminder for a note
 */
public class MainActivity extends BaseActivity implements
        AllNotesFragment.NoteListFragmentClickListener,
        NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private Toolbar mToolbar;
    private DrawerLayout mNavDrawerLayout;
    private NavigationView mNavView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        if(savedInstanceState == null) {
            setNoteListFragment(new AllNotesFragment());
            if (mIsTablet) { // TABLET
                setNoteFragment(new NoteFragment());
            }
        }
    }

    /**
     * Create Fragment for new Note.
     */
    private void newNote() {
        if (!mIsTablet) { // PHONE
            startActivity(new Intent(this, NoteActivity.class));
        } else { // TABLET
            setNoteFragment(new NoteFragment());
        }
    }

    @Override
    public void onNoteListFragmentFabClick() {
        newNote();
    }

    @Override
    public void onNoteClick(Note note) {
        if (!mIsTablet) { // PHONE
            Intent intent = new Intent(this, NoteActivity.class);
            intent.putExtra(Constants.NOTE, Parcels.wrap(note));
            startActivity(intent);
        } else { // TABLET
            //TODO: FIX LATER
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
                // Implemented in Fragment
                break;
            case R.id.action_sort:
                // Implemented in Fragment
                break;
        }
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        super.onNavigationItemSelected(menuItem);
        return true;
    }
}
