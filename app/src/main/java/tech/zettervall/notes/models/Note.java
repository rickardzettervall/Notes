package tech.zettervall.notes.models;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

import tech.zettervall.notes.utils.DateTimeUtil;
import tech.zettervall.notes.utils.StringUtil;

/**
 * Note which is stored in Room DB.
 */
@Parcel
@Entity(tableName = "notes")
public class Note {

    /**
     * DB column names.
     */
    public static final String idColumnName = "_id";
    public static final String titleColumnName = "title";
    public static final String textColumnName = "text";
    public static final String photoPathColumnName = "photo_path";
    public static final String tagsColumnName = "tags";
    public static final String creationEpochColumnName = "creation_epoch";
    public static final String modifiedEpochColumnName = "modified_epoch";
    public static final String notificationEpochColumnName = "notification_epoch";
    public static final String trashColumnName = "trash";
    public static final String favoriteColumnName = "favorite";

    @ColumnInfo(name = idColumnName)
    @PrimaryKey(autoGenerate = true)
    public int _id;
    @ColumnInfo(name = titleColumnName)
    public String title;
    @ColumnInfo(name = textColumnName)
    public String text;
    @ColumnInfo(name = photoPathColumnName)
    public String photoPath;
    @ColumnInfo(name = tagsColumnName)
    public List<Integer> tags;
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
    public Note(String title, String text, String photoPath, @NonNull List<Integer> tags,
                long creationEpoch, long modifiedEpoch, long notificationEpoch, boolean isTrash,
                boolean isFavorite) {
        this.title = StringUtil.setFirstCharUpperCase(title);
        this.text = StringUtil.setFirstCharUpperCase(text);
        this.photoPath = photoPath;
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
    public Note(int _id, String title, String text, String photoPath, @NonNull List<Integer> tags,
                long creationEpoch, long modifiedEpoch, long notificationEpoch, boolean isTrash,
                boolean isFavorite) {
        this._id = _id;
        this.title = StringUtil.setFirstCharUpperCase(title);
        this.text = StringUtil.setFirstCharUpperCase(text);
        this.photoPath = photoPath;
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
        this.title = StringUtil.setFirstCharUpperCase(title);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = StringUtil.setFirstCharUpperCase(text);
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public List<Integer> getTagIDs() {
        return new ArrayList<>(tags);
    }

    public void setTagIDs(List<Integer> tags) {
        this.tags = tags;
    }

    public long getCreationEpoch() {
        return creationEpoch;
    }

    public void setCreationEpoch(long creationEpoch) {
        this.creationEpoch = creationEpoch;
    }

    public String getCreationString(Context context) {
        return DateTimeUtil.getDateStringFromEpoch(creationEpoch, context);
    }

    public long getModifiedEpoch() {
        return modifiedEpoch;
    }

    public void setModifiedEpoch(long modifiedEpoch) {
        this.modifiedEpoch = modifiedEpoch;
    }

    public String getModifiedString(Context context) {
        return DateTimeUtil.getDateStringFromEpoch(modifiedEpoch, context);
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
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    /**
     * Compare contents for diff check in Adapter, determines whether to update the item
     * in adapter. Whenever modifiedEpoch changed it means that the Note text fields was
     * modified. We also need to check if favorite status or notificationEpoch changed
     * because the adapter displays an icon for those.
     *
     * @param obj Note to compare to this
     * @return true when contents are the same
     */
    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Note note = (Note) obj;
        return _id == note.getId() &&
                modifiedEpoch == note.getModifiedEpoch() &&
                isFavorite == note.isFavorite() &&
                notificationEpoch == note.getNotificationEpoch();
    }

    @Override
    public String toString() {
        return "Note{" +
                "_id=" + _id +
                ", title='" + title + '\'' +
                ", text='" + text + '\'' +
                ", photoPath='" + photoPath + '\'' +
                ", tags=" + tags +
                ", creationEpoch=" + creationEpoch +
                ", modifiedEpoch=" + modifiedEpoch +
                ", notificationEpoch=" + notificationEpoch +
                ", isTrash=" + isTrash +
                ", isFavorite=" + isFavorite +
                '}';
    }
}
