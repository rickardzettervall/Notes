package tech.zettervall.notes.data.typeconverters;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import tech.zettervall.notes.data.DbMigration;

public abstract class StringListTypeConverter {

    @TypeConverter
    public static List<String> stringToObjectList(String data) {
        data = DbMigration.MIGRATION_9_10_FIX(data);
        return new Gson().fromJson(data, new TypeToken<List<String>>() {
        }.getType());
    }

    @TypeConverter
    public static String stringListToString(List<String> list) {
        return new Gson().toJson(list);
    }
}
