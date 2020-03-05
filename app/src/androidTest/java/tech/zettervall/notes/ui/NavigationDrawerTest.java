package tech.zettervall.notes.ui;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import tech.zettervall.mNotes.R;
import tech.zettervall.notes.MainActivity;
import tech.zettervall.notes.SettingsActivity;
import tech.zettervall.notes.TestHelper;
import tech.zettervall.notes.repositories.NoteRepository;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static tech.zettervall.notes.RecyclerViewAssertions.itemCountMatches;
import static tech.zettervall.notes.RecyclerViewAssertions.itemViewMatches;

@RunWith(AndroidJUnit4.class)
public class NavigationDrawerTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);
    private NoteRepository mNoteRepository;

    @Before
    public void init() {
        Intents.init();
        mNoteRepository = NoteRepository.getInstance(mActivityTestRule.getActivity().getApplication());
        mNoteRepository.deleteAllNotes();
        mNoteRepository.insertNotes(TestHelper.getMockNotes());
        mNoteRepository.insertNote(TestHelper.getTrashedNote());
        TestHelper.sleepThread(250);
    }

    @After
    public void release() {
        Intents.release();
        mNoteRepository.deleteAllNotes();
    }

    /**
     * Test that correct Fragment, List or Activity is loaded when a Navigation Drawer item is clicked.
     */
    @Test
    public void navigation() {
        // All Notes
        onView(withId(R.id.fragment_notelist_recyclerview))
                .check(itemCountMatches(3));

        // Favorites
        onView(withContentDescription(TestHelper.getContext().getString(R.string.cont_nav_drawer_open))).perform(click());
        onView(withId(R.id.nav_favorites)).perform(click());
        onView(withId(R.id.fragment_notelist_recyclerview))
                .check(itemCountMatches(1))
                .check(itemViewMatches(0, R.id.list_note_title_textview, withText(TestHelper.NOTE_BETA_TITLE)));

        // Reminders
        onView(withContentDescription(TestHelper.getContext().getString(R.string.cont_nav_drawer_open))).perform(click());
        onView(withId(R.id.nav_reminders)).perform(click());
        onView(withId(R.id.fragment_notelist_recyclerview))
                .check(itemCountMatches(1))
                .check(itemViewMatches(0, R.id.list_note_title_textview, withText(TestHelper.NOTE_CHARLIE_TITLE)));

        // Check #Personal Tag
        onView(withContentDescription(TestHelper.getContext().getString(R.string.cont_nav_drawer_open))).perform(click());
        onView(withText("#" + TestHelper.getContext().getString(R.string.tag_personal))).perform(click());
        onView(withId(R.id.fragment_notelist_recyclerview))
                .check(itemCountMatches(2))
                .check(itemViewMatches(R.id.list_note_title_textview, withText(TestHelper.NOTE_ALPHA_TITLE)))
                .check(itemViewMatches(R.id.list_note_title_textview, withText(TestHelper.NOTE_BETA_TITLE)));

        // #Work Tag
        onView(withContentDescription(TestHelper.getContext().getString(R.string.cont_nav_drawer_open))).perform(click());
        onView(withText("#" + TestHelper.getContext().getString(R.string.tag_work))).perform(click());
        onView(withId(R.id.fragment_notelist_recyclerview))
                .check(itemCountMatches(1))
                .check(itemViewMatches(R.id.list_note_title_textview, withText(TestHelper.NOTE_ALPHA_TITLE)));

        // Tags
        onView(withContentDescription(TestHelper.getContext().getString(R.string.cont_nav_drawer_open))).perform(click());
        onView(allOf(childAtPosition(
                allOf(withId(R.id.design_navigation_view),
                        childAtPosition(
                                withId(R.id.nav_view),
                                0)),
                8),
                isDisplayed())).perform(click());
        onView(withId(R.id.fragment_taglist_recyclerview))
                .check(itemCountMatches(2))
                .check(itemViewMatches(R.id.list_tag_title_textview, withText("#" + TestHelper.getContext().getString(R.string.tag_personal))))
                .check(itemViewMatches(R.id.list_tag_title_textview, withText("#" + TestHelper.getContext().getString(R.string.tag_work))));

        // Trash
        onView(withContentDescription(TestHelper.getContext().getString(R.string.cont_nav_drawer_open))).perform(click());
        onView(withText(TestHelper.getContext().getString(R.string.action_trash))).perform(click());
        onView(withId(R.id.fragment_notelist_recyclerview))
                .check(itemCountMatches(1))
                .check(itemViewMatches(R.id.list_note_title_textview, withText(TestHelper.NOTE_DELTA_TITLE)));

        // Settings
        onView(withContentDescription(TestHelper.getContext().getString(R.string.cont_nav_drawer_open))).perform(click());
        onView(withText(TestHelper.getContext().getString(R.string.action_settings))).perform(click());
        intended(hasComponent(SettingsActivity.class.getName()));
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}