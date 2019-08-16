package tech.zettervall.notes.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StringUtilTest {

    /**
     * Checks that String gets returned with first character upper cased.
     */
    @Test
    public void setFirstCharUpperCase() {
        assertEquals("Hello", StringUtil.setFirstCharUpperCase("hello"));
    }
}
