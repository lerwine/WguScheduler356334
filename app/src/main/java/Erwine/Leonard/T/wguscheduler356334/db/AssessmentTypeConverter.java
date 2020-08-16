package Erwine.Leonard.T.wguscheduler356334.db;

import androidx.room.TypeConverter;

import Erwine.Leonard.T.wguscheduler356334.entity.AssessmentType;

public class AssessmentTypeConverter {
    private static final AssessmentType[] VALUES = AssessmentType.values();

    @TypeConverter
    public static AssessmentType toAssessmentType(Integer value) {
        if (null != value && value >= 0) {
            if (value < VALUES.length) {
                return VALUES[value];
            }
        }
        return AssessmentType.OBJECTIVE_ASSESSMENT;
    }

    @TypeConverter
    public static Integer fromAssessmentType(AssessmentType value) {
        return ((null == value) ? AssessmentType.OBJECTIVE_ASSESSMENT : value).ordinal();
    }

}
