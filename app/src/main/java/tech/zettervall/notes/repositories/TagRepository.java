package tech.zettervall.notes.repositories;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import tech.zettervall.notes.AppExecutor;
import tech.zettervall.notes.data.NoteDb;
import tech.zettervall.notes.data.TagDao;
import tech.zettervall.notes.models.Tag;

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
     * Get all Tags in LiveData format.
     *
     * @return All Tags in LiveData List
     */
    public LiveData<List<Tag>> getTags() {
        Log.d(TAG, "Retrieving Tags from db..");
        return mTagDao.getTags();
    }

    /**
     * Get a specific Tag based on ID.
     *
     * @param _id ID of Tag
     * @return Tag in LiveData format
     */
    public LiveData<Tag> getTag(int _id) {
        Log.d(TAG, "Retrieving Tag[id:" + _id + "] from db..");
        return mTagDao.getTag(_id);
    }

    /**
     * Insert new Tag into DB and return rowID (tagID).
     *
     * @param tag New Tag to insert
     * @return tagID
     */
    public long insertTag(final Tag tag) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Callable<Long> callable = new Callable<Long>() {
            @Override
            public Long call() {
                return mTagDao.insertTag(tag);
            }
        };
        Future<Long> future = executorService.submit(callable);
        long tagID = 0;
        try {
            tagID = future.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(500, TimeUnit.MILLISECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
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
