package tech.zettervall.notes.data.typeconverters;

import androidx.room.TypeConverter;

import java.util.ArrayList;
import java.util.List;

import tech.zettervall.notes.data.DbMigration;

/**
 * Converts List of Integers to String in the format Num,Num,Num,
 */
public class IntegerListTypeConverter {

    /**
     * @throws NumberFormatException Occurs when db is upgraded from 5 to 7. Because of
     *                               the old way a Tag was stored, in this case a special migration method will be used.
     */
    @TypeConverter
    public static List<Integer> stringToIntegerList(String data) throws NumberFormatException {
        ArrayList<Integer> values = new ArrayList<>();
        try {
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
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return DbMigration.MIGRATION_5_7_FIX(data);
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
