package Erwine.Leonard.T.wguscheduler356334.db;

import androidx.room.TypeConverter;

import Erwine.Leonard.T.wguscheduler356334.util.IndexedStringList;

public class StringModelListConverter {

    @TypeConverter
    public static IndexedStringList toStringModelList(String value) {
        IndexedStringList list = new IndexedStringList();
        list.setText(value);
        return list;
    }

    @TypeConverter
    public static String fromStringModelList(IndexedStringList value) {
        if (null == value) {
            return "";
        }
        return value.getText();
    }

}
