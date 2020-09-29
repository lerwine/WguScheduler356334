package Erwine.Leonard.T.wguscheduler356334.db;

import androidx.annotation.Nullable;
import androidx.room.TypeConverter;

import Erwine.Leonard.T.wguscheduler356334.entity.AssessmentStatus;
import Erwine.Leonard.T.wguscheduler356334.entity.CourseStatus;

public class CourseStatusConverter {
    private static final CourseStatus[] VALUES = CourseStatus.values();
    public static final CourseStatus DEFAULT = VALUES[0];

    @TypeConverter
    public static CourseStatus toCourseStatus(Integer value) {
        return (null != value && value >= 0 && value < VALUES.length) ? VALUES[value] : DEFAULT;
    }

    @TypeConverter
    public static Integer fromCourseStatus(CourseStatus value) {
        return (null == value) ? 0 : value.ordinal();
    }

    public static int compare(@Nullable CourseStatus o1, @Nullable CourseStatus o2) {
        return (null == o1) ? ((null == o2) ? 0 : o2.ordinal()) : ((null == o2) ? -o1.ordinal() : o1.ordinal() - o2.ordinal());
    }

    public static boolean areEqual(@Nullable CourseStatus o1, @Nullable CourseStatus o2) {
        return (null == o1 || o1 == DEFAULT) ? (null == o2 || o2 == DEFAULT) : o1 == o2;
    }

    public static CourseStatus asNonNull(@Nullable CourseStatus status) {
        return (null == status) ? DEFAULT : status;
    }

    public static CourseStatus fromAssessmentStatus(AssessmentStatus status) {
        if (null != status) {
            switch (status) {
                case EVALUATING:
                case IN_PROGRESS:
                    return CourseStatus.IN_PROGRESS;
                case PASSED:
                    return CourseStatus.PASSED;
                case NOT_PASSED:
                    return CourseStatus.NOT_PASSED;
                default:
                    break;
            }
        }
        return DEFAULT;
    }

}
