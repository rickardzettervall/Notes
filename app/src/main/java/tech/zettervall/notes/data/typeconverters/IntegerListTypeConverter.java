package tech.zettervall.notes.data.typeconverters;

import androidx.room.TypeConverter;

import java.util.ArrayList;
import java.util.List;

import tech.zettervall.notes.data.DbMigration;

/**
 * Converts List of Integers to String in the format "Num","Num","Num",
 */
public abstract class IntegerListTypeConverter {

    @TypeConverter
    public static List<Integer> stringToIntegerList(String data) {
        ArrayList<Integer> values = new ArrayList<>();
        if (data != null) {
            if (data.contains("_id")) { // Db version 5
                return DbMigration.MIGRATION_5_7_FIX(data);
            } else if (!data.contains("\"")) { // Db version 7
                return DbMigration.MIGRATION_7_8_FIX(data);
            } else { // Db current
                StringBuilder digit = new StringBuilder();
                for (int i = 1; i < data.length(); i++) {
                    if (Character.isDigit(data.charAt(i))) {
                        digit.append(data.charAt(i));
                    } else {
                        values.add(Integer.valueOf(digit.toString()));
                        digit.delete(0, digit.length());
                        i = i + 2;
                    }
                }
            }
        }
        return values;
    }

    @TypeConverter
    public static String integerListToString(List<Integer> list) {
        StringBuilder values = new StringBuilder();
        for (int i : list) {
            values.append("\"").append(i).append("\",");
        }
        return values.toString();
    }
}
