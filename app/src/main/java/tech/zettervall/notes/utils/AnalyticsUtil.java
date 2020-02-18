package tech.zettervall.notes.utils;

import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

public abstract class AnalyticsUtil {

    private static final String EVENT_NEW_NOTE_ID = "new_note";
    private static final String EVENT_NEW_NOTE_NAME = "New Note";
    private static final String EVENT_SHARE_ID = "share";
    private static final String EVENT_SHARE_NAME = "Share";
    private static final String EVENT_REMINDER_ID = "schedule_reminder";
    private static final String EVENT_REMINDER_NAME = "Schedule Reminder";
    private static final String EVENT_PHOTO_ID = "take_photo";
    private static final String EVENT_PHOTO_NAME = "Take Photo";

    /**
     * Log Event for creating a new Note.
     */
    public static void logEventNewNote(FirebaseAnalytics firebaseAnalytics) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, EVENT_NEW_NOTE_ID);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, EVENT_NEW_NOTE_NAME);
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    /**
     * Log Event for clicking share action.
     */
    public static void logEventShare(FirebaseAnalytics firebaseAnalytics) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, EVENT_SHARE_ID);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, EVENT_SHARE_NAME);
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    /**
     * Log Event for successfully scheduling a new reminder (notification).
     */
    public static void logEventScheduleReminder(FirebaseAnalytics firebaseAnalytics) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, EVENT_REMINDER_ID);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, EVENT_REMINDER_NAME);
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    /**
     * Log Event for taking a new photo and saving it to Note.
     */
    public static void logEventTakePhoto(FirebaseAnalytics firebaseAnalytics) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, EVENT_PHOTO_ID);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, EVENT_PHOTO_NAME);
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }


}
