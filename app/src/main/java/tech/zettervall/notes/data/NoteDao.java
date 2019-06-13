package tech.zettervall.notes.data;

import androidx.lifecycle.LiveData;
import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.RawQuery;
import androidx.room.Update;
import androidx.sqlite.db.SupportSQLiteQuery;

import tech.zettervall.notes.models.Note;

/**
 * Data Access Object (DAO) for interacting with Note Db.
 */
@Dao
public interface NoteDao {

    // Get Notes
    @RawQuery(observedEntities = Note.class)
    DataSource.Factory<Integer, Note> getNotes(SupportSQLiteQuery query);

    // Get specific Note based on ID
    @Query("SELECT * FROM note WHERE _id IS :id")
    LiveData<Note> getNote(int id);

    // Insert Note and return the ID
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertNote(Note note);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertNotes(Note[] notes);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateNote(Note note);

    @Delete
    void deleteNote(Note note);

    // Delete ALL Notes
    @Query("DELETE FROM note")
    void deleteAllNotes();

    // Delete ALL trashed Notes
    @Query("DELETE FROM note WHERE trash = 1")
    void deleteAllTrashedNotes();
}
