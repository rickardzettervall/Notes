package tech.zettervall.notes.models;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import tech.zettervall.notes.utils.DateTimeUtil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class NoteTest {

    private String title1, title2, text1, text2;
    private long creationEpoch, modifiedEpoch, notificationEpoch;
    private ArrayList<Integer> tagIDs;

    /**
     * Set fields which will be used in various tests.
     */
    @Before
    public void init() {
        title1 = "this is the title";
        title2 = "This is the title";
        text1 = "this is the text";
        text2 = "This is the text";
        creationEpoch = DateTimeUtil.getCurrentEpoch();
        modifiedEpoch = DateTimeUtil.getCurrentEpoch();
        notificationEpoch = -1;
        tagIDs = new ArrayList<>();
    }

    /**
     * Make sure title and text is changed to uppercase.
     */
    @Test
    public void isTitleAndText_upperCased_returnTrue() {
        Note note = new Note(1, title1, text1, null, tagIDs, creationEpoch,
                modifiedEpoch, notificationEpoch, false, false);

        assertEquals(note.getTitle(), title2);
        assertEquals(note.getText(), text2);
        System.out.println("The title and text was formatted correctly!");
    }

    /**
     * Compare two identical Notes.
     */
    @Test
    public void isNotesEqual_identical_returnTrue() {
        Note note1 = new Note(1, title1, text1, null, tagIDs, creationEpoch,
                modifiedEpoch, notificationEpoch, false, false);
        Note note2 = new Note(1, title1, text1, null, tagIDs, creationEpoch,
                modifiedEpoch, notificationEpoch, false, false);

        assertEquals(note1, note2);
        System.out.println("The Notes are equal!");
    }

    /**
     * Compare two Notes with different ids.
     */
    @Test
    public void isNotesEqual_differentIds_returnFalse() {
        Note note1 = new Note(1, title1, text1, null, tagIDs, creationEpoch,
                modifiedEpoch, notificationEpoch, false, false);
        Note note2 = new Note(2, title1, text1, null, tagIDs, creationEpoch,
                modifiedEpoch, notificationEpoch, false, false);

        assertNotEquals(note1, note2);
        System.out.println("The Notes are not equal!");
    }
}
