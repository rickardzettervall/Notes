package tech.zettervall.notes.utils;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import tech.zettervall.mNotes.R;

public abstract class DateTimeUtil {

    /**
     * Get current Unix Epoch timestamp in long.
     */
    public static long getCurrentEpoch() {
        return new Date().getTime();
    }

    /**
     * Get Unix Epoch timestamp in long but with the seconds reset to 00.
     * For example 01/01/2000 20:33:21 will be converted to 01/01/2000 20:33:00.
     * This is used for setting notifications.
     */
    public static long getEpochWithZeroSeconds(long epoch) {
        Date date = new Date();
        date.setTime(epoch);
        String dateString = date.toString();
        int seconds = Integer.valueOf(dateString.substring(17, 19));
        return epoch - (seconds * 1000);
    }

    /**
     * Get Date String in local timezone from Epoch.
     *
     * @param epoch   Unix Epoch to use.
     * @param context Used for fetching SharedPreferences and translatable Strings.
     * @return Date String formatted in various ways depending on how old the
     * Epoch is, e.g. an Epoch from today will return "Today, HH:MM".
     */
    @NonNull
    public static String getDateStringFromEpoch(long epoch, Context context) {

        // Set Date Objects
        Date currentDate = new Date();
        currentDate.setTime(getCurrentEpoch());
        Date inputDate = new Date();
        inputDate.setTime(epoch);

        // Convert current/input date to integers for comparison
        SimpleDateFormat dayCheck = new SimpleDateFormat("d", Locale.US),
                yearCheck = new SimpleDateFormat("yyyy", Locale.US);
        int currentDayVal = Integer.valueOf(dayCheck.format(currentDate)),
                inputDayVal = Integer.valueOf(dayCheck.format(inputDate)),
                currentYearVal = Integer.valueOf(yearCheck.format(currentDate)),
                inputYearVal = Integer.valueOf(yearCheck.format(inputDate));

        // Default formats
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault()),
                dateFormat = new SimpleDateFormat("d MMM", Locale.getDefault()),
                yearFormat = new SimpleDateFormat("YYYY", Locale.getDefault());

        // Set AM/PM for countries which use that standard
        if (!use24h(context)) {
            timeFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
            dateFormat = new SimpleDateFormat("MMM d", Locale.getDefault());
        }

        // Return Strings
        if (currentDayVal - inputDayVal == 0) { // TODAY
            return context.getString(R.string.today) + ", " + timeFormat.format(inputDate);
        } else if (currentDayVal - inputDayVal == 1) { // YESTERDAY
            return context.getString(R.string.yesterday) + ", " + timeFormat.format(inputDate);
        } else if (currentYearVal == inputYearVal) { // THIS YEAR
            return dateFormat.format(inputDate) + ", " + timeFormat.format(inputDate);
        } else { // OLDER YEAR
            return dateFormat.format(inputDate) + ", " +
                    yearFormat.format(inputDate) +
                    " " + timeFormat.format(inputDate);
        }
    }

    /**
     * Checks whether to use 12 or 24H for time. Uses 24H as default but changes to 12H if
     * user system language uses that, but also overrides that if user chose to in
     * the app settings activity.
     *
     * @param context Use context to retrieve SharedPreferences
     * @return true for 24h and false for 12h
     */
    public static boolean use24h(Context context) {

        // Get user preferences
        boolean overrideLocale = PreferenceManager.getDefaultSharedPreferences(context).
                getBoolean(context.getString(R.string.time_24_key), false);

        // Return early because of user override
        if (overrideLocale) {
            return true;
        }

        // Countries with AM/PM clock
        Locale[] amPmCountries = {Locale.US, Locale.CANADA, Locale.CANADA_FRENCH};
        for (Locale i : amPmCountries) { // Check if system preferences are a AM/PM country
            if (Locale.getDefault().getDisplayCountry().equals(i.getDisplayCountry())) {
                return false;
            }
        }

        return true;
    }
}
