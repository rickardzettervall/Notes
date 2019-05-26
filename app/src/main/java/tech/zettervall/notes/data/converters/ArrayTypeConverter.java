package tech.zettervall.notes.data.converters;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import androidx.room.TypeConverter;

public abstract class ArrayTypeConverter {

    @TypeConverter
    public static String[] jsonToStringArray(String json) {
        return new Gson().fromJson(json, new TypeToken<String[]>() {
        }.getType());
    }

    @TypeConverter
    public static String stringArrayToJson(String[] array) {
        return new Gson().toJson(array);
    }
}
