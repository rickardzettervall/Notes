package tech.zettervall.notes.data.typeconverters;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import tech.zettervall.notes.models.Tag;

public class TagListTypeConverter {

    @TypeConverter
    public static List<Tag> jsonToTagList(String json) {
        return new Gson().fromJson(json, new TypeToken<List<Tag>>() {
        }.getType());
    }

    @TypeConverter
    public static String tagListToJson(List<Tag> tags) {
        return new Gson().toJson(tags);
    }
}
