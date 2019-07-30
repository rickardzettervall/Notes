package tech.zettervall.notes.utils;

import android.app.Activity;
import android.view.WindowManager;

public abstract class KeyboardUtil {

    /**
     * Hides keyboard from activity.
     */
    public static void hideKeyboard(Activity activity) {
        activity.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
    }
}
