package Erwine.Leonard.T.wguscheduler356334.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;

import java.time.LocalDate;

import Erwine.Leonard.T.wguscheduler356334.db.AppDb;

/**
 * Represents a row of data from the {@link AppDb#TABLE_NAME_ASSESSMENTS "assessments"} database table.
 */
@Entity(tableName = AppDb.TABLE_NAME_ASSESSMENTS, indices = {
        @Index(value = AssessmentEntity.COLNAME_COURSE_ID, name = AssessmentEntity.INDEX_COURSE),
        @Index(value = AssessmentEntity.COLNAME_CODE, name = AssessmentEntity.INDEX_CODE)
})
public final class AssessmentEntity extends AbstractAssessmentEntity<AssessmentEntity> implements Comparable<AssessmentEntity> {

    /**
     * The name of the foreign key index for the {@link #COLNAME_COURSE_ID "courseId"} database column.
     */
    public static final String INDEX_COURSE = "IDX_ASSESSMENT_COURSE";
    /**
     * The name of the unique index for the {@link #COLNAME_CODE "code"} database column.
     */
    public static final String INDEX_CODE = "IDX_ASSESSMENT_CODE";

    /**
     * Initializes a new {@code AssessmentEntity} object to represent an existing row of data in the {@link AppDb#TABLE_NAME_ASSESSMENTS "assessments"} database table.
     *
     * @param code           the WGU-proprietary code that is used to refer to the assessment.
     * @param status         the current or final status value for the assessment.
     * @param goalDate       the pre-determined goal date for completing the assessment or {@code null} if no goal date has been established.
     * @param type           the assessment type.
     * @param notes          multi-line text containing notes about the assessment.
     * @param completionDate the actual completion date for the assessment or {@code null} if the course has not yet been concluded.
     * @param courseId       the value of the {@link CourseEntity#COLNAME_ID primary key} for the {@link CourseEntity course} associated with the assessment.
     * @param id             The value of the {@link #COLNAME_ID primary key column}.
     */
    public AssessmentEntity(String code, AssessmentStatus status, LocalDate goalDate, AssessmentType type, String notes, LocalDate completionDate, long courseId,
                            long id) {
        super(id, courseId, code, status, goalDate, type, notes, completionDate);
    }

    /**
     * Initializes a new {@code AssessmentEntity} object to represent a new row of data for the {@link AppDb#TABLE_NAME_ASSESSMENTS "assessments"} database table.
     *
     * @param code           the WGU-proprietary code that is used to refer to the assessment.
     * @param status         the current or final status value for the assessment.
     * @param goalDate       the pre-determined goal date for completing the assessment or {@code null} if no goal date has been established.
     * @param type           the assessment type.
     * @param notes          multi-line text containing notes about the assessment.
     * @param completionDate the actual completion date for the assessment or {@code null} if the course has not yet been concluded.
     * @param courseId       the value of the {@link CourseEntity#COLNAME_ID primary key} for the {@link CourseEntity course} associated with the assessment.
     */
    @Ignore
    public AssessmentEntity(String code, AssessmentStatus status, LocalDate goalDate, AssessmentType type, String notes, LocalDate completionDate, long courseId) {
        super(null, courseId, code, status, goalDate, type, notes, completionDate);
    }

    /**
     * Initializes a new {@code AssessmentEntity} object to represent a new row of data for the {@link AppDb#TABLE_NAME_ASSESSMENTS "assessments"} database table.
     *
     * @param courseId the value of the {@link CourseEntity#COLNAME_ID primary key} for the {@link CourseEntity course} associated with the assessment.
     */
    @Ignore
    public AssessmentEntity(AssessmentType type, long courseId) {
        super(null, courseId, null, AssessmentStatus.NOT_STARTED, null, type, null, null);
    }

    /**
     * Initializes a new {@code AssessmentEntity} object with empty values to represent a new row of data for the {@link AppDb#TABLE_NAME_ASSESSMENTS "assessments"} database table.
     */
    @Ignore
    public AssessmentEntity() {
        super(null, null, null, null, null, null, null, null);
    }

    @Override
    public synchronized int compareTo(AssessmentEntity o) {
        if (this == o) return 0;
        if (o == null) return -1;
        LocalDate d = o.getCompletionDate();
        int result;
        if (null == d) {
            if (null == (d = o.getGoalDate())) {
                if (null != getGoalDate() || null != getCompletionDate()) {
                    return -1;
                }
            } else if (null == getCompletionDate()) {
                if (null == getGoalDate()) {
                    return 1;
                }
                if ((result = getGoalDate().compareTo(d)) != 0) {
                    return result;
                }
            } else {
                if ((result = getCompletionDate().compareTo(d)) != 0 || (null != getGoalDate() && (result = getGoalDate().compareTo(d)) != 0)) {
                    return result;
                }
            }
        } else {
            if (null == getCompletionDate()) {
                if (null == getGoalDate()) {
                    return 1;
                }
                if ((result = getGoalDate().compareTo(d)) != 0 || (null != (d = o.getGoalDate()) && (result = getGoalDate().compareTo(d)) != 0)) {
                    return result;
                }
            } else {
                if ((result = getCompletionDate().compareTo(d)) != 0) {
                    return result;
                }
                LocalDate g = o.getGoalDate();
                if (null == g) {
                    if (null != getGoalDate() && (result = getGoalDate().compareTo(d)) != 0) {
                        return result;
                    }
                } else if ((result = ((null == getGoalDate()) ? getCompletionDate() : getGoalDate()).compareTo(g)) != 0) {
                    return result;
                }
            }
        }
        if ((result = getStatus().compareTo(o.getStatus())) != 0 || (result = getType().compareTo(o.getType())) != 0 || (result = getCode().compareTo(o.getCode())) != 0) {
            return result;
        }
        Long i = o.getId();
        return (null == i) ? ((null == getId()) ? 0 : -1) : ((null == getId()) ? 1 : Long.compare(getId(), i));
    }

    @NonNull
    @Override
    public String toString() {
        return "AssessmentEntity{" +
                "id=" + getId() +
                ", courseId=" + getCourseId() +
                ", number='" + getCode() + '\'' +
                ", status=" + getStatus() +
                ", goalDate=" + getGoalDate() +
                ", completionDat=" + getCompletionDate() +
                ", type=" + getType() +
                ", notes='" + getNotes() + '\'' +
                '}';
    }
}
