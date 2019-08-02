package tech.zettervall.notes.data;

import androidx.lifecycle.LiveData;
import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import tech.zettervall.notes.models.Tag;

/**
 * Data Access Object (DAO) for interacting with Tag table.
 * Allows fetching data as POJO, List, LiveData and PageList.
 */
@Dao
public interface TagDao {

    // Get all Tags as PagedList (sorted alphabetically)
    @Query("SELECT * FROM tags ORDER BY title ASC")
    DataSource.Factory<Integer, Tag> getTagsPagedList();

    // Get all Tags as LiveData (sorted alphabetically)
    @Query("SELECT * FROM tags ORDER BY title ASC")
    LiveData<List<Tag>> getTagsLiveData();

    // Get all Tags as List (sorted alphabetically)
    @Query("SELECT * FROM tags ORDER BY title ASC")
    List<Tag> getTagsList();

    // Get Tag as LiveData based on ID
    @Query("SELECT * FROM tags WHERE _id IS :tagID")
    LiveData<Tag> getTagLiveData(int tagID);

    // Get Tag based on ID
    @Query("SELECT * FROM tags WHERE _id IS :tagID")
    Tag getTag(int tagID);

    // Insert Tag and return ID
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertTag(Tag tag);

    // Insert multiple Tags and return ID's
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insertTags(Tag[] tags);

    // Update Tag
    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateTag(Tag tag);

    // Delete Tag
    @Delete
    void deleteTag(Tag tag);
}
