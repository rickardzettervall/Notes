package tech.zettervall.notes.repositories;

import android.app.Application;
import android.util.Log;

import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.LiveData;
import androidx.paging.DataSource;

import java.util.List;

import tech.zettervall.notes.AppExecutor;
import tech.zettervall.notes.data.AppDb;
import tech.zettervall.notes.data.TagDao;
import tech.zettervall.notes.models.Tag;
import tech.zettervall.notes.utils.DbUtil;

public class TagRepository {

    private static final String TAG = NoteRepository.class.getSimpleName();
    private static TagRepository INSTANCE;
    private TagDao mTagDao;

    private TagRepository(Application application) {
        AppDb db = AppDb.getInstance(application.getApplicationContext());
        mTagDao = db.tagDao();
    }

    @VisibleForTesting
    public TagRepository(AppDb db) {
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
     * Get all Tags as PagedList LiveData.
     */
    public DataSource.Factory<Integer, Tag> getTagsPagedList() {
        Log.d(TAG, "Retrieving all Tags from db..");
        return mTagDao.getTagsPagedList();
    }

    /**
     * Get all Tags as LiveData.
     */
    public LiveData<List<Tag>> getTagsLiveData() {
        Log.d(TAG, "Retrieving all Tags from db..");
        return mTagDao.getTagsLiveData();
    }

    /**
     * Get all Tags as List.
     */
    public List<Tag> getTagsList() {
        Log.d(TAG, "Retrieving all Tags from db..");
        return DbUtil.rawDB(() -> mTagDao.getTagsList());
    }

    /**
     * Get a specific Tag as LiveData based on Tag ID.
     */
    public LiveData<Tag> getTagLiveData(int tagID) {
        Log.d(TAG, "Retrieving Tag[id:" + tagID + "] from db..");
        return mTagDao.getTagLiveData(tagID);
    }

    /**
     * Get a specific Tag based on Tag ID.
     */
    public Tag getTag(final int tagID) {
        Log.d(TAG, "Retrieving Tag[id:" + tagID + "] from db..");
        return DbUtil.rawDB(() -> mTagDao.getTag(tagID));
    }

    /**
     * Insert new Tag into DB and return Tag ID.
     */
    public long insertTag(final Tag tag) {
        long tagID = DbUtil.rawDB(() -> mTagDao.insertTag(tag));
        Log.d(TAG, "Inserted Tag[id:" + tagID + "] in db..");
        return tagID;
    }

    /**
     * Update existing Tag.
     */
    public void updateTag(final Tag tag) {
        Log.d(TAG, "Updating Tag[id:" + tag.getId() + "] in db..");
        AppExecutor.getExecutor().diskIO().execute(() -> mTagDao.updateTag(tag));
    }

    /**
     * Delete existing Tag.
     */
    public void deleteTag(final Tag tag) {
        Log.d(TAG, "Deleting Tag[id:" + tag.getId() + "] from db..");
        AppExecutor.getExecutor().diskIO().execute(() -> mTagDao.deleteTag(tag));
    }
}
