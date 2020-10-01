package Erwine.Leonard.T.wguscheduler356334.db;

import androidx.annotation.Nullable;
import androidx.room.TypeConverter;

import Erwine.Leonard.T.wguscheduler356334.entity.assessment.AssessmentType;

public final class AssessmentTypeConverter {
    private static final AssessmentType[] VALUES = AssessmentType.values();
    public static AssessmentType DEFAULT = VALUES[0];

    @TypeConverter
    public static AssessmentType toAssessmentType(Integer value) {
        return (null != value && value >= 0 && value < VALUES.length) ? VALUES[value] : DEFAULT;
    }

    @TypeConverter
    public static Integer fromAssessmentType(AssessmentType value) {
        return (null == value) ? 0 : value.ordinal();
    }

    public static int compare(@Nullable AssessmentType o1, @Nullable AssessmentType o2) {
        return (null == o1) ? ((null == o2) ? 0 : o2.ordinal()) : ((null == o2) ? -o1.ordinal() : o1.ordinal() - o2.ordinal());
    }

    public static boolean areEqual(@Nullable AssessmentType o1, @Nullable AssessmentType o2) {
        return (null == o1 || o1 == DEFAULT) ? (null == o2 || o2 == DEFAULT) : o1 == o2;
    }

    public static AssessmentType asNonNull(@Nullable AssessmentType value) {
        return (null == value) ? DEFAULT : value;
    }

}
