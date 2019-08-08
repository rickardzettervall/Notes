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
import tech.zettervall.notes.models.Tag;

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
                .getBoolean(getString(R.string.dark_theme_key), false);
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
     * Get NoteFragment.
     *
     * @param note        Clicked Note to send to Fragment, set null for new Note
     * @param setFavorite Determines if the new Note should be a favorite on creation
     * @param tag         Used to set Tag for Note on creation
     */
    public NoteFragment getNoteFragment(@Nullable Note note, boolean setFavorite, @Nullable Tag tag) {
        NoteFragment noteFragment = new NoteFragment();
        Bundle bundle = new Bundle();
        if (note != null) {
            bundle.putParcelable(Constants.NOTE, Parcels.wrap(note));
        } else {
            bundle.putBoolean(Constants.NOTE_FAVORITE, setFavorite);
            bundle.putParcelable(Constants.TAG, Parcels.wrap(tag));
        }
        noteFragment.setArguments(bundle);
        return noteFragment;
    }

    /**
     * Get NotesByTagFragment with Bundle.
     *
     * @param tag Tag to bundle into Fragment
     */
    public NotesByTagFragment getNotesByTagFragment(Tag tag) {
        NotesByTagFragment notesByTagFragment = new NotesByTagFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.TAG, Parcels.wrap(tag));
        notesByTagFragment.setArguments(bundle);
        return notesByTagFragment;
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
     * Set FavoritesFragment.
     */
    public void setFavoritesFragment(FavoritesFragment favoritesFragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_list, favoritesFragment, Constants.FRAGMENT_FAVORITES)
                .commit();
    }

    /**
     * Set RemindersFragment.
     */
    public void setRemindersFragment(RemindersFragment remindersFragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_list, remindersFragment, Constants.FRAGMENT_REMINDERS)
                .commit();
    }

    /**
     * Set TagsFragment.
     */
    public void setTagsFragment(TagsFragment tagsFragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_list, tagsFragment, Constants.FRAGMENT_TAGS)
                .commit();
    }

    /**
     * Set TrashFragment.
     */
    public void setTrashFragment(TrashFragment trashFragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_list, trashFragment, Constants.FRAGMENT_TRASH)
                .commit();
    }

    /**
     * Set NoteFragment
     */
    public void setNoteFragment(NoteFragment noteFragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.activity_note_framelayout, noteFragment, Constants.FRAGMENT_NOTE)
                .commit();
    }

    /**
     * Set NotesByTagFragment.
     */
    public void setNotesByTagFragment(NotesByTagFragment notesByTagFragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_list, notesByTagFragment, Constants.FRAGMENT_NOTES_BY_TAG)
                .commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_all_notes: // Set Fragment or launch activity when outside MainActivity
                if (this instanceof MainActivity) {
                    setNoteListFragment(new AllNotesFragment());
                } else {
                    startActivity(new Intent(this, MainActivity.class));
                }
                break;
            case R.id.nav_favorites: // Set Fragment or launch activity when outside MainActivity
                if (this instanceof MainActivity) {
                    setFavoritesFragment(new FavoritesFragment());
                } else {
                    startActivity(new Intent(this, MainActivity.class));
                }
                break;
            case R.id.nav_reminders: // Set Fragment or launch activity when outside MainActivity
                if (this instanceof MainActivity) {
                    setRemindersFragment(new RemindersFragment());
                } else {
                    startActivity(new Intent(this, MainActivity.class));
                }
                break;
            case R.id.nav_tags: // Launch Activity
                if (this instanceof MainActivity) {
                    setTagsFragment(new TagsFragment());
                } else {
                    startActivity(new Intent(this, MainActivity.class));
                }
                break;
            case R.id.nav_trash: // Launch Activity
                if (this instanceof MainActivity) {
                    setTrashFragment(new TrashFragment());
                } else {
                    startActivity(new Intent(this, MainActivity.class));
                }
                break;
            case R.id.nav_settings: // Launch Activity
                startActivity(new Intent(this, SettingsActivity.class));
                break;
        }
        return true;
    }
}
