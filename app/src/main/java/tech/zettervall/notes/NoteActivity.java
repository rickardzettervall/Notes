package tech.zettervall.notes;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.widget.Toolbar;

import org.parceler.Parcels;

import tech.zettervall.mNotes.R;
import tech.zettervall.notes.models.Note;

public class NoteActivity extends BaseActivity {

    private static final String TAG = NoteActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        // Set ToolBar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        // Set Fragments
        if (savedInstanceState == null) {
            if (getIntent().getExtras() != null) { // Clicked Note or new Note from Favorites Fragment
                if (getIntent().getExtras().getBoolean(Constants.NOTE_FAVORITE)) {
                    setNoteFragment(getNoteFragment(null, true));
                } else if (getIntent().getExtras().getParcelable(Constants.NOTE) != null) {
                    Note note = Parcels.unwrap(getIntent().getExtras().getParcelable(Constants.NOTE));
                    setNoteFragment(getNoteFragment(note, false));
                } else {
                    setNoteFragment(new NoteFragment());
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_favoritize:
                // IMPLEMENT IN FRAGMENT
                break;
            case R.id.action_delete:
                // IMPLEMENT IN FRAGMENT
                break;
        }
        return false;
    }
}