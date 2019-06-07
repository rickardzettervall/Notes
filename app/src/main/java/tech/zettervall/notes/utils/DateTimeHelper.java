package tech.zettervall.notes.utils;

import android.content.Context;

import androidx.preference.PreferenceManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import tech.zettervall.mNotes.R;
import tech.zettervall.notes.Constants;

public abstract class DateTimeHelper {

    /**
     * Get current Unix Epoch timestamp in long.
     */
    public static long getCurrentEpoch() {
        return new Date().getTime();
    }

    /**
     * Get Date String in local timezone from Epoch.
     *
     * @param epoch   Unix Epoch to use.
     * @param context Used for fetching SharedPreferences and translatable Strings.
     * @return Date String formatted in various ways depending on how old the
     * Epoch is, e.g. an Epoch from today will return "Today, HH:MM".
     */
    public static String getDateStringFromEpoch(long epoch, Context context) {

        // Get user preferences
        int timeSelector = PreferenceManager.getDefaultSharedPreferences(context).
                getInt(Constants.TIME_SELECTOR, 0);

        // Countries with AM/PM clock
        Locale[] amPmCountries = {Locale.US, Locale.CANADA, Locale.CANADA_FRENCH};

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
        if (timeSelector != Constants.TIME_24) {
            for (Locale i : amPmCountries) {
                if (timeSelector == Constants.TIME_12 ||
                        Locale.getDefault().getDisplayCountry().equals(i.getDisplayCountry())) {
                    timeFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
                    dateFormat = new SimpleDateFormat("MMM d", Locale.getDefault());
                    break;
                }
            }
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
}
