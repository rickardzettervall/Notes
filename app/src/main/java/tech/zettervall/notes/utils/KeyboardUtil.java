package tech.zettervall.notes.utils;

import android.app.Activity;
import android.content.Context;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

public abstract class KeyboardUtil {

    /**
     * Hide Keyboard.
     */
    public static void hideKeyboard(Activity activity) {
        activity.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
    }

    /**
     * Show Keyboard.
     */
    public static void showKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)
                activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(),
                InputMethodManager.SHOW_FORCED, 0);
    }
}
