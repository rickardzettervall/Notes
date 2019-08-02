package tech.zettervall.notes.data.typeconverters;

import androidx.room.TypeConverter;

import java.util.ArrayList;
import java.util.List;

/**
 * Converts List of Integers to String in the format Num,Num,Num,
 */
public class IntegerListTypeConverter {

    @TypeConverter
    public static List<Integer> stringToIntegerList(String data) {
        ArrayList<Integer> values = new ArrayList<>();
        if (data != null) {
            StringBuilder digit = new StringBuilder();
            for (int i = 0; i < data.length(); i++) {
                if (Character.isDigit(data.charAt(i))) {
                    digit.append(data.charAt(i));
                } else {
                    values.add(Integer.valueOf(digit.toString()));
                    digit.delete(0, digit.length());
                }
            }
        }
        return values;
    }

    @TypeConverter
    public static String integerListToString(List<Integer> list) {
        StringBuilder values = new StringBuilder();
        for (int i : list) {
            values.append(i).append(",");
        }
        return values.toString();
    }
}
