package tech.zettervall.notes.utils;

import androidx.annotation.NonNull;

public abstract class StringUtil {

    /**
     * Set first char in a String to uppercase.
     */
    public static String setFirstCharUpperCase(@NonNull String str) {
        return !str.isEmpty() ? str.substring(0, 1).toUpperCase() + str.substring(1) : str;
    }
}
