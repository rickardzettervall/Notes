package tech.zettervall.notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.parceler.Parcels;

import tech.zettervall.mNotes.R;
import tech.zettervall.notes.models.Note;
import tech.zettervall.notes.repositories.NoteRepository;
import tech.zettervall.notes.utils.DateTimeHelper;

public class NoteActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = NoteActivity.class.getSimpleName();
    private static final String NOTE_HEADLINE = "note_headline";
    private static final String NOTE_TEXT = "note_text";
    private static final String NOTE_ID = "note_id";
    private TextView mHeadline, mText;
    private FloatingActionButton mFab;
    private Integer mNoteID;
    private Note mNote;
    private boolean delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        // Set title
        setTitle(R.string.note_new);

        // Find Views
        mHeadline = findViewById(R.id.headline_tv);
        mText = findViewById(R.id.text_tv);
        mFab = findViewById(R.id.fab);

        // Set default Note
        mNote = new Note(Constants.TYPE_PLAIN,"", "");

        // Retrieve existing data
        if (getIntent().getExtras() != null) {
            // A already existing Note was clicked and the ID resides in Extras
            mNote = Parcels.unwrap(getIntent().getParcelableExtra(Constants.NOTE_PARCEL));
            // Change title
            setTitle(R.string.note_modify);
        } else if (savedInstanceState != null) {
            // Restore newly created Note data from savedInstanceState
            mNote.set_id(savedInstanceState.getInt(NOTE_ID));
            mNote.setHeadline(savedInstanceState.getString(NOTE_HEADLINE));
            mNote.setText(savedInstanceState.getString(NOTE_TEXT));
        }

        // Set Views
        mHeadline.setText(mNote.getHeadline());
        mText.setText(mNote.getText());

        // Set OnClickListeners
        mFab.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                finish();
                break;
        }
    }

    /**
     * Save Note to db
     */
    private void saveNote() {
        // Check for delete flag and that user actually entered any text
        if(!delete && (!mHeadline.getText().toString().isEmpty() ||
                !mText.getText().toString().isEmpty())) {
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveNote();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
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
        getMenuInflater().inflate(R.menu.menu_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_delete:
                DialogInterface.OnClickListener dialogClickListener =
                        new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                NoteRepository.getInstance(getApplication()).deleteNote(mNote);
                                if(mNote.getHeadline() != null && !mNote.getHeadline().isEmpty()) {
                                    Toast.makeText(NoteActivity.this,
                                            "Deleted '" + mNote.getHeadline() + "'",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(NoteActivity.this,
                                            "Deleted Note",
                                            Toast.LENGTH_SHORT).show();
                                }
                                // Set delete field to true so that the Note isn't saved in onPause()
                                delete = true;
                                finish();
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(getString(R.string.confirm_deletion))
                        .setPositiveButton(getString(R.string.delete), dialogClickListener)
                        .setNegativeButton(getString(R.string.abort), dialogClickListener).show();
                return true;
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}