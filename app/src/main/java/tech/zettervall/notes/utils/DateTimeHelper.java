package tech.zettervall.notes.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class DateTimeHelper {

    /**
     * Gets the current date and time in the format of user system settings.
     * @return Date in a String format
     */
    public static String getCurrentDateTime() {
        return SimpleDateFormat.getDateTimeInstance().toString();
//        return DateFormat.getDateTimeInstance().format(new Date(System.currentTimeMillis()));
    }
}
