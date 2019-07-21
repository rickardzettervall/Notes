package tech.zettervall.notes.utils;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.SimpleDateFormat;
import java.util.Date;

import tech.zettervall.mNotes.R;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * Tests the DateTimeUtil class methods.
 */
@RunWith(AndroidJUnit4.class)
public class DateTimeUtilTest {

    private long dayInMilliseconds, todayEpoch, dayOldEpoch, weekOldEpoch, yearOldEpoch,
            yearOldEpochZeroed;
    private Context context;

    @Before
    public void setUp() {
        dayInMilliseconds = 86400000L;
        todayEpoch = new Date().getTime();
        dayOldEpoch = todayEpoch - (dayInMilliseconds);
        weekOldEpoch = todayEpoch - (dayInMilliseconds * 7);
        yearOldEpoch = 1514808030000L; // Jan 1, 2018 13:00:30
        yearOldEpochZeroed = 1514808000000L; // Jan 1, 2018 13:00:00
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
    }

    /**
     * Checks that method return current Epoch.
     */
    @Test
    public void isCurrentEpochNow_returnTrue() {
        assertEquals(new Date().getTime(), DateTimeUtil.getCurrentEpoch(), 3L);
    }

    /**
     * Checks that Epoch sent to method gets returned with zeroed seconds.
     */
    @Test
    public void isEpochZeroed_returnTrue() {
        assertEquals(yearOldEpochZeroed, DateTimeUtil.getEpochWithZeroSeconds(yearOldEpoch));
    }

    /**
     * Checks that Unix Epoch set at today contains "Today".
     */
    @Test
    public void isDateStringToday_returnTrue() {
        assertThat(DateTimeUtil.getDateStringFromEpoch(todayEpoch, context),
                containsString("Today"));
    }

    /**
     * Checks that Unix Epoch set at yesterday contains "Yesterday".
     */
    @Test
    public void isDateStringYesterDay_returnTrue() {
        assertThat(DateTimeUtil.getDateStringFromEpoch(dayOldEpoch, context),
                containsString(context.getString(R.string.yesterday)));
    }

    /**
     * Checks that Unix Epoch set at a week ago doesn't contain
     * "Today", "Yesterday" or current year. Will not work first
     * week of January.
     */
    @Test
    public void isDateStringWeekOld_returnFalse() {
        assertFalse((DateTimeUtil.getDateStringFromEpoch(weekOldEpoch, context)
                .contains("Yesterday") &&
                DateTimeUtil.getDateStringFromEpoch(weekOldEpoch, context)
                        .contains("Today") &&
                DateTimeUtil.getDateStringFromEpoch(weekOldEpoch, context)
                        .contains(new SimpleDateFormat("yyyy").format(new Date()))));
    }

    /**
     * Checks that Unix Epoch set at previous year return correct String,
     * time value should default to 24h.
     */
    @Test
    public void isDateStringYearOld_returnTrue() {
        assertThat(DateTimeUtil.getDateStringFromEpoch(yearOldEpoch, context),
                is("1 Jan, 2018 13:00"));
    }
}
