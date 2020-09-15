package Erwine.Leonard.T.wguscheduler356334.entity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;

import java.time.LocalDate;
import java.util.Objects;

import Erwine.Leonard.T.wguscheduler356334.db.AppDb;

/**
 * Represents a row of data from the {@link AppDb#TABLE_NAME_ASSESSMENTS "assessments"} database table.
 */
@Entity(tableName = AppDb.TABLE_NAME_ASSESSMENTS, indices = {
        @Index(value = AssessmentEntity.COLNAME_COURSE_ID, name = AssessmentEntity.INDEX_COURSE),
        @Index(value = AssessmentEntity.COLNAME_CODE, name = AssessmentEntity.INDEX_CODE)
})
public final class AssessmentEntity extends AbstractNotedEntity<AssessmentEntity> implements Comparable<AssessmentEntity> {

    /**
     * The name of the foreign key index for the {@link #COLNAME_COURSE_ID "courseId"} database column.
     */
    public static final String INDEX_COURSE = "IDX_ASSESSMENT_COURSE";
    /**
     * The name of the unique index for the {@link #COLNAME_CODE "code"} database column.
     */
    public static final String INDEX_CODE = "IDX_ASSESSMENT_CODE";
    /**
     * The name of the {@link #courseId "courseId"} database column, which is the value of the {@link CourseEntity#COLNAME_ID primary key} for the {@link CourseEntity course}
     * associated with the assessment.
     */
    public static final String COLNAME_COURSE_ID = "courseId";
    /**
     * The name of the {@link #code "code"} database column, which contains the WGU-proprietary code that is used to refer to the assessment.
     */
    public static final String COLNAME_CODE = "code";
    /**
     * The name of the {@link #status "status"} database column, which the current or final status value for the assessment.
     */
    public static final String COLNAME_STATUS = "status";
    /**
     * The name of the {@link #goalDate "goalDate"}  database column, which contains the pre-determined goal date for completing the assessment.
     */
    public static final String COLNAME_GOAL_DATE = "goalDate";
    /**
     * The name of the  {@link #type "type"} database column, which indicates the assessment type.
     */
    public static final String COLNAME_TYPE = "type";
    /**
     * The name of the {@link #completionDate "completionDate"} database column, which contains the actual completion date for the assessment.
     */
    public static final String COLNAME_COMPLETION_DATE = "completionDate";

    @ForeignKey(entity = CourseEntity.class, parentColumns = {TermEntity.COLNAME_ID}, childColumns = {COLNAME_COURSE_ID}, onDelete = ForeignKey.CASCADE, deferred = true)
    @ColumnInfo(name = COLNAME_COURSE_ID)
    private long courseId;
    @ColumnInfo(name = COLNAME_CODE, collate = ColumnInfo.NOCASE)
    private String code;
    @ColumnInfo(name = COLNAME_STATUS)
    private AssessmentStatus status;
    @ColumnInfo(name = COLNAME_GOAL_DATE)
    private LocalDate goalDate;
    @ColumnInfo(name = COLNAME_COMPLETION_DATE)
    private LocalDate completionDate;
    @ColumnInfo(name = COLNAME_TYPE)
    private AssessmentType type;

