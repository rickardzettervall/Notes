package tech.zettervall.notes;

public abstract class Constants {

    // Note
    public static final String NOTE = "note";
    public static final String NOTE_ID = "note_id";
    public static final String NOTE_TITLE = "note_title"; // Used for Notifications
    public static final String NOTE_TEXT = "note_text"; // Used for Notifications
    public static final String NOTE_FAVORITE = "note_favorite"; // New Note in Favorites List
    public static final String NOTE_CREATION_EPOCH = "note_creation_epoch"; // Used for tablet fragments

    // Tag
    public static final String TAG = "tag";
    public static final String TAG_ID = "tag_id";

    // Photo
    public static final String PHOTO_PATH = "photo_path";

    // Fragments
    public static final String FRAGMENT_ALL_NOTES = "fragment_all_notes";
    public static final String FRAGMENT_FAVORITES = "fragment_favorites";
    public static final String FRAGMENT_REMINDERS = "fragment_reminders";
    public static final String FRAGMENT_TRASH = "fragment_trash";
    public static final String FRAGMENT_TAGS = "fragment_tags";
    public static final String FRAGMENT_NOTE = "fragment_note";

    // Fragment (flag for swipe to trash fix)
    public static final String SET_TRASH_STATUS = "trash_status";

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

    // Google Play Store
    public static final String GOOGLE_PLAY_STORE = "http://play.google.com/store/search?q=pub:zettervall.tech&c=apps";
    public static final String GOOGLE_PLAY_STORE_NOTES = "http://play.google.com/store/apps/details?id=tech.zettervall.notes";
}
