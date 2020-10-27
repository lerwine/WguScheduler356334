package Erwine.Leonard.T.wguscheduler356334.db;

import androidx.annotation.Nullable;
import androidx.room.TypeConverter;

import Erwine.Leonard.T.wguscheduler356334.entity.assessment.AssessmentStatus;
import Erwine.Leonard.T.wguscheduler356334.entity.course.CourseStatus;

public class AssessmentStatusConverter {
    private static final AssessmentStatus[] VALUES = AssessmentStatus.values();
    public static final AssessmentStatus DEFAULT = VALUES[0];

    @TypeConverter
    public static AssessmentStatus toAssessmentStatus(@Nullable Integer value) {
        return (null != value && value >= 0 && value < VALUES.length) ? VALUES[value] : DEFAULT;
    }

    @TypeConverter
    public static Integer fromAssessmentStatus(@Nullable AssessmentStatus value) {
        return (null == value) ? 0 : value.ordinal();
    }

    public static int compare(@Nullable AssessmentStatus o1, @Nullable AssessmentStatus o2) {
        return (null == o1) ? ((null == o2) ? 0 : o2.ordinal()) : ((null == o2) ? -o1.ordinal() : o1.ordinal() - o2.ordinal());
    }

    public static boolean areEqual(@Nullable AssessmentStatus o1, @Nullable AssessmentStatus o2) {
        return (null == o1 || o1 == DEFAULT) ? (null == o2 || o2 == DEFAULT) : o1 == o2;
    }

    public static AssessmentStatus asNonNull(@Nullable AssessmentStatus status) {
        return (null == status) ? DEFAULT : status;
    }

    public static AssessmentStatus fromCourseStatus(CourseStatus status) {
        if (null != status) {
            switch (status) {
                case IN_PROGRESS:
                    return AssessmentStatus.IN_PROGRESS;
                case PASSED:
                    return AssessmentStatus.PASSED;
                case NOT_PASSED:
                    return AssessmentStatus.NOT_PASSED;
                default:
                    break;
            }
        }
        return DEFAULT;
    }

}
