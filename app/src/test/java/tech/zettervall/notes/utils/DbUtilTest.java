package tech.zettervall.notes.utils;

import org.junit.Test;

import java.util.concurrent.Callable;

import static org.junit.Assert.assertEquals;

public class DbUtilTest {

    /**
     * Checks that DbUtil rawDB can extract the return from callable.
     */
    @Test
    public void rawDB() {
        final long call = 2L;
        long extracted = DbUtil.rawDB(() -> call);
        assertEquals(call, extracted);
    }
}
