package tech.zettervall.notes.repositories;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.paging.DataSource;

import tech.zettervall.notes.AppExecutor;
import tech.zettervall.notes.Constants;
import tech.zettervall.notes.data.NoteDao;
import tech.zettervall.notes.data.NoteDb;
import tech.zettervall.notes.models.Note;

public class NoteRepository {

    private static final String TAG = NoteRepository.class.getSimpleName();
    private static NoteRepository INSTANCE;
    private NoteDao mNoteDao;
    private long mNoteID;

    private NoteRepository(Application application) {
        NoteDb db = NoteDb.getInstance(application.getApplicationContext());
        mNoteDao = db.noteDao();
    }

    public static NoteRepository getInstance(final Application application) {
        if (INSTANCE == null) {
            synchronized (NoteRepository.class) {
                if (INSTANCE == null) {
                    Log.d(TAG, "Creating new NoteRepository instance..");
                    INSTANCE = new NoteRepository(application);
                }
            }
        }
        return INSTANCE;
    }

    /**
     * Get all Note Objects from the db as DataSource
     * Object, so that it can be used in PagedList.
     *
     * @return DataSource containing all Notes
     */
    public DataSource.Factory<Integer, Note> getNotes() {
        Log.d(TAG, "Retrieving all Notes..");
        return mNoteDao.getNotes();
    }

    /**
     * Get single Note based on unique _id.
     *
     * @param _id Db _id
     * @return LiveData Object containing a Note
     */
    public LiveData<Note> getNote(int _id) {
        Log.d(TAG, "Retrieving Note[id: " + _id + "]..");
        return mNoteDao.getNote(_id);
    }

    /**
     * Insert a single Note into db.
     *
     * @param note Note Object to be inserted
     */
    public long insertNote(final Note note) {
        AppExecutor.getExecutor().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Inserting new Note into db..");
                mNoteID = mNoteDao.insertNote(note);
            }
        });
        return mNoteID;
    }

    /**
     * Insert Dummy data into the db for testing.
     */
    public void insertDummyData() {
        final Note[] notes = new Note[10];
        for (int i = 0; i < 10; i++) {
            notes[i] = new Note(
                    Constants.TYPE_PLAIN, "Dummy Headline!", "Dummy Text!");
        }
        AppExecutor.getExecutor().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Inserting dummy data into db..");
                mNoteDao.insertNotes(notes);
            }
        });
    }

    /**
     * Update an existing Note.
     *
     * @param note Note Object to update
     */
    public void updateNote(final Note note) {
        AppExecutor.getExecutor().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Updating Note[id: " + note.get_id() + "] in db..");
                mNoteDao.updateNote(note);
            }
        });
    }

    /**
     * Delete an existing Note.
     *
     * @param note Note Object to delete
     */
    public void deleteNote(final Note note) {
        AppExecutor.getExecutor().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Deleting Note[id: " + note.get_id() + "]  from db..");
                mNoteDao.deleteNote(note);
            }
        });
    }

    /**
     * Delete all Notes.
     */
    public void deleteAllNotes() {
        AppExecutor.getExecutor().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Deleting all Notes from db..!");
                mNoteDao.deleteAllNotes();
            }
        });
    }
}
