package Erwine.Leonard.T.wguscheduler356334.entity.assessment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.ForeignKey;
import androidx.room.Ignore;

import java.time.LocalDate;
import java.util.Objects;

import Erwine.Leonard.T.wguscheduler356334.db.AssessmentStatusConverter;
import Erwine.Leonard.T.wguscheduler356334.entity.AbstractNotedEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.course.CourseEntity;

public abstract class AbstractAssessmentEntity<T extends AbstractAssessmentEntity<T>> extends AbstractNotedEntity<T> implements Assessment {

    @ForeignKey(entity = CourseEntity.class, parentColumns = {CourseEntity.COLNAME_ID}, childColumns = {COLNAME_COURSE_ID}, onDelete = ForeignKey.CASCADE, deferred = true)
    @ColumnInfo(name = COLNAME_COURSE_ID)
    private long courseId;
    @ColumnInfo(name = COLNAME_CODE, collate = ColumnInfo.NOCASE)
    @NonNull
    private String code;
    @ColumnInfo(name = COLNAME_NAME)
    @Nullable
    private String name;
    @ColumnInfo(name = COLNAME_STATUS)
    @NonNull
    private AssessmentStatus status;
    @ColumnInfo(name = COLNAME_GOAL_DATE)
    @Nullable
    private LocalDate goalDate;
    @ColumnInfo(name = COLNAME_COMPLETION_DATE)
    @Nullable
    private LocalDate completionDate;
    @ColumnInfo(name = COLNAME_TYPE)
    @NonNull
    private AssessmentType type;

    @Ignore
    protected AbstractAssessmentEntity(long id, long courseId, String code, @Nullable String name, AssessmentStatus status, @Nullable LocalDate goalDate, AssessmentType type, String notes,
                                       @Nullable LocalDate completionDate) {
        super(id, notes);
        this.courseId = courseId;
        this.completionDate = completionDate;
        this.code = SINGLE_LINE_NORMALIZER.apply(code);
        String s = SINGLE_LINE_NORMALIZER.apply(name);
        this.name = (s.isEmpty()) ? null : name;
        this.status = (null == status) ? AssessmentStatus.NOT_STARTED : status;
        this.goalDate = goalDate;
        this.type = (null == type) ? AssessmentType.OBJECTIVE_ASSESSMENT : type;
    }

    @Ignore
    protected AbstractAssessmentEntity(AbstractAssessmentEntity<?> source) {
        super(source);
        this.courseId = source.courseId;
        this.completionDate = source.completionDate;
        this.code = source.code;
        this.name = source.name;
        this.status = source.status;
        this.goalDate = source.goalDate;
        this.type = source.type;
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
     * Gets the title for the assessment.
     *
     * @return The course title, which is always single-line, whitespace-normalized and trimmed if it is not null.
     */
    @Override
    @Nullable
    public String getName() {
        return name;
    }

    @Override
    public void setName(@Nullable String name) {
        String s = SINGLE_LINE_NORMALIZER.apply(name);
        this.name = (s.isEmpty()) ? null : name;
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
        this.status = (null == status) ? AssessmentStatusConverter.DEFAULT : status;
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
    public void setGoalDate(@Nullable LocalDate goalDate) {
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
    public void setCompletionDate(@Nullable LocalDate completionDate) {
        this.completionDate = completionDate;
    }

    @Override
    protected boolean equalsEntity(@NonNull T other) {
        return Objects.equals(courseId, other.getCourseId()) &&
                type == other.getType() &&
                code.equals(other.getCode()) &&
                status == other.getStatus() &&
                Objects.equals(name, other.getName()) &&
                Objects.equals(goalDate, other.getGoalDate()) &&
                Objects.equals(name, other.getName()) &&
                Objects.equals(completionDate, other.getCompletionDate()) &&
                getNotes().equals(other.getNotes());
    }

    @Override
    protected int hashCodeFromProperties() {
        return Objects.hash(courseId, code, name, status, goalDate, completionDate, type, getNotes());
    }

    protected void applyChanges(AbstractAssessmentEntity<?> source) {
        if (source.getId() != getId()) {
            if (getId() != ID_NEW) {
                throw new IllegalStateException();
            }
            setId(source.getId());
        }
        setNotes(source.getNotes());
        this.courseId = source.courseId;
        this.completionDate = source.completionDate;
        this.code = source.code;
        this.name = source.name;
        this.status = source.status;
        this.goalDate = source.goalDate;
        this.type = source.type;
    }
}
