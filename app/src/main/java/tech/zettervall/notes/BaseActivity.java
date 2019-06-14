package tech.zettervall.notes;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

import com.google.android.material.navigation.NavigationView;

import org.parceler.Parcels;

import tech.zettervall.mNotes.R;
import tech.zettervall.notes.models.Note;

/**
 * Base Activity with commonly used methods.
 */
public abstract class BaseActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    protected boolean mEnableDarkTheme, mIsTablet;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEnableDarkTheme = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(getString(R.string.enable_dark_theme_key), false);
        mIsTablet = getResources().getBoolean(R.bool.isTablet);

        // Set Theme
        setTheme();
    }

    /**
     * Set App theme.
     */
    public void setTheme() {
        if (mEnableDarkTheme) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    /**
     * Get NoteFragment bundled with Note.
     */
    public NoteFragment getNoteFragmentWithBundledNote(Note note) {
        // Create Bundle and Fragment
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.NOTE, Parcels.wrap(note));
        NoteFragment noteFragment = new NoteFragment();
        noteFragment.setArguments(bundle);
        return noteFragment;
    }

    /**
     * Set AllNotesFragment.
     */
    public void setNoteListFragment(AllNotesFragment allNotesFragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_list, allNotesFragment, Constants.FRAGMENT_NOTELIST)
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
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_all_notes:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.nav_favories:
                break;
            case R.id.nav_reminders:
                break;
            case R.id.nav_tags:
                break;
            case R.id.nav_trash:
                startActivity(new Intent(this, TrashActivity.class));
                break;
            case R.id.nav_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.nav_change_theme:
                if (mEnableDarkTheme) {
                    PreferenceManager.getDefaultSharedPreferences(this)
                            .edit().putBoolean(getString(R.string.enable_dark_theme_key), false)
                            .apply();
                } else {
                    PreferenceManager.getDefaultSharedPreferences(this)
                            .edit().putBoolean(getString(R.string.enable_dark_theme_key), true)
                            .apply();
                }
                setTheme();
                recreate();
                break;
        }
        return true;
    }
}
