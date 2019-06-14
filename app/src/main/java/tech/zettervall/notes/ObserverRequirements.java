package tech.zettervall.notes;

/**
 * Used for Fragments with List of Notes.
 */
public interface ObserverRequirements {

    void subscribeObservers();
    void refreshObservers(String query);
}
