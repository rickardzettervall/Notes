package tech.zettervall.notes;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import org.parceler.Parcels;

import tech.zettervall.mNotes.R;
import tech.zettervall.notes.adapters.NoteAdapter;
import tech.zettervall.notes.models.Note;
import tech.zettervall.notes.viewmodels.NotesViewModel;

/**
 * 1. make it possible to add notes and display them in the main recyclerview
 * 2. allow settings to be changed, theme
 * 3. allow user to set notification reminder for a note
 */
public class MainActivity extends BaseActivity implements View.OnClickListener,
        NoteAdapter.OnNoteClickListener, NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private FloatingActionButton mFab;
    private NotesViewModel mNotesViewModel;
    private NoteAdapter mNoteAdapter;
    private Toolbar mToolbar;
    private DrawerLayout mNavDrawerLayout;
    private NavigationView mNavView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set Theme
        setTheme();

        // Set ContentView
        setContentView(R.layout.activity_main);

        // Initialize ViewModel
        mNotesViewModel = ViewModelProviders.of(this).get(NotesViewModel.class);

        // Find Views
        mRecyclerView = findViewById(R.id.notes_list_rv);
        mFab = findViewById(R.id.fab);
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

        // Set Adapter / LayoutManager / Decoration
        mNoteAdapter = new NoteAdapter(this);
        mLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        mRecyclerView.setAdapter(mNoteAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                mLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);
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

        // Set Listeners
        mFab.setOnClickListener(this);
        mNavView.setNavigationItemSelectedListener(this);

        // Subscribe Observers
        subscribeObservers();
    }

    private void setTheme() {
        // Get SharedPreferences (Dark Theme?)
        boolean enableDarkTheme = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(getString(R.string.enable_dark_theme_key), false);

        if (enableDarkTheme) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
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

    /**
     * Subscribe Observers so that data survives configuration changes.
     */
    private void subscribeObservers() {
        mNotesViewModel.getNotes().observe(this, new Observer<PagedList<Note>>() {
            @Override
            public void onChanged(PagedList<Note> notes) {
                mNoteAdapter.submitList(notes);
            }
        });
    }

    /**
     * OnClickListener for Notes RecyclerView.
     *
     * @param index Index of clicked item
     */
    @Override
    public void onNoteClick(int index) {
        // Start NoteActivity with clicked Note so that it can be edited
        Intent intent = new Intent(this, NoteActivity.class);
        intent.putExtra(Constants.NOTE_PARCEL,
                Parcels.wrap(mNotesViewModel.getNotes().getValue().get(index)));
        startActivity(intent);
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
                startActivity(new Intent(this, NoteActivity.class));
                break;
            case R.id.nav_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.nav_about:
                startActivity(new Intent(this, AboutActivity.class));
                break;
        }
        mNavDrawerLayout.closeDrawers();
        return true;
    }
}
