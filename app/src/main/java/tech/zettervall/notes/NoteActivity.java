package tech.zettervall.notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import tech.zettervall.mNotes.R;

public class NoteActivity extends BaseActivity {

    private static final String TAG = NoteActivity.class.getSimpleName();
    private Integer mNoteID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        // Set title
        setTitle(R.string.note_new);

        // Retrieve existing data
        if (getIntent().getExtras() != null) {
            mNoteID = getIntent().getExtras().getInt(Constants.NOTE_ID);
            // Change title
            setTitle(R.string.note_modify);
        }

        // Fragment handling
        if(mNoteID != null) {
            setNoteFragment(getNoteFragmentWithBundle(mNoteID));
        } else {
            setNoteFragment(new NoteFragment());
        }
    }

    @Override
    public NoteFragment getNoteFragmentWithBundle(int noteID) {
        return super.getNoteFragmentWithBundle(noteID);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save Note ID
        if(mNoteID != null) {
            outState.putInt(Constants.NOTE_ID, mNoteID);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                DialogInterface.OnClickListener dialogClickListener =
                        new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                // TODO: enable deletion here
//                                NoteRepository.getInstance(getApplication()).deleteNote(mNote);
//                                if(mNote.getHeadline() != null && !mNote.getHeadline().isEmpty()) {
//                                    Toast.makeText(NoteActivity.this,
//                                            "Deleted '" + mNote.getHeadline() + "'",
//                                            Toast.LENGTH_SHORT).show();
//                                } else {
//                                    Toast.makeText(NoteActivity.this,
//                                            "Deleted Note",
//                                            Toast.LENGTH_SHORT).show();
//                                }
//                                // Set delete field to true so that the Note isn't saved in onPause()
//                                delete = true;
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
        }
        return super.onOptionsItemSelected(item);
    }
}