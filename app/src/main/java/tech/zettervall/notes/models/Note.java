package tech.zettervall.notes.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.ArrayList;
import java.util.List;

import tech.zettervall.notes.data.typeconverters.StringListTypeConverter;
import tech.zettervall.notes.utils.DateTimeHelper;

@Entity(tableName = "note")
public class Note implements Comparable<Note> {

    @PrimaryKey(autoGenerate = true)
    private int _id;
    private String title, text;
    @TypeConverters(StringListTypeConverter.class)
    private List<String> tags;
    @ColumnInfo(name = "creation_epoch")
    private long creationEpoch;
    @ColumnInfo(name = "modified_epoch")
    private long modifiedEpoch;
    @ColumnInfo(name = "notification_epoch")
    private long notificationEpoch;
    @ColumnInfo(name = "trash")
    private boolean isTrash;
    @ColumnInfo(name = "favorite")
    private boolean isFavorite;
    @ColumnInfo(name = "folder_id")
    private int folderId;
    @ColumnInfo(name = "color_id")
    private int colorId;

    /**
     * Constructor for new Note Objects.
     */
    @Ignore
    public Note(String title, String text, @NonNull List<String> tags, long creationEpoch,
                long modifiedEpoch, long notificationEpoch, boolean isTrash, boolean isFavorite) {
        this.title = title;
        this.text = text;
        this.tags = tags;
        this.creationEpoch = creationEpoch;
        this.modifiedEpoch = modifiedEpoch;
        this.notificationEpoch = notificationEpoch;
        this.isTrash = isTrash;
        this.isFavorite = isFavorite;
    }

    /**
     * Constructor for Room.
     */
    public Note(int _id, String title, String text, @NonNull List<String> tags, long creationEpoch,
                long modifiedEpoch, long notificationEpoch, boolean isTrash, boolean isFavorite) {
        this._id = _id;
        this.title = title;
        this.text = text;
        this.tags = tags;
        this.creationEpoch = creationEpoch;
        this.modifiedEpoch = modifiedEpoch;
        this.notificationEpoch = notificationEpoch;
        this.isTrash = isTrash;
        this.isFavorite = isFavorite;
    }

    public int getId() {
        return _id;
    }

    public void setId(int _id) {
        this._id = _id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<String> getTags() {
        return new ArrayList<>(tags);
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public long getCreationEpoch() {
        return creationEpoch;
    }

    public String getCreationString() {
        return DateTimeHelper.getDateStringFromEpoch(creationEpoch);
    }

    public void setCreationEpoch(long creationEpoch) {
        this.creationEpoch = creationEpoch;
    }

    public long getModifiedEpoch() {
        return modifiedEpoch;
    }

    public String getModifiedString() {
        return DateTimeHelper.getDateStringFromEpoch(modifiedEpoch);
    }

    public void setModifiedEpoch(long modifiedEpoch) {
        this.modifiedEpoch = modifiedEpoch;
    }

    public long getNotificationEpoch() {
        return notificationEpoch;
    }

    public void setNotificationEpoch(long notificationEpoch) {
        this.notificationEpoch = notificationEpoch;
    }

    public boolean isTrash() {
        return isTrash;
    }

    public void setTrash(boolean trash) {
        isTrash = trash;
        if(trash) {
            // Also set isFavorite to false because a trashed Note shouldn't be in favorites
            isFavorite = false;
        }
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public int getFolderId() {
        return folderId;
    }

    public void setFolderId(int folderId) {
        this.folderId = folderId;
    }

    public int getColorId() {
        return colorId;
    }

    public void setColorId(int colorId) {
        this.colorId = colorId;
    }

    /**
     * Compare contents for diff check in Adapter.
     *
     * @param note Note to compare to this
     * @return 0 when match and otherwise -1
     */
    @Override
    public int compareTo(@NonNull Note note) {
        boolean matchHeadline = this.title.equals(note.getTitle()),
                matchText = this.text.equals(note.getText()),
                matchFavorite = this.isFavorite == note.isFavorite();
        if (matchHeadline && matchText && matchFavorite) {
            return 0;
        }
        return -1;
    }

    @NonNull
    @Override
    public String toString() {
        return "Note{" +
                "_id=" + _id +
                ", title='" + title + '\'' +
                ", text='" + text + '\'' +
                ", tags=" + tags +
                ", creationEpoch=" + creationEpoch +
                ", modifiedEpoch=" + modifiedEpoch +
                ", notificationEpoch=" + notificationEpoch +
                ", isTrash=" + isTrash +
                ", isFavorite=" + isFavorite +
                '}';
    }
}
