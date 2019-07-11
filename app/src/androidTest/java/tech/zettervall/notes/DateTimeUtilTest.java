package tech.zettervall.notes;

import android.content.Context;

import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

import tech.zettervall.notes.utils.DateTimeUtil;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * Tests the DateTimeUtil class methods.
 */
@RunWith(AndroidJUnit4.class)
public class DateTimeUtilTest {

    private long dayInMilliseconds;
    private Context context;

    @Before
    public void setUp() {
        dayInMilliseconds = 86400000L;
        context = InstrumentationRegistry.getTargetContext();
    }

    @Test
    public void getCurrentEpoch() {
        assertEquals(new Date().getTime(), DateTimeUtil.getCurrentEpoch());
    }

    @Test
    public void getDateStringFromEpoch() {
        long todayEpoch = new Date().getTime(),
                dayOldEpoch = todayEpoch - (dayInMilliseconds),
                weekOldEpoch = todayEpoch - (dayInMilliseconds * 7);

        // Jan 1, 2018 1:00:30 PM
        long oldEpoch = 1514808030000L;

        // Today (time now)
        assertThat(DateTimeUtil.getDateStringFromEpoch(todayEpoch, context),
                containsString("Today"));

        // Yesterday
        assertThat(DateTimeUtil.getDateStringFromEpoch(dayOldEpoch, context),
                containsString("Yesterday"));

        // Week old
        assertFalse((DateTimeUtil.getDateStringFromEpoch(weekOldEpoch, context)
                        .contains("Yesterday") &&
                DateTimeUtil.getDateStringFromEpoch(weekOldEpoch, context)
                        .contains("Today") &&
                DateTimeUtil.getDateStringFromEpoch(weekOldEpoch, context)
                        .contains("2019")));

        // Year old
        assertThat(DateTimeUtil.getDateStringFromEpoch(oldEpoch, context),
                is("Jan 1 2018, 1:00 PM"));
    }
}
