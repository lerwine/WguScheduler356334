package Erwine.Leonard.T.wguscheduler356334.db;

import androidx.room.TypeConverter;

import java.time.LocalDate;

public class LocalDateConverter {

    @TypeConverter
    public static LocalDate toLocalDate(Long timeStamp) {
        return (null == timeStamp) ? null : LocalDate.ofEpochDay(timeStamp);
    }

    @TypeConverter
    public static  Long fromLocalDate(LocalDate date) {
        return (null == date) ? null : date.toEpochDay();
    }
}
