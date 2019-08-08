package tech.zettervall.notes.data;

import androidx.annotation.NonNull;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public abstract class DbMigration {

    /**
     * Migration of db version 5 -> 7.
     */
    public static Migration MIGRATION_5_7 = new Migration(5, 7) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.beginTransaction();
            try {
                database.execSQL("ALTER TABLE tags RENAME TO tags_tmp");
                database.execSQL("CREATE TABLE tags(_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, title TEXT)");
                database.execSQL("INSERT INTO tags(_id, title) SELECT _id, tag FROM tags_tmp");
                database.execSQL("DROP TABLE tags_tmp");
                database.setTransactionSuccessful();
            } finally {
                database.endTransaction();
            }
        }
    };

    /**
     * Migration of db version 7 -> 8.
     */
    public static Migration MIGRATION_7_8 = new Migration(7, 8) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // No change to db but the way Tags (String) are stored in a Note was changed.
        }
    };

    /**
     * Converts the old Tags JSON String to List.
     */
    public static List<Integer> MIGRATION_5_7_FIX(String oldTagString) {
        /* Example of old Tags String:
         * [{"_id":1,"title":"Personal"},{"_id":1000,"title":"Work"}] */
        List<Integer> indexes = new ArrayList<>();
        int index = 0;
        while (index != -1) {
            index = oldTagString.indexOf("\"_id\":", index);
            if (index != -1) {
                indexes.add(index + 6);
                index++;
            }
        }
        List<Integer> values = new ArrayList<>();
        for (int i : indexes) {
            StringBuilder digitString = new StringBuilder();
            for (int j = 0; j < 10; j++) {
                if (Character.isDigit(oldTagString.charAt(i + j))) {
                    digitString.append(oldTagString.charAt(i + j));
                }
            }
            values.add(Integer.valueOf(digitString.toString()));
        }
        return values;
    }

    /**
     * Converts the old Tags String to List.
     */
    public static List<Integer> MIGRATION_7_8_FIX(String oldTagString) {
        ArrayList<Integer> values = new ArrayList<>();
        StringBuilder digit = new StringBuilder();
        for (int i = 0; i < oldTagString.length(); i++) {
            if (Character.isDigit(oldTagString.charAt(i))) {
                digit.append(oldTagString.charAt(i));
            } else {
                values.add(Integer.valueOf(digit.toString()));
                digit.delete(0, digit.length());
            }
        }
        return values;
    }
}
