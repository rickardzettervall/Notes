package tech.zettervall.notes.data.typeconverters;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import androidx.room.TypeConverter;

import java.util.List;

public abstract class StringListTypeConverter {

    @TypeConverter
    public static List<String> jsonToStringArray(String json) {
        return new Gson().fromJson(json, new TypeToken<List<String>>() {
        }.getType());
    }

    @TypeConverter
    public static String stringArrayToJson(List<String> strings) {
        return new Gson().toJson(strings);
    }
}
