package Erwine.Leonard.T.wguscheduler356334.db;

import androidx.room.TypeConverter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import Erwine.Leonard.T.wguscheduler356334.util.Values;

public class StringModelListConverter {

    @TypeConverter
    public static List<String> toStringModelList(String value) {
        List<String> list = new ArrayList<>();
        if (null != value && !(value = value.trim()).isEmpty()) {
            String[] lines = Values.REGEX_LINEBREAKN.split(value);
            Collections.addAll(list, lines);
        }
        return list;
    }

    @TypeConverter
    public static String fromStringModelList(List<String> value) {
        if (null == value || value.isEmpty()) {
            return "";
        }
        Iterator<String> iterator = value.stream().map(Values::asNonNullAndWsNormalized).iterator();
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
