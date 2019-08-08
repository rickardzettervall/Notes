package tech.zettervall.notes;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.widget.Toolbar;

import org.parceler.Parcels;

import tech.zettervall.mNotes.R;
import tech.zettervall.notes.models.Note;
import tech.zettervall.notes.models.Tag;

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
            if (getIntent().getExtras() != null) { // Clicked Note or new Note from Favorites/NotesByTag Fragment
                Note note = Parcels.unwrap(getIntent().getExtras().getParcelable(Constants.NOTE));
                Tag tag = Parcels.unwrap(getIntent().getExtras().getParcelable(Constants.TAG));
                boolean favorite = getIntent().getExtras().getBoolean(Constants.NOTE_FAVORITE);
                if (favorite) { // FAB clicked in FavoritesFragment
                    setNoteFragment(getNoteFragment(null, true, null));
                } else if (tag != null) { // FAB clicked in NotesByTagFragment
                    setNoteFragment(getNoteFragment(null, false, tag));
                } else if (note != null) { // Note clicked in any Fragment
                    setNoteFragment(getNoteFragment(note, false, null));
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