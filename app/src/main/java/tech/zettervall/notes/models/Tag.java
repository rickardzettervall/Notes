package tech.zettervall.notes.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.parceler.Parcel;

import tech.zettervall.notes.utils.StringUtil;

/**
 * Tag which can be applied to Notes to allow user specified labeling.
 */
@Parcel
@Entity(tableName = "tags")
public class Tag {

    /**
     * DB column names.
     */
    public static final String idColumnName = "_id";
    public static final String tagColumnName = "title";

    @ColumnInfo(name = idColumnName)
    @PrimaryKey(autoGenerate = true)
    public int _id;
    @ColumnInfo(name = tagColumnName)
    public String title;

    /**
     * Empty Constructor for Parcel.
     */
    @Ignore
    public Tag() {
    }

    /**
     * Constructor new Tag Objects.
     */
    @Ignore
    public Tag(String title) {
        this.title = StringUtil.setFirstCharUpperCase(title);
    }

    /**
     * Constructor for Room.
     */
    public Tag(int _id, String title) {
        this._id = _id;
        this.title = StringUtil.setFirstCharUpperCase(title);
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

    @Override
    public String toString() {
        return "Tag{" +
                "_id=" + _id +
                ", title='" + title + '\'' +
                '}';
    }
}
