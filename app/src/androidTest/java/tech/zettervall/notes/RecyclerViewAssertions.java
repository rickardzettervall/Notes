package tech.zettervall.notes;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.ViewAssertion;

import org.hamcrest.Matcher;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public abstract class RecyclerViewAssertions {

    /**
     * Assertion for specified view in select RecyclerView Adapter position.
     *
     * @param position    Position in RecyclerView Adapter
     * @param viewID      ID of view to match.
     * @param viewMatcher Espresso ViewMatcher for a descendant Adapter item
     * @return Espresso ViewAssertion to check against a RecyclerView
     */
    public static ViewAssertion itemViewMatches(int position,
                                                int viewID,
                                                final Matcher<View> viewMatcher) {
        assertNotNull(viewMatcher);

        return (view, noViewException) -> {
            if (noViewException != null) {
                throw noViewException;
            }

            assertTrue(view instanceof RecyclerView);
            RecyclerView recyclerView = (RecyclerView) view;
            RecyclerView.Adapter adapter = recyclerView.getAdapter();

            // Fail if position out of bounds
            if (position < 0 || position > (adapter.getItemCount() - 1)) {
                fail("Position [" + position + "] doesn't exist in Adapter");
                return;
            }

            int itemType = adapter.getItemViewType(position);
            RecyclerView.ViewHolder viewHolder = adapter.createViewHolder(recyclerView, itemType);
            adapter.bindViewHolder(viewHolder, position);

            View targetView = viewHolder.itemView.findViewById(viewID);

            if (viewMatcher.matches(targetView)) {
                return; // Found match
            }

            fail("No match found");
        };
    }

    /**
     * Assertion for specified view in any RecyclerView Adapter position.
     *
     * @param viewID      ID of view to match.
     * @param viewMatcher Espresso ViewMatcher for a descendant of any row in the Adapter
     * @return Espresso ViewAssertion to check against a RecyclerView
     */
    public static ViewAssertion itemViewMatches(int viewID,
                                                final Matcher<View> viewMatcher) {
        assertNotNull(viewMatcher);

        return (view, noViewException) -> {
            if (noViewException != null) {
                throw noViewException;
            }

            assertTrue(view instanceof RecyclerView);
            RecyclerView recyclerView = (RecyclerView) view;
            RecyclerView.Adapter adapter = recyclerView.getAdapter();

            for (int i = 0; i < adapter.getItemCount(); i++) {
                int itemType = adapter.getItemViewType(i);
                RecyclerView.ViewHolder viewHolder = adapter.createViewHolder(recyclerView, itemType);
                adapter.bindViewHolder(viewHolder, i);

                View targetView = viewHolder.itemView.findViewById(viewID);

                if (viewMatcher.matches(targetView)) {
                    return; // Found match
                }
            }

            fail("No match found");
        };
    }

    /**
     * Count items in RecyclerView Adapter and compare to expected count.
     *
     * @param expectedItemCount What the count is expected to be
     * @return Espresso ViewAssertion to check against a RecyclerView
     */
    public static ViewAssertion itemCountMatches(int expectedItemCount) {
        return (view, noViewException) -> {
            if (noViewException != null) {
                throw noViewException;
            }

            assertTrue(view instanceof RecyclerView);
            RecyclerView recyclerView = (RecyclerView) view;
            RecyclerView.Adapter adapter = recyclerView.getAdapter();

            if (adapter.getItemCount() == expectedItemCount) {
                return; // Item count matches
            }

            fail("Item count does not match");
        };
    }
}