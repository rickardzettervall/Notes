package tech.zettervall.notes.ui;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import tech.zettervall.mNotes.R;
import tech.zettervall.notes.MainActivity;
import tech.zettervall.notes.TestHelper;
import tech.zettervall.notes.repositories.NoteRepository;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static tech.zettervall.notes.RecyclerViewAssertions.itemViewMatches;

@RunWith(AndroidJUnit4.class)
public class NoteListSortingTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);
    private NoteRepository mNoteRepository;

    @Before
    public void init() {
        mNoteRepository = NoteRepository.getInstance(mActivityTestRule.getActivity().getApplication());
        mNoteRepository.deleteAllNotes();
        mNoteRepository.insertNotes(TestHelper.getMockNotes());
        TestHelper.sleepThread(250);
    }

    @After
    public void release() {
        mNoteRepository.deleteAllNotes();
    }

    /**
     * Sort Notes.
     */
    @Test
    public void sort() {

        /*
         * Sort Alphabetically, ASC
         */
        onView(withId(R.id.action_sort)).perform(click());
        onView(withId(R.id.dialog_sort_type_alphabetically_radiobutton)).perform(click());
        onView(withText(TestHelper.getContext().getString(R.string.sort_by_ascending))).perform(click());

        // Check that first position is Note Alpha and third position is Note Charlie
        onView(withId(R.id.fragment_notelist_recyclerview))
                .check(itemViewMatches(
                        0,
                        R.id.list_note_title_textview,
                        withText(TestHelper.NOTE_ALPHA_TITLE)))
                .check(itemViewMatches(
                        2,
                        R.id.list_note_title_textview,
                        withText(TestHelper.NOTE_CHARLIE_TITLE)));

        /*
         * Sort Alphabetically, DESC
         */
        onView(withId(R.id.action_sort)).perform(click());
        onView(withId(R.id.dialog_sort_type_alphabetically_radiobutton)).perform(click());
        onView(withText(TestHelper.getContext().getString(R.string.sort_by_descending))).perform(click());

        // Check that first position is Note Charlie and third position is Note Alpha
        onView(withId(R.id.fragment_notelist_recyclerview))
                .check(itemViewMatches(
                        0,
                        R.id.list_note_title_textview,
                        withText(TestHelper.NOTE_CHARLIE_TITLE)))
                .check(itemViewMatches(
                        2,
                        R.id.list_note_title_textview,
                        withText(TestHelper.NOTE_ALPHA_TITLE)));

        /*
         * Sort by Creation Date, ASC
         */
        onView(withId(R.id.action_sort)).perform(click());
        onView(withId(R.id.dialog_sort_type_creation_date_radiobutton)).perform(click());
        onView(withText(TestHelper.getContext().getString(R.string.sort_by_ascending))).perform(click());

        // Check that first position is Note Alpha and third position is Note Charlie
        onView(withId(R.id.fragment_notelist_recyclerview))
                .check(itemViewMatches(
                        0,
                        R.id.list_note_title_textview,
                        withText(TestHelper.NOTE_ALPHA_TITLE)))
                .check(itemViewMatches(
                        2,
                        R.id.list_note_title_textview,
                        withText(TestHelper.NOTE_CHARLIE_TITLE)));

        /*
         * Sort by Creation Date, DESC
         */
        onView(withId(R.id.action_sort)).perform(click());
        onView(withId(R.id.dialog_sort_type_creation_date_radiobutton)).perform(click());
        onView(withText(TestHelper.getContext().getString(R.string.sort_by_descending))).perform(click());

        // Check that first position is Note Charlie and third position is Note Alpha
        onView(withId(R.id.fragment_notelist_recyclerview))
                .check(itemViewMatches(
                        0,
                        R.id.list_note_title_textview,
                        withText(TestHelper.NOTE_CHARLIE_TITLE)))
                .check(itemViewMatches(
                        2,
                        R.id.list_note_title_textview,
                        withText(TestHelper.NOTE_ALPHA_TITLE)));

        /*
         * Sort by Modified Date, ASC
         */
        onView(withId(R.id.action_sort)).perform(click());
        onView(withId(R.id.dialog_sort_type_modified_date_radiobutton)).perform(click());
        onView(withText(TestHelper.getContext().getString(R.string.sort_by_ascending))).perform(click());

        // Check that first position is Note Alpha and third position is Note Charlie
        onView(withId(R.id.fragment_notelist_recyclerview))
                .check(itemViewMatches(
                        0,
                        R.id.list_note_title_textview,
                        withText(TestHelper.NOTE_ALPHA_TITLE)))
                .check(itemViewMatches(
                        2,
                        R.id.list_note_title_textview,
                        withText(TestHelper.NOTE_CHARLIE_TITLE)));

        /*
         * Sort by Modified Date, DESC
         */
        onView(withId(R.id.action_sort)).perform(click());
        onView(withId(R.id.dialog_sort_type_modified_date_radiobutton)).perform(click());
        onView(withText(TestHelper.getContext().getString(R.string.sort_by_descending))).perform(click());

        // Check that first position is Note Charlie and third position is Note Alpha
        onView(withId(R.id.fragment_notelist_recyclerview))
                .check(itemViewMatches(
                        0,
                        R.id.list_note_title_textview,
                        withText(TestHelper.NOTE_CHARLIE_TITLE)))
                .check(itemViewMatches(
                        2,
                        R.id.list_note_title_textview,
                        withText(TestHelper.NOTE_ALPHA_TITLE)));

        /*
         * Sort Alphabetically, ASC (Favorites on top)
         */
        onView(withId(R.id.action_sort)).perform(click());
        onView(withId(R.id.dialog_sort_type_alphabetically_radiobutton)).perform(click());
        onView(withId(R.id.dialog_sort_favorites_on_top_checkbox)).perform(click());
        onView(withText(TestHelper.getContext().getString(R.string.sort_by_ascending))).perform(click());

        /* Check that first position is Note Beta, second position is Note Alpha
         * and that third position is Note Charlie */
        onView(withId(R.id.fragment_notelist_recyclerview))
                .check(itemViewMatches(
                        0,
                        R.id.list_note_title_textview,
                        withText(TestHelper.NOTE_BETA_TITLE)))
                .check(itemViewMatches(
                        1,
                        R.id.list_note_title_textview,
                        withText(TestHelper.NOTE_ALPHA_TITLE)))
                .check(itemViewMatches(
                        2,
                        R.id.list_note_title_textview,
                        withText(TestHelper.NOTE_CHARLIE_TITLE)));
    }
}
