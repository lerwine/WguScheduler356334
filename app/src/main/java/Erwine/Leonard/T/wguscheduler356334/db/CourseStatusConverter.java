package Erwine.Leonard.T.wguscheduler356334.db;

import androidx.room.TypeConverter;

import Erwine.Leonard.T.wguscheduler356334.entity.CourseStatus;

public class CourseStatusConverter {

    @TypeConverter
    public static CourseStatus toCourseStatus(Integer value) {
        if (null != value && value >= 0) {
            CourseStatus[] values = CourseStatus.values();
            if (value < values.length) {
                return values[value];
            }
        }
        return CourseStatus.UNPLANNED;
    }

    @TypeConverter
    public static Integer fromCourseStatus(CourseStatus value) {
        return ((null == value) ? CourseStatus.UNPLANNED : value).ordinal();
    }

}
