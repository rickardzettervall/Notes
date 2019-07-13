package tech.zettervall.notes.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

/**
 * Tag which can be applied to Notes to allow user specified labeling.
 */
@Entity(tableName = "tags")
public class Tag {

    /**
     * DB column names.
     */
    public static final String idColumnName = "_id";
    public static final String tagColumnName = "tag";

    @ColumnInfo(name = idColumnName)
    @PrimaryKey(autoGenerate = true)
    private int _id;
    @ColumnInfo(name = tagColumnName)
    private String tag;

    @Ignore
    public Tag(String tag) {
        this.tag = tag;
    }

    /**
     * Constructor for Room.
     */
    public Tag(int _id, String tag) {
        this._id = _id;
        this.tag = tag;
    }

    public int getId() {
        return _id;
    }

    public void setId(int _id) {
        this._id = _id;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    @Override
    public String toString() {
        return "Tag{" +
                "_id=" + _id +
                ", tag='" + tag + '\'' +
                '}';
    }
}
