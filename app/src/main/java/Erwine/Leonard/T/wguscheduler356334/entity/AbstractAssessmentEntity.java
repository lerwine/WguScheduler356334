package Erwine.Leonard.T.wguscheduler356334.entity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.ForeignKey;
import androidx.room.Ignore;

import java.time.LocalDate;
import java.util.Objects;

import Erwine.Leonard.T.wguscheduler356334.db.AppDb;

public abstract class AbstractAssessmentEntity<T extends AbstractAssessmentEntity<T>> extends AbstractNotedEntity<T> {

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

    public static final String STATE_KEY_ID = AppDb.TABLE_NAME_ASSESSMENTS + "." + COLNAME_ID;
    public static final String STATE_KEY_CODE = AppDb.TABLE_NAME_ASSESSMENTS + "." + COLNAME_CODE;
    public static final String STATE_KEY_STATUS = AppDb.TABLE_NAME_ASSESSMENTS + "." + COLNAME_STATUS;
    public static final String STATE_KEY_GOAL_DATE = AppDb.TABLE_NAME_ASSESSMENTS + "." + COLNAME_GOAL_DATE;
    public static final String STATE_KEY_COMPLETION_DATE = AppDb.TABLE_NAME_ASSESSMENTS + "." + COLNAME_COMPLETION_DATE;
    public static final String STATE_KEY_TYPE = AppDb.TABLE_NAME_ASSESSMENTS + "." + COLNAME_TYPE;
    public static final String STATE_KEY_NOTES = AppDb.TABLE_NAME_ASSESSMENTS + "." + COLNAME_NOTES;
    public static final String STATE_KEY_ORIGINAL_COURSE_ID = "o:" + AbstractCourseEntity.STATE_KEY_ID;
    public static final String STATE_KEY_ORIGINAL_CODE = "o:" + STATE_KEY_CODE;
    public static final String STATE_KEY_ORIGINAL_STATUS = "o:" + STATE_KEY_STATUS;
    public static final String STATE_KEY_ORIGINAL_GOAL_DATE = "o:" + STATE_KEY_GOAL_DATE;
    public static final String STATE_KEY_ORIGINAL_COMPLETION_DATE = "o:" + STATE_KEY_COMPLETION_DATE;
    public static final String STATE_KEY_ORIGINAL_TYPE = "o:" + STATE_KEY_TYPE;
    public static final String STATE_KEY_ORIGINAL_NOTES = "o:" + STATE_KEY_NOTES;

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
    protected AbstractAssessmentEntity(Long id, Long courseId, String code, AssessmentStatus status, LocalDate goalDate, AssessmentType type, String notes, LocalDate completionDate) {
        super(id, notes);
        this.courseId = courseId;
        this.completionDate = completionDate;
        this.code = SINGLE_LINE_NORMALIZER.apply(code);
        this.status = (null == status) ? AssessmentStatus.NOT_STARTED : status;
        this.goalDate = goalDate;
        this.type = (null == type) ? AssessmentType.OBJECTIVE_ASSESSMENT : type;
    }

    protected AbstractAssessmentEntity(AbstractAssessmentEntity<?> source) {
        super(source);
        this.courseId = source.courseId;
        this.completionDate = source.completionDate;
        this.code = source.code;
        this.status = source.status;
        this.goalDate = source.goalDate;
        this.type = source.type;
    }

    @Ignore
    protected AbstractAssessmentEntity(@NonNull Bundle bundle, boolean original) {
        super(STATE_KEY_ID, (original) ? STATE_KEY_ORIGINAL_NOTES : STATE_KEY_NOTES, bundle);
        if (bundle.containsKey(AbstractCourseEntity.STATE_KEY_ID)) {
            courseId = bundle.getLong(AbstractCourseEntity.STATE_KEY_ID);
        }
        if (bundle.containsKey(STATE_KEY_GOAL_DATE)) {
            goalDate = LocalDate.ofEpochDay(bundle.getLong(STATE_KEY_GOAL_DATE));
        }
        if (bundle.containsKey(STATE_KEY_COMPLETION_DATE)) {
            completionDate = LocalDate.ofEpochDay(bundle.getLong(STATE_KEY_COMPLETION_DATE));
        }
        code = bundle.getString(STATE_KEY_CODE, "");
        status = (bundle.containsKey(STATE_KEY_STATUS)) ? AssessmentStatus.valueOf(bundle.getString(STATE_KEY_STATUS)) : AssessmentStatus.NOT_STARTED;
        type = (bundle.containsKey(STATE_KEY_TYPE)) ? AssessmentType.valueOf(bundle.getString(STATE_KEY_TYPE)) : AssessmentType.OBJECTIVE_ASSESSMENT;
    }

    public void saveState(@NonNull Bundle bundle, boolean original) {
        Long id = getId();
        if (null != id) {
            bundle.putLong(STATE_KEY_ID, getId());
        }
        id = courseId;
        if (null != id) {
            bundle.putLong((original) ? STATE_KEY_ORIGINAL_COURSE_ID : AbstractCourseEntity.STATE_KEY_ID, id);
        }
        LocalDate d = goalDate;
        if (null != d) {
            bundle.putLong((original) ? STATE_KEY_ORIGINAL_GOAL_DATE : STATE_KEY_GOAL_DATE, d.toEpochDay());
        }
        d = completionDate;
        if (null != d) {
            bundle.putLong((original) ? STATE_KEY_ORIGINAL_COMPLETION_DATE : STATE_KEY_COMPLETION_DATE, d.toEpochDay());
        }
        bundle.putString((original) ? STATE_KEY_ORIGINAL_CODE : STATE_KEY_CODE, code);
        bundle.putString((original) ? STATE_KEY_ORIGINAL_STATUS : STATE_KEY_STATUS, status.name());
        bundle.putString((original) ? STATE_KEY_ORIGINAL_TYPE : STATE_KEY_TYPE, type.name());
        bundle.putString((original) ? STATE_KEY_ORIGINAL_NOTES : STATE_KEY_NOTES, getNotes());
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
    protected boolean equalsEntity(@NonNull T other) {
        return courseId == other.getCourseId() &&
                type == other.getType() &&
                code.equals(other.getCode()) &&
                status == other.getStatus() &&
                Objects.equals(goalDate, other.getGoalDate()) &&
                Objects.equals(completionDate, other.getCompletionDate()) &&
                getNotes().equals(other.getNotes());
    }

    @Override
    protected int hashCodeFromProperties() {
        return Objects.hash(courseId, code, status, goalDate, completionDate, type, getNotes());
    }

}
