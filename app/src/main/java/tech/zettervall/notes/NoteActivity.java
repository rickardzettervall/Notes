package tech.zettervall.notes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.parceler.Parcels;

import tech.zettervall.mNotes.R;
import tech.zettervall.notes.models.Note;

public class NoteActivity extends AppCompatActivity {

    private static final String TAG = NoteActivity.class.getSimpleName();
    private static final String NOTE_ID = "note_id";
    private TextView mHeadline, mText;
    private Integer mNoteID;
    private Note mNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        // Find Views
        mHeadline = findViewById(R.id.headline_tv);
        mText = findViewById(R.id.text_tv);

        // Set default Note
        mNote = new Note(Constants.TYPE_PLAIN,null, null);

        // Retrieve existing data
        if (getIntent().getExtras() != null) {
            // A already existing Note was clicked and the ID resides in Extras
            mNote = Parcels.unwrap(getIntent().getParcelableExtra(Constants.NOTE_PARCEL));
        } else if (savedInstanceState != null) {
            // Restore newly created Note ID from savedInstanceState
            mNoteID = savedInstanceState.getInt(NOTE_ID);
        }

        // Set Views
        mHeadline.setText(mNote.getHeadline());
        mText.setText(mNote.getText());
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