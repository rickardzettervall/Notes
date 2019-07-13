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

import java.util.List;

import tech.zettervall.notes.models.Note;
import tech.zettervall.notes.models.Tag;

/**
 * Data Access Object (DAO) for interacting with Note table.
 */
@Dao
public interface NoteDao {

    // Get Notes
    @RawQuery(observedEntities = Note.class)
    DataSource.Factory<Integer, Note> getNotes(SupportSQLiteQuery query);

    // Get Notes by Tag
    @Query("SELECT * FROM notes WHERE tags LIKE :tag")
    DataSource.Factory<Integer, Note> getNotesByTag(Tag tag);

    // Get Notes by Tag (Raw)
    @Query("SELECT * FROM notes WHERE tags LIKE :tag")
    List<Note> getNotesByTagRaw(Tag tag);

    // Get specific Note based on ID
    @Query("SELECT * FROM notes WHERE _id IS :id")
    LiveData<Note> getNote(int id);

    // Get specific Note based on ID (Raw)
    @Query("SELECT * FROM notes WHERE _id IS :id")
    Note getNoteRaw(int id);

    // Insert Note and return the ID
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertNote(Note note);

    // Update Note
    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateNote(Note note);

    // Delete Note
    @Delete
    void deleteNote(Note note);

    // Delete ALL Notes
    @Query("DELETE FROM notes")
    void deleteAllNotes();

    // Delete ALL trashed Notes
    @Query("DELETE FROM notes WHERE trash = 1")
    void deleteAllTrashedNotes();
}
