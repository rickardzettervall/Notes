package tech.zettervall.notes.utils;

import android.app.Activity;
import android.view.WindowManager;

public abstract class Keyboard {

    public static void hideKeyboard(Activity activity) {
        activity.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
    }
}
