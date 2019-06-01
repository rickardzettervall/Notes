package tech.zettervall.notes.models;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "tag")
public class Tag {

    @PrimaryKey(autoGenerate = true)
    private int _id;
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
}
