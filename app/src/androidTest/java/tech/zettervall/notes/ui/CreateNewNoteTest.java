package tech.zettervall.notes.ui;

import android.content.pm.ActivityInfo;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import tech.zettervall.mNotes.R;
import tech.zettervall.notes.MainActivity;
import tech.zettervall.notes.NoteActivity;
import tech.zettervall.notes.repositories.NoteRepository;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@RunWith(AndroidJUnit4.class)
public class CreateNewNoteTest {

    @Rule
    public ActivityTestRule<MainActivity> mMainActivity = new ActivityTestRule<>(MainActivity.class);
    private boolean isTablet;

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

    @Before
    public void init() {
        isTablet = mMainActivity.getActivity().getResources().getBoolean(R.bool.isTablet);
        Intents.init();
    }

    @After
    public void release() {
        // Delete all Notes from db
        NoteRepository.getInstance(mMainActivity.getActivity().getApplication()).deleteAllNotes();
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

        // Go back
        ViewInteraction appCompatImageButton = onView(
                allOf(withContentDescription("Navigate up"),
                        childAtPosition(
                                allOf(withId(R.id.toolbar),
                                        childAtPosition(
                                                withId(R.id.app_bar_layout),
                                                0)),
                                1),
                        isDisplayed()));
        appCompatImageButton.perform(click());

        // Check that Note was inserted into db
        ViewInteraction linearLayout = onView(
                allOf(childAtPosition(
                        allOf(withId(R.id.fragment_notelist_recyclerview),
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.RelativeLayout.class),
                                        0)),
                        0),
                        isDisplayed()));
        linearLayout.check(matches(isDisplayed()));
    }
}
