package tech.zettervall.notes.repositories;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.paging.DataSource;
import androidx.sqlite.db.SimpleSQLiteQuery;

import java.util.List;
import java.util.concurrent.Callable;

import tech.zettervall.notes.AppExecutor;
import tech.zettervall.notes.Constants;
import tech.zettervall.notes.data.NoteDao;
import tech.zettervall.notes.data.NoteDb;
import tech.zettervall.notes.models.Note;
import tech.zettervall.notes.models.Tag;
import tech.zettervall.notes.utils.DbUtil;

public class NoteRepository {

    private static final String TAG = NoteRepository.class.getSimpleName();
    private static NoteRepository INSTANCE;
    private NoteDao mNoteDao;
    private SharedPreferences mSharedPreferences;

    private NoteRepository(Application application) {
        NoteDb db = NoteDb.getInstance(application.getApplicationContext());
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
     * @return Complete SQL query String ready to be used with RawQuery in Room
     */
    private String queryBuilder(int sortType, int sortDirection, boolean trash,
                                boolean onlyFavorites, boolean onlyReminders,
                                boolean favoritesOnTop, @Nullable String searchQuery) {
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

        // Only show trashed Notes?
        query.append(Note.trashColumnName).append(" = ").append(trashVal);

        // Only show favoritized Notes?
        if (onlyFavorites) {
            query.append(favoritesQuery);
        } else if (onlyReminders) {
            query.append(remindersQuery);
        }

        // User query
        if (searchQuery != null) {
            query.append(" AND ").append(Note.titleColumnName).append(" LIKE '%' || '")
                    .append(searchQuery).append("' || '%' OR ").append(Note.trashColumnName)
                    .append(" = ").append(trashVal);
            if (onlyFavorites) {
                query.append(favoritesQuery);
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
     * Get all Notes from the db as DataSource.
     *
     * @param query Search String, use null to not search
     * @return DataSource containing all Notes matching query
     */
    public DataSource.Factory<Integer, Note> getAllNotes(@Nullable String query) {
        Log.d(TAG, "Retrieving all Notes matching query..");
        String queryString = queryBuilder(getSortType(), getSortDirection(),
                false, false, false, getSortFavoritesOnTop(), query);
        SimpleSQLiteQuery sqlQuery = new SimpleSQLiteQuery(queryString);
        return mNoteDao.getNotes(sqlQuery);
    }

    /**
     * Get all Notes matching tag from the db as DataSource.
     *
     * @param tag Tag to query Notes with
     * @return DataSource containing all Notes containing tag
     */
    public DataSource.Factory<Integer, Note> getAllNotesByTag(Tag tag) {
        Log.d(TAG, "Retrieving all Notes matching tag..");
        return mNoteDao.getNotesByTag(tag);
    }

    /**
     * Get all Notes matching tag in plain List,
     * this is used when deleting a tag.
     *
     * @param tag Tag to query Notes with
     * @return List of Notes
     */
    public List<Note> getAllNotesByTagRaw(final Tag tag) {
        Log.d(TAG, "Retrieving Notes matching tag from db..");
        return DbUtil.rawDB(new Callable<List<Note>>() {
            @Override
            public List<Note> call() {
                return mNoteDao.getNotesByTagRaw(tag);
            }
        });
    }

    /**
     * Get all trashed Notes from the db as DataSource.
     *
     * @param query Search String, use null to not search
     * @return DataSource containing all trashed Notes
     */
    public DataSource.Factory<Integer, Note> getAllTrashedNotes(@Nullable String query) {
        Log.d(TAG, "Retrieving all trashed Notes..");
        String queryString = queryBuilder(getSortType(), getSortDirection(),
                true, false, false, false, query);
        SimpleSQLiteQuery sqlQuery = new SimpleSQLiteQuery(queryString);
        return mNoteDao.getNotes(sqlQuery);
    }

    /**
     * Get all favoritized Notes from the db as DataSource.
     *
     * @param query Search String, use null to not search
     * @return DataSource containing all favoritized Notes
     */
    public DataSource.Factory<Integer, Note> getAllFavoritizedNotes(@Nullable String query) {
        Log.d(TAG, "Retrieving all favoritized Notes..");
        String queryString = queryBuilder(getSortType(), getSortDirection(),
                false, true, false, false, query);
        SimpleSQLiteQuery sqlQuery = new SimpleSQLiteQuery(queryString);
        return mNoteDao.getNotes(sqlQuery);
    }

    /**
     * Get all reminder Notes from the db as DataSource.
     *
     * @param query Search String, use null to not search
     * @return DataSource containing all reminder Notes
     */
    public DataSource.Factory<Integer, Note> getAllReminderNotes(@Nullable String query) {
        Log.d(TAG, "Retrieving all reminder Notes..");
        String queryString = queryBuilder(getSortType(), getSortDirection(),
                false, false, true, getSortFavoritesOnTop(), query);
        SimpleSQLiteQuery sqlQuery = new SimpleSQLiteQuery(queryString);
        return mNoteDao.getNotes(sqlQuery);
    }

    /**
     * Get single Note based on unique _id.
     *
     * @param _id Db _id
     * @return LiveData Object containing a Note
     */
    public LiveData<Note> getNote(int _id) {
        Log.d(TAG, "Retrieving Note[id:" + _id + "] from db..");
        return mNoteDao.getNote(_id);
    }

    /**
     * Get single Note based on unique _id in regular Object form, this is
     * primarily used for notification to fetch the Note and to reset the
     * notificationEpoch.
     *
     * @param _id Db _id
     * @return Note Object
     */
    public Note getNoteRaw(final int _id) {
        Log.d(TAG, "Retrieving Note[id:" + _id + "] from db..");
        return DbUtil.rawDB(new Callable<Note>() {
            @Override
            public Note call() {
                return mNoteDao.getNoteRaw(_id);
            }
        });
    }

    /**
     * Insert a single Note into db.
     *
     * @param note Note Object to be inserted
     * @return ID of inserted Note
     */
    public long insertNote(final Note note) {
        long noteID = DbUtil.rawDB(new Callable<Long>() {
            @Override
            public Long call() {
                return mNoteDao.insertNote(note);
            }
        });
        Log.d(NoteRepository.TAG, "Inserted Note[id:" + noteID + "] into db..");
        return noteID;
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
                Log.d(TAG, "Updating Note[id:" + note.getId() + "] in db..");
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
     * Delete alla trashed Notes.
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
