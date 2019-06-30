package tech.zettervall.notes.models;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

import tech.zettervall.notes.data.typeconverters.StringListTypeConverter;
import tech.zettervall.notes.utils.DateTimeHelper;

@Parcel
@Entity(tableName = "note")
public class Note implements Comparable<Note> {

    /**
     * DB column names.
     */
    public static final String idColumnName = "_id";
    public static final String titleColumnName = "title";
    public static final String textColumnName = "text";
    public static final String tagsColumnName = "tags";
    public static final String creationEpochColumnName = "creation_epoch";
    public static final String modifiedEpochColumnName = "modified_epoch";
    public static final String notificationEpochColumnName = "notification_epoch";
    public static final String trashColumnName = "trash";
    public static final String favoriteColumnName = "favorite";
    public static final String folderIdColumnName = "folder_id";
    public static final String colorIdColumnName = "color_id";

    @ColumnInfo(name = idColumnName)
    @PrimaryKey(autoGenerate = true)
    public int _id;
    @ColumnInfo(name = titleColumnName)
    public String title;
    @ColumnInfo(name = textColumnName)
    public String text;
    @ColumnInfo(name = tagsColumnName)
    @TypeConverters(StringListTypeConverter.class)
    public List<String> tags;
    @ColumnInfo(name = creationEpochColumnName)
    public long creationEpoch;
    @ColumnInfo(name = modifiedEpochColumnName)
    public long modifiedEpoch;
    @ColumnInfo(name = notificationEpochColumnName)
    public long notificationEpoch;
    @ColumnInfo(name = trashColumnName)
    public boolean isTrash;
    @ColumnInfo(name = favoriteColumnName)
    public boolean isFavorite;
    @ColumnInfo(name = folderIdColumnName)
    public int folderId;
    @ColumnInfo(name = colorIdColumnName)
    public int colorId;

    /**
     * Empty Constructor for Parceler.
     */
    @Ignore
    public Note() {
    }

    /**
     * Constructor for new Note Objects.
     */
    @Ignore
    public Note(String title, String text, @NonNull List<String> tags, long creationEpoch,
                long modifiedEpoch, long notificationEpoch, boolean isTrash, boolean isFavorite) {
        this.title = setFirstCharUpperCase(title);
        this.text = setFirstCharUpperCase(text);
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

    /**
     * Set first char in a String to uppercase.
     */
    private String setFirstCharUpperCase(String str) {
        return !str.isEmpty() ? str.substring(0, 1).toUpperCase() + str.substring(1) : str;
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
        this.title = setFirstCharUpperCase(title);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = setFirstCharUpperCase(text);
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

    public String getCreationString(Context context) {
        return DateTimeHelper.getDateStringFromEpoch(creationEpoch, context);
    }

    public void setCreationEpoch(long creationEpoch) {
        this.creationEpoch = creationEpoch;
    }

    public long getModifiedEpoch() {
        return modifiedEpoch;
    }

    public String getModifiedString(Context context) {
        return DateTimeHelper.getDateStringFromEpoch(modifiedEpoch, context);
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
        if (trash) {
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
     * Compare contents for diff check in Adapter, determines whether to update the item
     * in adapter. Since modifiedEpoch is changed on a updated Note, it's the only field
     * that is checked.
     *
     * @param note Note to compare to this
     * @return 0 when match and otherwise -1
     */
    @Override
    public int compareTo(@NonNull Note note) {
        boolean notificationEpochChanged = this.notificationEpoch != note.getNotificationEpoch();
        return this.modifiedEpoch == note.getModifiedEpoch() || notificationEpochChanged ? 0 : -1;
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
