package tech.zettervall.notes;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

import org.parceler.Parcels;

import tech.zettervall.mNotes.R;
import tech.zettervall.notes.models.Note;
import tech.zettervall.notes.models.Tag;

/**
 * Base Activity with commonly used methods.
 */
public abstract class BaseActivity extends AppCompatActivity {

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
    public static NoteFragment getNoteFragment(@Nullable Note note, boolean setFavorite, @Nullable Tag tag) {
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
    public void setAllNotesFragment(AllNotesFragment allNotesFragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_list, allNotesFragment, Constants.FRAGMENT_ALL_NOTES)
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
    public void setNotesByTagFragment(NotesByTagFragment notesByTagFragment, String tag) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_list, notesByTagFragment, tag)
                .commit();
    }
}
