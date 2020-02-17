package tech.zettervall.notes.utils;

import android.content.Context;
import android.content.res.ColorStateList;

import tech.zettervall.mNotes.R;

public abstract class ColorStateListUtil {

    /**
     * Get ColorStateList for Navigation Drawer (Night Theme)
     */
    public static ColorStateList getNavigationDrawerNightColorStateList(Context context) {

        int[][] states = new int[][]{
                new int[]{android.R.attr.state_checked},
                new int[]{android.R.attr.state_enabled},
        };

        int[] colors = new int[]{
                context.getResources().getColor(R.color.primary_light),
                context.getResources().getColor(android.R.color.primary_text_dark),
        };

        return new ColorStateList(states, colors);
    }
}
