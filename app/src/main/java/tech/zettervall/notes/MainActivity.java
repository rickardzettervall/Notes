package tech.zettervall.notes;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import tech.zettervall.mNotes.R;
import tech.zettervall.notes.adapters.NoteAdapter;
import tech.zettervall.notes.models.Note;
import tech.zettervall.notes.viewmodels.NotesViewModel;

/**
 * 1. make it possible to add notes and display them in the main recyclerview
 * 2. allow settings to be changed, theme
 * 3. allow user to set notification reminder for a note
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        NoteAdapter.OnNoteClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private FloatingActionButton mFab;
    private NotesViewModel mNotesViewModel;
    private Toolbar mToolbar;
    private NoteAdapter mNoteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize ViewModel
        mNotesViewModel = ViewModelProviders.of(this).get(NotesViewModel.class);

        // Find Views
        mToolbar = findViewById(R.id.toolbar);
        mRecyclerView = findViewById(R.id.notes_list_rv);
        mFab = findViewById(R.id.fab);

        // Set Toolbar
        setSupportActionBar(mToolbar);

        // Set Adapter / LayoutManager / Decoration
        mNoteAdapter = new NoteAdapter(this, new ArrayList<Note>());
        mLayoutManager = new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
        mRecyclerView.setAdapter(mNoteAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                mLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        // Set OnClickListeners
        mFab.setOnClickListener(this);

        // Subscribe Observers
        subscribeObservers();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                // Start NoteActivity
                startActivity(new Intent(getBaseContext(), NoteActivity.class));
                break;
        }
    }

    /**
     * Subscribe Observers so that data survives configuration changes.
     */
    private void subscribeObservers() {
        mNotesViewModel.getNotes().observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                // Update RecyclerView when db changes
                mNoteAdapter.setNotes(notes);
            }
        });
    }

    /**
     * OnClickListener for Notes RecyclerView.
     * @param index Index of clicked item
     */
    @Override
    public void onNoteClick(int index) {
        // Start NoteActivity with _id (PrimaryKey) so that it can be retrieved from db
        Intent intent = new Intent(this, NoteActivity.class);
        intent.putExtra(Constants.NOTE_PARCEL, Parcels.wrap(mNoteAdapter.getNotes().get(index)));
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
