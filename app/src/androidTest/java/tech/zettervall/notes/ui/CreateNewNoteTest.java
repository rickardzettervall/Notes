package tech.zettervall.notes.ui;

import android.content.pm.ActivityInfo;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import tech.zettervall.mNotes.R;
import tech.zettervall.notes.MainActivity;
import tech.zettervall.notes.NoteActivity;
import tech.zettervall.notes.TestHelper;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static tech.zettervall.notes.RecyclerViewAssertions.itemViewMatches;

@RunWith(AndroidJUnit4.class)
public class CreateNewNoteTest {

    @Rule
    public ActivityTestRule<MainActivity> mMainActivity = new ActivityTestRule<>(MainActivity.class);
    private boolean isTablet;

    @Before
    public void init() {
        Intents.init();
        isTablet = mMainActivity.getActivity().getResources().getBoolean(R.bool.isTablet);
        TestHelper.ClearDb(mMainActivity.getActivity());
        TestHelper.sleepThread(250);
    }

    @After
    public void release() {
        TestHelper.ClearDb(mMainActivity.getActivity());
    }

    @Test
    public void createNewNote() {
        String title = "Abc",
                text = "Def";

        // Click FAB
        onView(withId(R.id.fragment_notelist_fab)).perform(click());

        // Check that NoteActivity was opened
        if (!isTablet) {
            intended(hasComponent(NoteActivity.class.getName()));
        }

        // Put title and text
        onView(withId(R.id.fragment_note_title_edittext)).perform(typeText(title));
        onView(withId(R.id.fragment_note_text_edittext)).perform(typeText(text));

        // Check that values survive rotation
        mMainActivity.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        mMainActivity.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        onView(withId(R.id.fragment_note_title_edittext)).check(matches(withText(title)));
        onView(withId(R.id.fragment_note_text_edittext)).check(matches(withText(text)));

        if (!isTablet) { // Go back
            onView(withContentDescription("Navigate up")).perform(click());
        } else { // Save
            onView(withId(R.id.fragment_note_fab)).perform(click());
        }

        // Check that Note was inserted into db and is now displayed in adapter
        onView(withId(R.id.fragment_notelist_recyclerview))
                .check(itemViewMatches(0, R.id.list_note_title_textview, withText(title)))
                .check(itemViewMatches(0, R.id.list_note_text_textview, withText(text)));
    }
}
