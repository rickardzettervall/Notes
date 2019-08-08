package tech.zettervall.notes.repositories;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.paging.DataSource;
import androidx.sqlite.db.SimpleSQLiteQuery;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import tech.zettervall.notes.AppExecutor;
import tech.zettervall.notes.Constants;
import tech.zettervall.notes.data.AppDb;
import tech.zettervall.notes.data.NoteDao;
import tech.zettervall.notes.models.Note;
import tech.zettervall.notes.utils.DbUtil;

public class NoteRepository {

    private static final String TAG = NoteRepository.class.getSimpleName();
    private static NoteRepository INSTANCE;
    private NoteDao mNoteDao;
    private SharedPreferences mSharedPreferences;

    private NoteRepository(Application application) {
        AppDb db = AppDb.getInstance(application.getApplicationContext());
        mNoteDao = db.noteDao();
        mSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(application.getApplicationContext());
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
     * Get SortType from SharedPreferences (Used in sorting).
     */
    private int getSortType() {
        return mSharedPreferences.getInt(Constants.SORT_TYPE_KEY, Constants.SORT_TYPE_DEFAULT);
    }

    /**
     * Get SortDirection from SharedPreferences (Used in sorting).
     */
    private int getSortDirection() {
        return mSharedPreferences.getInt(Constants.SORT_DIRECTION_KEY,
                Constants.SORT_DIRECTION_DEFAULT);
    }

    /**
     * Get FavoritesOnTop from SharedPreferences (Used in sorting).
     */
    private boolean getSortFavoritesOnTop() {
        return mSharedPreferences.getBoolean(Constants.SORT_FAVORITES_ON_TOP_KEY,
                Constants.SORT_FAVORITES_ON_TOP_DEFAULT);
    }

    /**
     * Builds SQL String query based on variables.
     *
     * @param sortType       What to sort by, e.g. alphabetically
     * @param sortDirection  Sorting direction, ASC / DESC
     * @param trash          Selects only Notes that have been trashed
     * @param onlyFavorites  Selects only favorites
     * @param favoritesOnTop Sort with favorites on top
     * @param searchQuery    Search String input from user, use null for no query
     * @param tagID          Only get Notes which has this Tag, use null for no Tag
     * @return Complete SQL query String ready to be used with RawQuery in DAO
     */
    private String queryBuilder(int sortType, int sortDirection, boolean trash,
                                boolean onlyFavorites, boolean onlyReminders,
                                boolean favoritesOnTop, @Nullable String searchQuery,
                                @Nullable Integer tagID) {
        // SQL query
        StringBuilder query = new StringBuilder("SELECT * FROM notes WHERE ");

        // Convert booleans to 1/0 for SQL query
        int trashVal = trash ? 1 : 0;
        int favoritesVal = onlyFavorites ? 1 : 0;

        // Query Strings
        final String favoritesQuery = " AND " + Note.favoriteColumnName + " = " + favoritesVal;
        final String remindersQuery = " AND " + Note.notificationEpochColumnName + " > 0";
        final String sortDirectionQuery = sortDirection == Constants.SORT_DIRECTION_ASC ?
                "ASC" : "DESC";
        final String tagIdQuery = " AND " + Note.tagsColumnName + " LIKE '%' || '" + tagID + "' || '%'";

        // Only show trashed Notes?
        query.append(Note.trashColumnName).append(" = ").append(trashVal);

        // Only show favoritized or Reminder Notes?
        if (onlyFavorites) {
            query.append(favoritesQuery);
        } else if (onlyReminders) {
            query.append(remindersQuery);
        }

        // Only show Notes matching tagID?
        if (tagID != null) {
            query.append(tagIdQuery);
        }

        /* User query
         * IMPORTANT: Apply onlyFavorites, onlyReminders and tagID here again because
         * the search is done on two columns (title and text) and must be separated
         * by an 'OR'.
         */
        if (searchQuery != null) {
            query.append(" AND ").append(Note.titleColumnName).append(" LIKE '%' || '")
                    .append(searchQuery).append("' || '%' OR ").append(Note.trashColumnName)
                    .append(" = ").append(trashVal);

            // Only show favoritized or Reminder Notes?
            if (onlyFavorites) {
                query.append(favoritesQuery);
            } else if (onlyReminders) {
                query.append(remindersQuery);
            }

            // Only show Notes matching tagID?
            if (tagID != null) {
                query.append(tagIdQuery);
            }

            query.append(" AND ").append(Note.textColumnName).append(" LIKE '%' || '")
                    .append(searchQuery).append("' || '%'");
        }

        // Order by
        query.append(" ORDER BY ");
        if (favoritesOnTop) {
            query.append(Note.favoriteColumnName).append(" DESC, ");
        }
        String sortBy = null;
        switch (sortType) {
            case Constants.SORT_TYPE_ALPHABETICALLY:
                sortBy = Note.titleColumnName;
                break;
            case Constants.SORT_TYPE_CREATION_DATE:
                sortBy = Note.creationEpochColumnName;
                break;
            case Constants.SORT_TYPE_MODIFIED_DATE:
                sortBy = Note.modifiedEpochColumnName;
                break;
        }
        query.append(sortBy);

        // ASC / DESC
        query.append(" ").append(sortDirectionQuery);

        Log.d(TAG, "SQLQuery: " + query.toString());

        return query.toString();
    }

    /**
     * Get all Notes from the db as PagedList LiveData.
     *
     * @param query Search String, use null to not search
     */
    public DataSource.Factory<Integer, Note> getNotesPagedList(final @Nullable String query) {
        Log.d(TAG, "Retrieving all Notes matching query..");
        String queryString = queryBuilder(getSortType(), getSortDirection(),
                false, false, false, getSortFavoritesOnTop(), query, null);
        SimpleSQLiteQuery sqlQuery = new SimpleSQLiteQuery(queryString);
        return mNoteDao.getNotesPagedList(sqlQuery);
    }

    /**
     * Get all Notes which have Tag ID as PagedList LiveData.
     */
    public DataSource.Factory<Integer, Note> getNotesPagedList(final int tagID,
                                                               final @Nullable String query) {
        Log.d(TAG, "Retrieving all Notes matching tag + query..");
        String queryString = queryBuilder(getSortType(), getSortDirection(),
                false, false, false, getSortFavoritesOnTop(), query, tagID);
        SimpleSQLiteQuery sqlQuery = new SimpleSQLiteQuery(queryString);
        return mNoteDao.getNotesPagedList(sqlQuery);
    }

    /**
     * Get all Notes as List based on Tag ID.
     */
    public List<Note> getNotesList(final int tagID) {
        Log.d(TAG, "Retrieving Notes matching tag from db..");
        return DbUtil.rawDB(new Callable<List<Note>>() {
            @Override
            public List<Note> call() {
                return mNoteDao.getNotesList(String.valueOf(tagID));
            }
        });
    }

    /**
     * Get all trashed Notes from the db as PagedList LiveData.
     *
     * @param query Search String, use null to not search
     */
    public DataSource.Factory<Integer, Note> getTrashedNotesPagedList(final @Nullable String query) {
        Log.d(TAG, "Retrieving all trashed Notes..");
        String queryString = queryBuilder(getSortType(), getSortDirection(),
                true, false, false, getSortFavoritesOnTop(), query, null);
        SimpleSQLiteQuery sqlQuery = new SimpleSQLiteQuery(queryString);
        return mNoteDao.getNotesPagedList(sqlQuery);
    }

    /**
     * Get all favoritized Notes from the db as PagedList LiveData.
     *
     * @param query Search String, use null to not search
     */
    public DataSource.Factory<Integer, Note> getFavoritizedNotesPagedList(final @Nullable String query) {
        Log.d(TAG, "Retrieving all favoritized Notes..");
        String queryString = queryBuilder(getSortType(), getSortDirection(),
                false, true, false, false, query, null);
        SimpleSQLiteQuery sqlQuery = new SimpleSQLiteQuery(queryString);
        return mNoteDao.getNotesPagedList(sqlQuery);
    }

    /**
     * Get all reminder Notes from the db as PagedList LiveData.
     *
     * @param query Search String, use null to not search
     */
    public DataSource.Factory<Integer, Note> getReminderNotesPagedList(final @Nullable String query) {
        Log.d(TAG, "Retrieving all reminder Notes..");
        String queryString = queryBuilder(getSortType(), getSortDirection(),
                false, false, true, getSortFavoritesOnTop(), query, null);
        SimpleSQLiteQuery sqlQuery = new SimpleSQLiteQuery(queryString);
        return mNoteDao.getNotesPagedList(sqlQuery);
    }

    /**
     * Get a specific Note as LiveData based on Note ID.
     */
    public LiveData<Note> getNoteLiveData(final int noteID) {
        Log.d(TAG, "Retrieving Note[id:" + noteID + "] from db..");
        return mNoteDao.getNoteLiveData(noteID);
    }

    /**
     * Get a specific Note based on Note ID.
     */
    public Note getNote(final int noteID) {
        Log.d(TAG, "Retrieving Note[id:" + noteID + "] from db..");
        return DbUtil.rawDB(new Callable<Note>() {
            @Override
            public Note call() {
                return mNoteDao.getNote(noteID);
            }
        });
    }

    /**
     * Insert new Note into db and return Note ID.
     */
    public long insertNote(final Note note) {
        long noteID = DbUtil.rawDB(new Callable<Long>() {
            @Override
            public Long call() {
                return mNoteDao.insertNote(note);
            }
        });
        Log.d(TAG, "Inserted Note[id:" + noteID + "] into db..");
        return noteID;
    }

    /**
     * Insert multiple Notes into db and return array with Note ID's.
     */
    public long[] insertNotes(final Note[] notes) {
        long[] noteIDs = DbUtil.rawDB(new Callable<long[]>() {
            @Override
            public long[] call() {
                return mNoteDao.insertNotes(notes);
            }
        });
        Log.d(TAG, "Inserted Notes[id's:" + Arrays.toString(noteIDs) + "] into db..");
        return noteIDs;
    }

    /**
     * Update existing Note.
     */
    public void updateNote(final Note note) {
        AppExecutor.getExecutor().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Updating Note[id:" + note.getId() + "] in db..");
                mNoteDao.updateNote(note);
            }
        });
    }

    /**
     * Update existing Notes.
     */
    public void updateNotes(final Note[] notes) {
        AppExecutor.getExecutor().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Updating Notes[id:" + Arrays.toString(notes) + "] in db..");
                mNoteDao.updateNotes(notes);
            }
        });
    }

    /**
     * Delete existing Note.
     */
    public void deleteNote(final Note note) {
        AppExecutor.getExecutor().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Deleting Note[id:" + note.getId() + "] from db..");
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

    /**
     * Delete all trashed Notes.
     */
    public void deleteAllTrashedNotes() {
        AppExecutor.getExecutor().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Deleting all trashed Notes from db..!");
                mNoteDao.deleteAllTrashedNotes();
            }
        });
    }
}
