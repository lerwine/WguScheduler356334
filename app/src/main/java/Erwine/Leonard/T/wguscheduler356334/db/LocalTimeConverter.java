package Erwine.Leonard.T.wguscheduler356334.db;

import androidx.room.TypeConverter;

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class LocalTimeConverter {

    public static final DateTimeFormatter MEDIUM_FORMATTER = DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM).withZone(ZoneId.systemDefault());

    @TypeConverter
    public static LocalTime toLocalTime(Integer minutes) {
        return (null == minutes) ? null : LocalTime.ofSecondOfDay((long) minutes * 60L);
    }

    @TypeConverter
    public static Integer fromLocalTime(LocalTime time) {
        return (null == time) ? null : time.toSecondOfDay() / 60;
    }

}
