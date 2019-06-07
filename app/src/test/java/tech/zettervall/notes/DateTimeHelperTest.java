package tech.zettervall.notes;

import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import tech.zettervall.notes.utils.DateTimeHelper;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * Tests the DateTimeHelper class methods.
 */
public class DateTimeHelperTest {

    private long dayInMilliseconds;

    @Before
    public void setUp() {
        dayInMilliseconds = 86400000L;
    }

    @Test
    public void getCurrentEpoch() {
        assertEquals(new Date().getTime(), DateTimeHelper.getCurrentEpoch());
    }

    @Test
    public void getDateStringFromEpoch() {
        long todayEpoch = new Date().getTime(),
                dayOldEpoch = todayEpoch - (dayInMilliseconds),
                weekOldEpoch = todayEpoch - (dayInMilliseconds * 7);

        // Jan 1, 2018 1:00:30 PM
        long oldEpoch = 1514808030000L;

        // Today (time now)
        assertThat(DateTimeHelper.getDateStringFromEpoch(todayEpoch),
                containsString("Today"));

        // Yesterday
        assertThat(DateTimeHelper.getDateStringFromEpoch(dayOldEpoch),
                containsString("Yesterday"));

        // Week old
        assertFalse((DateTimeHelper.getDateStringFromEpoch(weekOldEpoch).contains("Yesterday") &&
                DateTimeHelper.getDateStringFromEpoch(weekOldEpoch).contains("Today") &&
                DateTimeHelper.getDateStringFromEpoch(weekOldEpoch).contains("2019")));

        // Year old
        assertThat(DateTimeHelper.getDateStringFromEpoch(oldEpoch),
                is("Jan 1 2018, 1:00 PM"));
    }
}
