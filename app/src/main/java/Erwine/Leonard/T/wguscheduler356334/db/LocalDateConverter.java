package Erwine.Leonard.T.wguscheduler356334.db;

import androidx.annotation.Nullable;
import androidx.room.TypeConverter;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class LocalDateConverter {

    public static final DateTimeFormatter FULL_FORMATTER = DateTimeFormatter.ofPattern("eee, MMM d, YYYY").withZone(ZoneId.systemDefault());
    public static final DateTimeFormatter LONG_FORMATTER = DateTimeFormatter.ofPattern("eee M/d/YYYY").withZone(ZoneId.systemDefault());
    public static final DateTimeFormatter MEDIUM_FORMATTER = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withZone(ZoneId.systemDefault());
    public static final DateTimeFormatter SHORT_FORMATTER = DateTimeFormatter.ofPattern("MM/d/YYYY").withZone(ZoneId.systemDefault());
    private static final DateTimeFormatter[] ALT_FORMATTER = new DateTimeFormatter[]{
            DateTimeFormatter.ofPattern("M/d/yyyy").withZone(ZoneId.systemDefault()),
            DateTimeFormatter.ofPattern("M-d-yyyy").withZone(ZoneId.systemDefault()),
            DateTimeFormatter.ofPattern("M.d.yyyy").withZone(ZoneId.systemDefault()),
            DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).withZone(ZoneId.systemDefault()),
            DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).withZone(ZoneId.systemDefault()),
            DateTimeFormatter.ISO_LOCAL_DATE.withZone(ZoneId.systemDefault())
    };

    @TypeConverter
    public static LocalDate toLocalDate(Long timeStamp) {
        return (null == timeStamp) ? null : LocalDate.ofEpochDay(timeStamp);
    }

    @TypeConverter
    public static Long fromLocalDate(LocalDate date) {
        return (null == date) ? null : date.toEpochDay();
    }

    @Nullable
    public static LocalDate fromString(String text) {
        if (null == text || (text = text.trim()).isEmpty()) {
            return null;
        }
        LocalDate date;
        try {
            date = LocalDate.from(MEDIUM_FORMATTER.parse(text));
        } catch (DateTimeException e0) {
            date = null;
            for (DateTimeFormatter f : ALT_FORMATTER) {
                try {
                    date = LocalDate.from(f.parse(text));
                } catch (DateTimeException e) {
                    // Ignored
                }
            }
            if (null == date) {
                return LocalDate.parse(text);
            }
        }
        return date;
    }

}
