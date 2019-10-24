package tech.zettervall.notes;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import java.util.ArrayList;
import java.util.List;

import tech.zettervall.notes.models.Note;
import tech.zettervall.notes.utils.DateTimeUtil;

public abstract class TestHelper {

    public static final String NOTE_1_TITLE = "espresso (n1)";
    public static final String NOTE_1_TEXT = "testing is great!";
    public static final String NOTE_2_TITLE = "apartment (n2)";
    public static final String NOTE_2_TEXT = "complex";
    public static final String NOTE_3_TITLE = "balls of steel (n3)";
    public static final String NOTE_3_TEXT = "no comment";
    public static final String NOTE_TRASHED_TITLE = "trashed title";
    public static final String NOTE_TRASHED_TEXT = "trashed_text";

    public static Context getContext() {
        return InstrumentationRegistry.getInstrumentation().getTargetContext();
    }

    /**
     * Note 1 is the most recently modified and Note 3 the oldest.
     */
    public static Note[] getMockNotes() {
        Note[] notes = new Note[3];
        List<Integer> tagIDs = new ArrayList<>();
        tagIDs.add(1);
        tagIDs.add(2);
        Note note1 = new Note(
                NOTE_1_TITLE,
                NOTE_1_TEXT,
                null,
                tagIDs,
                DateTimeUtil.getCurrentEpoch(),
                DateTimeUtil.getCurrentEpoch(),
                -1L,
                false,
                false);
        Note note2 = new Note(
                NOTE_2_TITLE,
                NOTE_2_TEXT,
                null,
                tagIDs,
                DateTimeUtil.getCurrentEpoch(),
                DateTimeUtil.getCurrentEpoch() - 5000L,
                -1L,
                false,
                false);
        Note note3 = new Note(
                NOTE_3_TITLE,
                NOTE_3_TEXT,
                null,
                new ArrayList<>(),
                DateTimeUtil.getCurrentEpoch(),
                DateTimeUtil.getCurrentEpoch() - 10000L,
                -1L,
                false,
                false);
        notes[0] = note1;
        notes[1] = note2;
        notes[2] = note3;
        return notes;
    }

    /**
     * Get Note which is flagged as trash.
     */
    public static Note getTrashedNote() {
        return new Note(
                NOTE_TRASHED_TITLE,
                NOTE_TRASHED_TEXT,
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
