package tech.zettervall.notes;

import android.graphics.Color;

public abstract class Constants {

    // Note
    public static final String NOTE = "note";
    public static final String NOTE_ID = "note_id";
    public static final String NOTE_TITLE = "note_title"; // Used for Notifications
    public static final String NOTE_TEXT = "note_text"; // Used for Notifications
    public static final String NOTE_FAVORITE = "note_favorite"; // New Note in Favorites List

    // Fragment
    public static final String FRAGMENT_NOTELIST = "fragment_notelist";
    public static final String FRAGMENT_FAVORITES = "fragment_favorites";
    public static final String FRAGMENT_REMINDERS = "fragment_reminders";
    public static final String FRAGMENT_TRASH = "fragment_trash";
    public static final String FRAGMENT_NOTE = "fragment_note";

    // Time
    public static final String TIME_SELECTOR = "time_selector";
    public static final int TIME_12 = 12;
    public static final int TIME_24 = 24;

    // List
    public static final int NOTE_LIST_PAGE_SIZE = 10;
    public static final int TAG_LIST_PAGE_SIZE = 10;

    // Search
    public static final String SEARCH_QUERY = "search_query";
    public static final String SEARCH_ICONIFIED = "search_iconified";

    // SharedPreferences
    public static final String SORT_TYPE_KEY = "sort_type_key";
    public static final int SORT_TYPE_ALPHABETICALLY = 0;
    public static final int SORT_TYPE_CREATION_DATE = 1;
    public static final int SORT_TYPE_MODIFIED_DATE = 2;
    public static final int SORT_TYPE_DEFAULT = SORT_TYPE_MODIFIED_DATE;
    public static final String SORT_DIRECTION_KEY = "sort_direction_key";
    public static final int SORT_DIRECTION_ASC = 0;
    public static final int SORT_DIRECTION_DESC = 1;
    public static final int SORT_DIRECTION_DEFAULT = SORT_DIRECTION_DESC;
    public static final String SORT_FAVORITES_ON_TOP_KEY = "sort_favorites_on_top";
    public static final boolean SORT_FAVORITES_ON_TOP_DEFAULT = false;
    public static final String NOTIFICATIONS_ENABLE_LIGHTS_KEY = "notifications_enable_lights";
    public static final boolean NOTIFICATIONS_ENABLE_LIGHTS_DEFAULT = false;
    public static final String NOTIFICATIONS_LIGHT_COLOR_KEY = "notifications_light_color";
    public static final int NOTIFICATION_COLOR_RED = Color.RED;
    public static final int NOTIFICATION_COLOR_GREEN = Color.GREEN;
    public static final int NOTIFICATION_COLOR_BLUE = Color.BLUE;
    public static final int NOTIFICATION_COLOR_DEFAULT = NOTIFICATION_COLOR_GREEN;
    public static final String NOTIFICATIONS_ENABLE_VIBRATION_KEY = "notification_enable_vibration";
    public static final boolean NOTIFICATIONS_ENABLE_VIBRATION_DEFAULT = false;
}
