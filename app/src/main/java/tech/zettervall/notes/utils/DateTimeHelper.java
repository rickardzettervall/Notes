package tech.zettervall.notes.utils;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public abstract class DateTimeHelper {

    private static final String TAG = DateTimeHelper.class.getSimpleName();

    /**
     * Get current Unix Epoch timestamp in long.
     */
    public static long getCurrentEpoch() {
        return new Date().getTime();
    }

    /**
     * Get Date String in local timezone from Epoch.
     */
    // TODO: allow for user to change between am/pm and 24h
    public static String getDateStringFromEpoch(long epoch) {

        // Countries with AM/PM clock
        Locale[] amPmCountries = { Locale.US, Locale.CANADA, Locale.CANADA_FRENCH };

        // Set Date Objects
        Date currentDate = new Date();
        currentDate.setTime(getCurrentEpoch());
        Date inputDate = new Date();
        inputDate.setTime(epoch);

        // Convert current/input date to integers for comparison
        SimpleDateFormat dayCheck = new SimpleDateFormat("dd", Locale.US),
                yearCheck = new SimpleDateFormat("yyyy", Locale.US);
        int currentDayVal = Integer.valueOf(dayCheck.format(currentDate)),
                inputDayVal = Integer.valueOf(dayCheck.format(inputDate)),
                currentYearVal = Integer.valueOf(yearCheck.format(currentDate)),
                inputYearVal = Integer.valueOf(yearCheck.format(inputDate));

        // Default HH/MM
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault()),
                dateFormat = new SimpleDateFormat("d MMM", Locale.getDefault());

        // Set AM/PM for countries which use that standard
        for(int i = 0; i < amPmCountries.length; i++) {
            if(Locale.getDefault().getDisplayCountry().equals(amPmCountries[i].getDisplayCountry())) {
                timeFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
                break;
            }
        }

        if(currentDayVal - inputDayVal == 0) { // TODAY
            return "Today, " + timeFormat.format(inputDate);
        } else if(currentDayVal - inputDayVal == 1) { // YESTERDAY
            return "Yesterday, " + timeFormat.format(inputDate);
        } else if(currentYearVal == inputYearVal) { // THIS YEAR
            return dateFormat.format(inputDate) + ", " + timeFormat.format(inputDate);
        } else { // OTHER YEAR
            dateFormat = new SimpleDateFormat("d MMM YYYY", Locale.getDefault());
            return dateFormat.format(inputDate) + ", " + timeFormat.format(inputDate);
        }
    }
}
