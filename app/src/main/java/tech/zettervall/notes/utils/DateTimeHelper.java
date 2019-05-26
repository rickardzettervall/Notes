package tech.zettervall.notes.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.TimeZone;

public abstract class DateTimeHelper {

    /**
     * Gets the current date and time in the format of user system settings.
     * @return Date in a String format
     */
    public static String getCurrentDateTime() {
        return DateFormat.getDateInstance().format(new Date(System.currentTimeMillis()));
    }

    public static long getCurrentEpoch() {
        return new Date().getTime();
    }

    /**
     * Get Date String in local timezone from Epoch.
     */
    public static String getDateStringFromEpoch(long epoch) {
        Date date = new Date();
        date.setTime(epoch);
        DateFormat dateFormat = DateFormat.getDateTimeInstance();
        dateFormat.setTimeZone(TimeZone.getDefault());
        return dateFormat.format(date);
    }
}
