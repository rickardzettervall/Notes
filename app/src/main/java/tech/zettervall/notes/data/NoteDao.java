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

/**
 * Data Access Object (DAO) for interacting with Note table.
 * Allows fetching data as POJO, List, LiveData and PageList.
 */
@Dao
public interface NoteDao {

    // Get Notes as PagedList by RawQuery
    @RawQuery(observedEntities = Note.class)
    DataSource.Factory<Integer, Note> getNotesPagedList(SupportSQLiteQuery query);

    // Get Notes as LiveData by RawQuery
    @RawQuery(observedEntities = Note.class)
    LiveData<List<Note>> getNotesLiveData(SupportSQLiteQuery query);

    // Get Notes as PagedList by Tag ID
    @Query("SELECT * FROM notes WHERE tags LIKE :tagID")
    DataSource.Factory<Integer, Note> getNotesPagedList(String tagID);

    // Get Notes as LiveData by Tag ID
    @Query("SELECT * FROM notes WHERE tags LIKE :tagID")
    LiveData<List<Note>> getNotesLiveData(String tagID);

    // Get Notes as List by Tag ID
    @Query("SELECT * FROM notes WHERE tags LIKE :tagID")
    List<Note> getNotesList(String tagID);

    // Get Note as LiveData based on ID
    @Query("SELECT * FROM notes WHERE _id IS :noteID")
    LiveData<Note> getNoteLiveData(int noteID);

    // Get Note based on ID
    @Query("SELECT * FROM notes WHERE _id IS :noteID")
    Note getNote(int noteID);

    // Insert Note and return ID
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertNote(Note note);

    // Insert multiple Notes and return ID's
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insertNotes(Note[] notes);

    // Update Note
    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateNote(Note note);

    // Delete Note
    @Delete
    void deleteNote(Note note);

    // Delete all Notes
    @Query("DELETE FROM notes")
    void deleteAllNotes();

    // Delete all trashed Notes
    @Query("DELETE FROM notes WHERE trash = 1")
    void deleteAllTrashedNotes();
}
