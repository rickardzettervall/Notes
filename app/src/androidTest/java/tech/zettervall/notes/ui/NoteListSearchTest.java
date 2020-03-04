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
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static tech.zettervall.notes.RecyclerViewAssertions.itemCountMatches;
import static tech.zettervall.notes.RecyclerViewAssertions.itemViewMatches;

@RunWith(AndroidJUnit4.class)
public class NoteListSearchTest {

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
     * Search Notes.
     */
    @Test
    public void search() {
        onView(withId(R.id.action_search)).perform(click());
        onView(withId(R.id.search_src_text)).perform(replaceText("beta"));

        // Check that only Note Beta is displayed
        onView(withId(R.id.fragment_notelist_recyclerview))
                .check(itemCountMatches(1))
                .check(itemViewMatches(
                        0,
                        R.id.list_note_title_textview,
                        withText(TestHelper.NOTE_BETA_TITLE)));
    }
}
