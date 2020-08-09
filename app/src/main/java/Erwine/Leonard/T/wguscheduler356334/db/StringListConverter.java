package Erwine.Leonard.T.wguscheduler356334.db;

import androidx.room.TypeConverter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import Erwine.Leonard.T.wguscheduler356334.util.Values;

public class StringListConverter {

    @TypeConverter
    public static List<String> toStringList(String value) {
        ArrayList<String> list = new ArrayList<>();
        if (null != value && !(value = value.trim()).isEmpty()) {
            String[] lines = Values.REGEX_LINEBREAKN.split(value);
            Collections.addAll(list, lines);
        }
        return list;
    }

    @TypeConverter
    public static String fromStringList(List<String> value) {
        if (null == value || value.isEmpty()) {
            return "";
        }
        Iterator<String> iterator = value.stream().filter(Objects::nonNull).map(Values::asNonNullAndWsNormalized).iterator();
        if (!iterator.hasNext()) {
            return "";
        }
        String text = iterator.next();
        while (text.isEmpty()) {
            if (!iterator.hasNext()) {
                return "";
            }
            text = iterator.next();
        }
        StringBuilder stringBuilder = new StringBuilder(text);
        while (iterator.hasNext()) {
            stringBuilder.append("\n").append(iterator.next());
        }
        return stringBuilder.toString().trim();
    }

}
