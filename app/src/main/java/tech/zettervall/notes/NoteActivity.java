package tech.zettervall.notes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import tech.zettervall.mNotes.R;
import tech.zettervall.notes.models.Note;
import tech.zettervall.notes.viewmodels.NoteViewModel;

public class NoteActivity extends AppCompatActivity {

    private static final String TAG = NoteActivity.class.getSimpleName();
    private static final String NOTE_ID = "note_id";
    private NoteViewModel mNoteViewModel;
    private TextView mHeadline, mText;
    private Integer mNoteID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        // Initialize ViewModel
        mNoteViewModel = ViewModelProviders.of(this).get(NoteViewModel.class);

        // Find Views
        mHeadline = findViewById(R.id.headline_tv);
        mText = findViewById(R.id.text_tv);

        // Retrieve existing data
        if (getIntent().getExtras() != null) {
            // A already existing Note was clicked and the ID resides in Extras
            mNoteID = getIntent().getExtras().getInt(Constants.NOTE_ID);
        } else if (savedInstanceState != null) {
            // Restore newly created Note ID from savedInstanceState
            mNoteID = savedInstanceState.getInt(NOTE_ID);
        }

        if (mNoteID != null) {
            // Set Note in ViewModel
            mNoteViewModel.setNote(mNoteID);

            // Subscribe Observers
            subscribeObservers();
        }
    }

    /**
     * Subscribe Observers so that data survives configuration changes.
     */
    private void subscribeObservers() {
        mNoteViewModel.getNote().observe(this, new Observer<Note>() {
            @Override
            public void onChanged(Note note) {
                // Update Views with data from db
                mHeadline.setText(note.getHeadline());
                mText.setText(note.getText());
            }
        });
    }

    /**
     * Save Note to db
     */
    @Override
    protected void onPause() {
        super.onPause();

        // New Note
        if (mNoteID == null) {
            Note note = new Note(Constants.TYPE_PLAIN,
                    mHeadline.getText().toString(),
                    mText.getText().toString());
            mNoteID = (int) mNoteViewModel.insertNote(note);
        } else {
            Note note = new Note(mNoteID,
                    Constants.TYPE_PLAIN,
                    mHeadline.getText().toString(),
                    mText.getText().toString());
            mNoteViewModel.updateNote(note);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save Note ID
        outState.putInt(NOTE_ID, mNoteID);
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
                // TODO: open settings
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}