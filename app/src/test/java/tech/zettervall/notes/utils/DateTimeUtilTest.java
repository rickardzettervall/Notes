package tech.zettervall.notes.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.text.SimpleDateFormat;
import java.util.Date;

import tech.zettervall.mNotes.R;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

/**
 * Tests the DateTimeUtil class methods.
 */
@RunWith(RobolectricTestRunner.class)
public class DateTimeUtilTest {

    private long mDayInMilliseconds, mTodayEpoch, mDayOldEpoch, mWeekOldEpoch, mYearOldEpoch,
            mYearOldEpochZeroed;
    private Context mContext;
    private SharedPreferences mSharedPreferences;

    @Before
    public void init() {
        mDayInMilliseconds = 86400000L;
        mTodayEpoch = new Date().getTime();
        mDayOldEpoch = mTodayEpoch - (mDayInMilliseconds);
        mWeekOldEpoch = mTodayEpoch - (mDayInMilliseconds * 7);
        mYearOldEpoch = 1514808030000L; // Jan 1, 2018 13:00:30
        mYearOldEpochZeroed = 1514808000000L; // Jan 1, 2018 13:00:00
        mContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
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
        assertEquals(mYearOldEpochZeroed, DateTimeUtil.getEpochWithZeroSeconds(mYearOldEpoch));
    }

    /**
     * Checks that Unix Epoch set at today contains "Today".
     */
    @Test
    public void isDateStringToday_returnTrue() {
        assertThat(DateTimeUtil.getDateStringFromEpoch(mTodayEpoch, mContext),
                containsString("Today"));
    }

    /**
     * Checks that Unix Epoch set at yesterday contains "Yesterday".
     */
    @Test
    public void isDateStringYesterDay_returnTrue() {
        assertThat(DateTimeUtil.getDateStringFromEpoch(mDayOldEpoch, mContext),
                containsString(mContext.getString(R.string.yesterday)));
    }

    /**
     * Checks that Unix Epoch set at a week ago doesn't contain
     * "Today", "Yesterday" or current year. Will not work first
     * week of January.
     */
    @Test
    public void isDateStringWeekOld_returnFalse() {
        assertFalse((DateTimeUtil.getDateStringFromEpoch(mWeekOldEpoch, mContext)
                .contains("Yesterday") &&
                DateTimeUtil.getDateStringFromEpoch(mWeekOldEpoch, mContext)
                        .contains("Today") &&
                DateTimeUtil.getDateStringFromEpoch(mWeekOldEpoch, mContext)
                        .contains(new SimpleDateFormat("yyyy").format(new Date()))));
    }

    /**
     * Checks that Unix Epoch set at previous year return correct String.
     * Date format should be MMM DD, YYYY.
     * Time format should be AM/PM, e.g. 1:00 PM.
     */
    @Test
    public void isDateStringYearOld_returnTrue() {
        // Change preferences to disable locale override
        mSharedPreferences.edit().putBoolean(mContext.getString(R.string.time_24_key), false).commit();
        assertThat(DateTimeUtil.getDateStringFromEpoch(mYearOldEpoch, mContext),
                is("Jan 1, 2018 1:00 PM"));
    }

    /**
     * Checks that Unix Epoch set at previous year return correct String,
     * Date format should be DD MMM, YYYY.
     * Time format should be military (24H), e.g. 13:00.
     */
    @Test
    public void isDateStringYearOldMilitary_returnTrue() {
        // Change preferences to enable local override
        mSharedPreferences.edit().putBoolean(mContext.getString(R.string.time_24_key), true).commit();
        assertThat(DateTimeUtil.getDateStringFromEpoch(mYearOldEpoch, mContext),
                is("1 Jan, 2018 13:00"));
    }
}
