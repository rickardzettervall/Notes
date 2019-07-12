package tech.zettervall.notes.models;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;

import tech.zettervall.notes.utils.DateTimeUtil;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class NoteTest {

    private String title, text;
    private long creationEpoch, modifiedEpoch_1, modifiedEpoch_2, notificationEpoch;

    /**
     * Set fields which will be used in various tests.
     */
    @Before
    public void setup() {
        title = "this is the title";
        text = "this is the text";
        creationEpoch = DateTimeUtil.getCurrentEpoch();
        modifiedEpoch_1 = DateTimeUtil.getCurrentEpoch();
        modifiedEpoch_2 = DateTimeUtil.getCurrentEpoch() + 1000L;
        notificationEpoch = -1;
    }

    /**
     * Make sure title and text is changed to uppercase.
     */
    @Test
    public void isTitleAndText_upperCased_returnTrue() {
        Note note = new Note(1, title, text, new ArrayList<String>(), creationEpoch,
                modifiedEpoch_1, notificationEpoch, false, false);

        String title1 = title,
                title2 = note.getTitle(),
                text1 = text,
                text2 = note.getText();

        title1 = title1.substring(0, 1).toUpperCase() + title1.substring(1);
        text1 = text1.substring(0, 1).toUpperCase() + text1.substring(1);

        assertEquals(title1, title2);
        assertEquals(text1, text2);
        System.out.println("The title and text was formatted correctly!");
    }

    /**
     * Compare two identical Notes.
     */
    @Test
    public void isNotesEqual_identical_returnTrue() {
        Note note1 = new Note(1, title, text, new ArrayList<String>(), creationEpoch,
                modifiedEpoch_1, notificationEpoch, false, false);
        Note note2 = new Note(1, title, text, new ArrayList<String>(), creationEpoch,
                modifiedEpoch_1, notificationEpoch, false, false);

        assertEquals(note1, note2);
        System.out.println("The Notes are equal!");
    }

    /**
     * Compare two Notes which represents the same Note but which has been updated.
     */
    @Test
    public void isNotesEqual_updated_returnFalse() {
        Note note1 = new Note(1, title, text, new ArrayList<String>(), creationEpoch,
                modifiedEpoch_1, notificationEpoch, false, false);
        Note note2 = new Note(1, title, text, new ArrayList<String>(), creationEpoch,
                modifiedEpoch_2, notificationEpoch, false, false);

        assertNotEquals(note1, note2);
        System.out.println("The Notes are not equal!");
    }

    /**
     * Compare two Notes with different ids.
     */
    @Test
    public void isNotesEqual_differentIds_returnFalse() {
        Note note1 = new Note(1, title, text, new ArrayList<String>(), creationEpoch,
                modifiedEpoch_1, notificationEpoch, false, false);
        Note note2 = new Note(2, title, text, new ArrayList<String>(), creationEpoch,
                modifiedEpoch_1, notificationEpoch, false, false);

        assertNotEquals(note1, note2);
        System.out.println("The Notes are not equal!");
    }
}
