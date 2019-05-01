package tech.zettervall.notes.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import tech.zettervall.notes.models.Note;

/**
 * Data Access Object (DAO) for interacting with Note Db.
 */
@Dao
public interface NoteDao {

    // Get all Notes
    @Query("SELECT * FROM note ORDER BY date DESC")
    LiveData<List<Note>> getNotes();

    // Get specific Note based on ID
    @Query("SELECT * FROM note WHERE _id IS :id")
    LiveData<Note> getNote(int id);

    // Insert Note and return the ID
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertNote(Note note);

    // Update Note and return the ID
    @Update(onConflict = OnConflictStrategy.REPLACE)
    int updateNote(Note note);

    @Delete
    void deleteNote(Note note);
}
