package tech.zettervall.notes.repositories;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.paging.DataSource;

import java.util.List;
import java.util.concurrent.Callable;

import tech.zettervall.notes.AppExecutor;
import tech.zettervall.notes.data.NoteDb;
import tech.zettervall.notes.data.TagDao;
import tech.zettervall.notes.models.Tag;
import tech.zettervall.notes.utils.DbUtil;

public class TagRepository {

    private static final String TAG = NoteRepository.class.getSimpleName();
    private static TagRepository INSTANCE;
    private TagDao mTagDao;

    private TagRepository(Application application) {
        NoteDb db = NoteDb.getInstance(application.getApplicationContext());
        mTagDao = db.tagDao();
    }

    public static TagRepository getInstance(final Application application) {
        if (INSTANCE == null) {
            synchronized (TagRepository.class) {
                if (INSTANCE == null) {
                    Log.d(TAG, "Creating new NoteRepository instance..");
                    INSTANCE = new TagRepository(application);
                }
            }
        }
        return INSTANCE;
    }

    /**
     * Get all Tags in PagedList LiveData.
     *
     * @return All Tags in PagedList LiveData
     */
    public DataSource.Factory<Integer, Tag> getTagsPagedList() {
        Log.d(TAG, "Retrieving all Tags from db..");
        return mTagDao.getTagsPagedList();
    }

    /**
     * Get all Tags in LiveData.
     *
     * @return All Tags in LiveData
     */
    public LiveData<List<Tag>> getTagsLiveData() {
        Log.d(TAG, "Retrieving all Tags from db..");
        return mTagDao.getTagsLiveData();
    }

    /**
     * Get all Tags in List.
     *
     * @return List of all Tags
     */
    public List<Tag> getTagsList() {
        Log.d(TAG, "Retrieving all Tags from db..");
        return DbUtil.rawDB(new Callable<List<Tag>>() {
            @Override
            public List<Tag> call() {
                return mTagDao.getTagsList();
            }
        });
    }

    /**
     * Get a specific Tag as LiveData based on ID.
     *
     * @param tagID ID of Tag
     * @return Tag in LiveData
     */
    public LiveData<Tag> getTagLiveData(int tagID) {
        Log.d(TAG, "Retrieving Tag[id:" + tagID + "] from db..");
        return mTagDao.getTagLiveData(tagID);
    }

    /**
     * Get a specific Tag based on ID.
     *
     * @param tagID ID of Tag
     * @return Tag
     */
    public Tag getTag(final int tagID) {
        Log.d(TAG, "Retrieving Tag[id:" + tagID + "] from db..");
        return DbUtil.rawDB(new Callable<Tag>() {
            @Override
            public Tag call() {
                return mTagDao.getTag(tagID);
            }
        });
    }

    /**
     * Insert new Tag into DB and return rowID (tagID).
     *
     * @param tag New Tag to insert
     * @return tagID
     */
    public long insertTag(final Tag tag) {
        long tagID = DbUtil.rawDB(new Callable<Long>() {
            @Override
            public Long call() {
                return mTagDao.insertTag(tag);
            }
        });
        Log.d(TAG, "Inserted Tag[id:" + tagID + "] in db..");
        return tagID;
    }

    /**
     * Update an existing Tag.
     *
     * @param tag Tag Object to update
     */
    public void updateTag(final Tag tag) {
        AppExecutor.getExecutor().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Updating Tag[id:" + tag.getId() + "] in db..");
                mTagDao.updateTag(tag);
            }
        });
    }

    /**
     * Delete an existing Tag.
     *
     * @param tag Tag Object to delete
     */
    public void deleteTag(final Tag tag) {
        AppExecutor.getExecutor().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Deleting Tag[id:" + tag.getId() + "] from db..");
                mTagDao.deleteTag(tag);
            }
        });
    }
}
