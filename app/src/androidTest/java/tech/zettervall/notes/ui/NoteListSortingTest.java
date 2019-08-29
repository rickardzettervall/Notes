package tech.zettervall.notes.ui;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import tech.zettervall.mNotes.R;
import tech.zettervall.notes.Constants;
import tech.zettervall.notes.MainActivity;
import tech.zettervall.notes.TestHelper;
import tech.zettervall.notes.repositories.NoteRepository;
import tech.zettervall.notes.utils.StringUtil;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;

@RunWith(AndroidJUnit4.class)
public class NoteListSortingTest {

    public static final String NOTE_1_TITLE = "espresso";
    public static final String NOTE_1_TEXT = "testing is great!";
    public static final String NOTE_2_TITLE = "apartment";
    public static final String NOTE_2_TEXT = "complex";
    public static final String NOTE_3_TITLE = "balls of steel";
    public static final String NOTE_3_TEXT = "no comment";
    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);
    private SharedPreferences mSharedPreferences;
    private NoteRepository mNoteRepository;

    @Before
    public void init() {
        // Get SharedPreferences
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(TestHelper.getContext());
        // Get NoteRepository
        mNoteRepository = NoteRepository.getInstance(mActivityTestRule.getActivity().getApplication());
        // Populate db with mock Notes
        mNoteRepository.insertNotes(TestHelper.getMockNotes());
    }

    @After
    public void release() {
        // Clear db
        mNoteRepository.deleteAllNotes();
    }

    /**
     * Sort alphabetically (Ascending, A,B,C).
     */
    @Test
    public void sortAlphabeticallyASC_ReturnTrue() {
        // Set SharedPreferences (Sort alphabetically, ASC)
        mSharedPreferences.edit()
                .putInt(Constants.SORT_TYPE_KEY, Constants.SORT_TYPE_ALPHABETICALLY)
                .putBoolean(Constants.SORT_FAVORITES_ON_TOP_KEY, false)
                .putInt(Constants.SORT_DIRECTION_KEY, Constants.SORT_DIRECTION_ASC)
                .commit();

        // Click first position in RecyclerView
        onView(withId(R.id.fragment_notelist_recyclerview))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        // Check that clicked Note matches Note (2) title/text
        onView(withId(R.id.fragment_note_title_edittext))
                .check(matches(withText(StringUtil.setFirstCharUpperCase(TestHelper.NOTE_2_TITLE))));
        onView(withId(R.id.fragment_note_text_edittext))
                .check(matches(withText(StringUtil.setFirstCharUpperCase(TestHelper.NOTE_2_TEXT))));
    }

    /**
     * Sort alphabetically (Descending, C,B,A).
     */
    @Test
    public void sortAlphabeticallyDESC_ReturnTrue() {
        // Set SharedPreferences (Sort alphabetically, DESC)
        mSharedPreferences.edit()
                .putInt(Constants.SORT_TYPE_KEY, Constants.SORT_TYPE_ALPHABETICALLY)
                .putBoolean(Constants.SORT_FAVORITES_ON_TOP_KEY, false)
                .putInt(Constants.SORT_DIRECTION_KEY, Constants.SORT_DIRECTION_DESC)
                .commit();

        // Click first position in RecyclerView
        onView(withId(R.id.fragment_notelist_recyclerview))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        // Check that clicked Note matches Note (1) title/text
        onView(withId(R.id.fragment_note_title_edittext))
                .check(matches(withText(StringUtil.setFirstCharUpperCase(TestHelper.NOTE_1_TITLE))));
        onView(withId(R.id.fragment_note_text_edittext))
                .check(matches(withText(StringUtil.setFirstCharUpperCase(TestHelper.NOTE_1_TEXT))));
    }

    /**
     * Sort by creation date (Ascending).
     */
    @Test
    public void sortByCreationDateASC_ReturnTrue() {
        // Set SharedPreferences (Sort by Creation Date, ASC)
        mSharedPreferences.edit()
                .putInt(Constants.SORT_TYPE_KEY, Constants.SORT_TYPE_CREATION_DATE)
                .putBoolean(Constants.SORT_FAVORITES_ON_TOP_KEY, false)
                .putInt(Constants.SORT_DIRECTION_KEY, Constants.SORT_DIRECTION_ASC)
                .commit();

        // Click first position in RecyclerView
        onView(withId(R.id.fragment_notelist_recyclerview))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        // Check that clicked Note matches Note (3) title/text
        onView(withId(R.id.fragment_note_title_edittext))
                .check(matches(withText(is(StringUtil.setFirstCharUpperCase(TestHelper.NOTE_3_TITLE)))));
//        onView(withId(R.id.fragment_note_text_textview))
//                .check(matches(withText(is(StringUtil.setFirstCharUpperCase(TestHelper.NOTE_3_TEXT)))));
    }
}
