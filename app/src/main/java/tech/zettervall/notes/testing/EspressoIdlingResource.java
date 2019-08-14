package tech.zettervall.notes.testing;

import androidx.test.espresso.idling.CountingIdlingResource;

public abstract class EspressoIdlingResource {

    private static final String RESOURCE = "GLOBAL";
    private static CountingIdlingResource mCountingIdlingResource
            = new CountingIdlingResource(RESOURCE);

    public static void pause() {
        mCountingIdlingResource.increment();
    }

    public static void resume() {
        mCountingIdlingResource.decrement();
    }

    public static CountingIdlingResource getCountingIdlingResource() {
        return mCountingIdlingResource;
    }
}
