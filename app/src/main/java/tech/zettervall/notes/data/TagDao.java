package tech.zettervall.notes.data;

import androidx.lifecycle.LiveData;
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
 */
@Dao
public interface TagDao {

    // Get all Tags (sorted alphabetically)
    @Query("SELECT * FROM tags ORDER BY tag DESC")
    LiveData<List<Tag>> getTags();

    // Get specific Tag based on ID
    @Query("SELECT * FROM tags WHERE _id IS :id")
    LiveData<Tag> getTag(int id);

    // Insert Tag and return the ID
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertTag(Tag tag);

    // Update Tag
    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateTag(Tag tag);

    // Delete Tag
    @Delete
    void deleteTag(Tag tag);
}
