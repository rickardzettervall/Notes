package tech.zettervall.notes;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import tech.zettervall.mNotes.R;

/**
 * Base Activity with commonly used methods.
 */
public abstract class BaseActivity extends AppCompatActivity {

    /**
     * Get NoteFragment bundled with note ID.
     */
    public NoteFragment getNoteFragmentWithBundle(int noteID) {
        // Create Bundle and Fragment
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.NOTE_ID, noteID);
        NoteFragment noteFragment = new NoteFragment();
        noteFragment.setArguments(bundle);
        return noteFragment;
    }

    /**
     * Set NoteListFragment.
     */
    public void setNoteListFragment(NoteListFragment noteListFragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_list, noteListFragment, Constants.FRAGMENT_NOTELIST)
                .commit();
    }

    /**
     * Set NoteFragment.
     */
    public void setNoteFragment(NoteFragment noteFragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_note, noteFragment, Constants.FRAGMENT_NOTE)
                .commit();
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
                return true;
            case R.id.action_about:
                startActivity(new Intent(this, AboutActivity.class));
                return true;
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
