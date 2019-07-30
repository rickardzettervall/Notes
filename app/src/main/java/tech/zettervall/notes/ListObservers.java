package tech.zettervall.notes;

import androidx.annotation.Nullable;

/**
 * Used for Observers in Lists.
 */
public interface ListObservers {

    void subscribeObservers();

    void refreshObservers(@Nullable String query);
}
