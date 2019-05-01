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
import tech.zettervall.notes.repositories.NoteRepository;
import tech.zettervall.notes.utils.DateTimeHelper;

public class NoteActivity extends AppCompatActivity {

    private static final String TAG = NoteActivity.class.getSimpleName();
    private static final String NOTE_HEADLINE = "note_headline";
    private static final String NOTE_TEXT = "note_text";
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
        mNote = new Note(Constants.TYPE_PLAIN,"", "");

        // Retrieve existing data
        if (getIntent().getExtras() != null) {
            // A already existing Note was clicked and the ID resides in Extras
            mNote = Parcels.unwrap(getIntent().getParcelableExtra(Constants.NOTE_PARCEL));
        } else if (savedInstanceState != null) {
            // Restore newly created Note data from savedInstanceState
            mNote.set_id(savedInstanceState.getInt(NOTE_ID));
            mNote.setHeadline(savedInstanceState.getString(NOTE_HEADLINE));
            mNote.setText(savedInstanceState.getString(NOTE_TEXT));
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
        if (mNote.getText().equals("")) {
            mNote.setHeadline(mHeadline.getText().toString());
            mNote.setText(mText.getText().toString());
            mNoteID = (int) NoteRepository.getInstance(getApplication()).insertNote(mNote);
        } else { // Update Note
            mNote.setHeadline(mHeadline.getText().toString());
            mNote.setText(mText.getText().toString());
            mNote.setDate(DateTimeHelper.getCurrentDateTime());
            NoteRepository.getInstance(getApplication()).updateNote(mNote);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save Note ID
        if(mNoteID != null) {
            outState.putInt(NOTE_ID, mNoteID);
        }

        // Save TextViews
        outState.putString(NOTE_HEADLINE, mHeadline.getText().toString());
        outState.putString(NOTE_TEXT, mText.getText().toString());
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