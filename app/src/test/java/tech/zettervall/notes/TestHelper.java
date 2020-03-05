package tech.zettervall.notes;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

public abstract class TestHelper {

    public static Context getContext() {
        return InstrumentationRegistry.getInstrumentation().getTargetContext();
    }
}
