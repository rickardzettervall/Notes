package tech.zettervall.notes.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StringUtilTest {

    @Test
    public void isFirstCharUpperCased_returnTrue() {
        assertEquals("Hello", StringUtil.setFirstCharUpperCase("hello"));
    }
}
