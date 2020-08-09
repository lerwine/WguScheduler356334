package Erwine.Leonard.T.wguscheduler356334.db;

import androidx.room.TypeConverter;

import Erwine.Leonard.T.wguscheduler356334.entity.AssessmentStatus;

public class AssessmentStatusConverter {

    @TypeConverter
    public static AssessmentStatus toAssessmentStatus(Integer value) {
        if (null != value && value >= 0) {
            AssessmentStatus[] values = AssessmentStatus.values();
            if (value < values.length) {
                return values[value];
            }
        }
        return AssessmentStatus.NOT_STARTED;
    }

    @TypeConverter
    public static Integer fromCourseStatus(AssessmentStatus value) {
        return ((null == value) ? AssessmentStatus.NOT_STARTED : value).ordinal();
    }

}