    @Ignore
    private AssessmentEntity(Long id, Long courseId, String code, AssessmentStatus status, LocalDate goalDate, AssessmentType type, String notes, LocalDate completionDate) {
        super(id, notes);
        this.courseId = courseId;
        this.completionDate = completionDate;
        this.code = SINGLE_LINE_NORMALIZER.apply(code);
        this.status = (null == status) ? AssessmentStatus.NOT_STARTED : status;
        this.goalDate = goalDate;
        this.type = (null == type) ? AssessmentType.OBJECTIVE_ASSESSMENT : type;
    }

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
        this(id, courseId, code, status, goalDate, type, notes, completionDate);
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
        this(null, courseId, code, status, goalDate, type, notes, completionDate);
    }

    /**
     * Initializes a new {@code AssessmentEntity} object to represent a new row of data for the {@link AppDb#TABLE_NAME_ASSESSMENTS "assessments"} database table.
     *
     * @param courseId the value of the {@link CourseEntity#COLNAME_ID primary key} for the {@link CourseEntity course} associated with the assessment.
     */
    @Ignore
    public AssessmentEntity(AssessmentType type, long courseId) {
        this(null, courseId, null, AssessmentStatus.NOT_STARTED, null, type, null, null);
    }

    /**
     * Initializes a new {@code AssessmentEntity} object with empty values to represent a new row of data for the {@link AppDb#TABLE_NAME_ASSESSMENTS "assessments"} database table.
     */
    @Ignore
    public AssessmentEntity() {
        this(null, null, null, null, null, null, null, null);
    }

    /**
     * Gets the value of the {@link CourseEntity#COLNAME_ID primary key} for the {@link CourseEntity course} associated with the assessment.
     *
     * @return The value of the {@link CourseEntity#COLNAME_ID primary key} for the {@link CourseEntity course} associated with the assessment.
     */
    public long getCourseId() {
        return courseId;
    }

    /**
     * Sets the {@link CourseEntity#COLNAME_ID primary key} value for the {@link CourseEntity course} to be associated with the assessment.
     *
     * @param courseId The {@link CourseEntity#COLNAME_ID primary key} value of the {@link CourseEntity course} to be associated with the assessment.
     */
    public void setCourseId(long courseId) {
        this.courseId = courseId;
    }

    /**
     * Gets the WGU-proprietary code that is used to refer to the assessment.
     *
     * @return The WGU-proprietary code that is used to refer to the assessment, which is always single-line, whitespace-normalized and trimmed.
     */
    @NonNull
    public String getCode() {
        return code;
    }

    /**
     * Sets the WGU-proprietary code that is used to refer to the assessment.
     *
     * @param code The new WGU-proprietary code that will to refer to the assessment.
     */
    public void setCode(String code) {
        this.code = SINGLE_LINE_NORMALIZER.apply(code);
    }

    /**
     * Gets the current or final status value for the assessment.
     *
     * @return The current or final status value for the assessment.
     */
    @NonNull
    public AssessmentStatus getStatus() {
        return status;
    }

    /**
     * Sets the status value for the assessment.
     *
     * @param status The new status value for the assessment.
     */
    public void setStatus(AssessmentStatus status) {
        this.status = (null == status) ? AssessmentStatus.NOT_STARTED : status;
    }

    /**
     * Gets the pre-determined goal date for completing the assessment.
     *
     * @return The pre-determined goal date for completing the assessment or {@code null} if no goal date has been established.
     */
    @Nullable
    public LocalDate getGoalDate() {
        return goalDate;
    }

    /**
     * Sets the pre-determined goal date for completing the assessment.
     *
     * @param goalDate The new pre-determined goal date for completing the assessment or {@code null} if no goal date has been established.
     */
    public void setGoalDate(LocalDate goalDate) {
        this.goalDate = goalDate;
    }

    /**
     * Gets the assessment type.
     *
     * @return The assessment type.
     */
    @NonNull
    public AssessmentType getType() {
        return type;
    }

    /**
     * Sets the assessment type.
     *
     * @param type The new assessment type.
     */
    public void setType(AssessmentType type) {
        this.type = (null == type) ? AssessmentType.OBJECTIVE_ASSESSMENT : type;
    }

    /**
     * Gets the actual completion date for the assessment.
     *
     * @return The actual completion date for the assessment or {@code null} if the course has not yet been concluded.
     */
    @Nullable
    public LocalDate getCompletionDate() {
        return completionDate;
    }

    /**
     * Sets the actual completion date for the assessment.
     *
     * @param completionDate The actual completion date for the assessment or {@code null} if the course has not yet been concluded.
     */
    public void setCompletionDate(LocalDate completionDate) {
        this.completionDate = completionDate;
    }

    @Override
    protected boolean equalsEntity(@NonNull AssessmentEntity other) {
        return courseId == other.courseId &&
                type == other.type &&
                code.equals(other.code) &&
                status == other.status &&
                Objects.equals(goalDate, other.goalDate) &&
                Objects.equals(completionDate, other.completionDate) &&
                getNotes().equals(other.getNotes());
    }

    @Override
    protected int hashCodeFromProperties() {
        return Objects.hash(courseId, code, status, goalDate, completionDate, type, getNotes());
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    @Override
    public synchronized int compareTo(AssessmentEntity o) {
        if (this == o) return 0;
        if (o == null) return -1;
        AssessmentEntity that = (AssessmentEntity) o;
        LocalDate d = that.completionDate;
        int result;
        if (null == d) {
            if (null == (d = that.goalDate)) {
                if (null != goalDate || null != completionDate) {
                    return -1;
                }
            } else if (null == completionDate) {
                if (null == goalDate) {
                    return 1;
                }
                if ((result = goalDate.compareTo(d)) != 0) {
                    return result;
                }
            } else {
                if ((result = completionDate.compareTo(d)) != 0 || (null != goalDate && (result = goalDate.compareTo(d)) != 0)) {
                    return result;
                }
            }
        } else {
            if (null == completionDate) {
                if (null == goalDate) {
                    return 1;
                }
                if ((result = goalDate.compareTo(d)) != 0 || (null != (d = that.goalDate) && (result = goalDate.compareTo(d)) != 0)) {
                    return result;
                }
            } else {
                if ((result = completionDate.compareTo(d)) != 0) {
                    return result;
                }
                LocalDate g = that.goalDate;
                if (null == g) {
                    if (null != goalDate && (result = goalDate.compareTo(d)) != 0) {
                        return result;
                    }
                } else if ((result = ((null == goalDate) ? completionDate : goalDate).compareTo(g)) != 0) {
                    return result;
                }
            }
        }
        if ((result = status.compareTo(that.status)) != 0 || (result = type.compareTo(that.type)) != 0 || (result = code.compareTo(that.code)) != 0) {
            return result;
        }
        Long i = that.getId();
        return (null == i) ? ((null == getId()) ? 0 : -1) : ((null == getId()) ? 1 : Long.compare(getId(), i));
    }

    @NonNull
    @Override
    public String toString() {
        return "AssessmentEntity{" +
                "id=" + getId() +
                ", courseId=" + courseId +
                ", number='" + code + '\'' +
                ", status=" + status +
                ", goalDate=" + goalDate +
                ", evaluationDate=" + completionDate +
                ", type=" + type +
                ", notes='" + getNotes() + '\'' +
                '}';
    }
}
