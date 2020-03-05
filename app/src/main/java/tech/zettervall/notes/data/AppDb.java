package tech.zettervall.notes.data;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import tech.zettervall.mNotes.R;
import tech.zettervall.notes.AppExecutor;
import tech.zettervall.notes.data.typeconverters.IntegerListTypeConverter;
import tech.zettervall.notes.data.typeconverters.StringListTypeConverter;
import tech.zettervall.notes.models.Note;
import tech.zettervall.notes.models.Tag;

/**
 * Room Database.
 * IMPORTANT!! Increment version number when changing any entity class.
 */
@Database(entities = {Note.class, Tag.class}, version = 10)
@TypeConverters({IntegerListTypeConverter.class, StringListTypeConverter.class})
public abstract class AppDb extends RoomDatabase {

    private static final String TAG = AppDb.class.getSimpleName();
    private static AppDb INSTANCE;
    public static final String DB_NAME = "notes_db";

    /**
     * Room Db instance.
     */
    public static AppDb getInstance(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDb.class) {
                if (INSTANCE == null) {
                    Log.d(TAG, "Creating new db instance..");
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDb.class,
                            DB_NAME
                    ).addCallback(new Callback() {
                        @Override
                        public void onCreate(@NonNull SupportSQLiteDatabase db) {
                            super.onCreate(db);
                            AppExecutor.getExecutor().diskIO().execute(() -> {
                                Log.d(TAG, "Creating default tags..");
                                Tag[] tags = {
                                        new Tag(context.getString(R.string.tag_personal)),
                                        new Tag(context.getString(R.string.tag_work))};
                                getInstance(context).tagDao().insertTags(tags);
                            });
                        }
                    })
                            .addMigrations(
                                    DbMigration.MIGRATION_5_7,
                                    DbMigration.MIGRATION_7_8,
                                    DbMigration.MIGRATION_8_9,
                                    DbMigration.MIGRATION_9_10)
                            .build();
                }
            }
        }
        Log.d(TAG, "Getting db instance..");
        return INSTANCE;
    }

    /**
     * DAO for Notes.
     */
    public abstract NoteDao noteDao();

    /**
     * DAO for Tags.
     */
    public abstract TagDao tagDao();
}
