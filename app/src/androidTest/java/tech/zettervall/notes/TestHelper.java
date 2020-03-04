package tech.zettervall.notes;

import android.app.Activity;
import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import java.util.ArrayList;
import java.util.List;

import tech.zettervall.notes.models.Note;
import tech.zettervall.notes.repositories.NoteRepository;
import tech.zettervall.notes.utils.DateTimeUtil;

public abstract class TestHelper {

    public static final String NOTE_ALPHA_TITLE = "Alpha";
    public static final String NOTE_ALPHA_TEXT = "Lorem ipsum dolor sit amet, consectetur adipiscing elit," +
            " sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.";
    public static final String NOTE_BETA_TITLE = "Beta";
    public static final String NOTE_BETA_TEXT = "Ut enim ad minim veniam, quis nostrud exercitation ullamco" +
            " laboris nisi ut aliquip ex ea commodo consequat.";
    public static final String NOTE_CHARLIE_TITLE = "Charlie";
    public static final String NOTE_CHARLIE_TEXT = "Duis aute irure dolor in reprehenderit in voluptate velit" +
            " esse cillum dolore eu fugiat nulla pariatur.";
    public static final String NOTE_DELTA_TITLE = "Delta";
    public static final String NOTE_DELTA_TEXT = "Excepteur sint occaecat cupidatat non proident, sunt" +
            " in culpa qui officia deserunt mollit anim id est laborum.";

    public static Context getContext() {
        return InstrumentationRegistry.getInstrumentation().getTargetContext();
    }

    public static void ClearDb(Activity activity) {
        NoteRepository.getInstance(activity.getApplication()).deleteAllNotes();
    }

    /**
     * Note Alpha is the oldest and Note Charlie the most recently modified.
     */
    public static Note[] getMockNotes() {
        Note[] notes = new Note[3];
        List<Integer> tagIDs = new ArrayList<>();
        tagIDs.add(1);
        tagIDs.add(2);
        Note alpha = new Note(
                NOTE_ALPHA_TITLE,
                NOTE_ALPHA_TEXT,
                null,
                tagIDs,
                DateTimeUtil.getCurrentEpoch() - 10000L,
                DateTimeUtil.getCurrentEpoch() - 10000L,
                -1L,
                false,
                false);
        Note beta = new Note(
                NOTE_BETA_TITLE,
                NOTE_BETA_TEXT,
                null,
                tagIDs,
                DateTimeUtil.getCurrentEpoch() - 5000L,
                DateTimeUtil.getCurrentEpoch() - 5000L,
                -1L,
                false,
                true);
        Note charlie = new Note(
                NOTE_CHARLIE_TITLE,
                NOTE_CHARLIE_TEXT,
                null,
                new ArrayList<>(),
                DateTimeUtil.getCurrentEpoch(),
                DateTimeUtil.getCurrentEpoch(),
                -1L,
                false,
                false);
        notes[0] = alpha;
        notes[1] = beta;
        notes[2] = charlie;
        return notes;
    }

    /**
     * Get Note which is flagged as trash.
     */
    public static Note getTrashedNote() {
        return new Note(
                NOTE_DELTA_TITLE,
                NOTE_DELTA_TEXT,
                null,
                new ArrayList<>(),
                DateTimeUtil.getCurrentEpoch(),
                DateTimeUtil.getCurrentEpoch(),
                -1L,
                true,
                false);
    }

    /**
     * Sleeps Thread.
     */
    public static void sleepThread(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
