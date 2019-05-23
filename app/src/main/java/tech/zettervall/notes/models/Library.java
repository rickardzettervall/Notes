package tech.zettervall.notes.models;

/**
 * Open source library Object for displaying used libraries
 * in the About Activity in order to give credit to them.
 */
public class Library {

    private String title, description, url;
    private int iconId;

    public Library(String title, String description, String url, int iconId) {
        this.title = title;
        this.description = description;
        this.url = url;
        this.iconId = iconId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }

    public int getIconId() {
        return iconId;
    }

    @Override
    public String toString() {
        return "Library{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", url='" + url + '\'' +
                ", iconId=" + iconId +
                '}';
    }
}
