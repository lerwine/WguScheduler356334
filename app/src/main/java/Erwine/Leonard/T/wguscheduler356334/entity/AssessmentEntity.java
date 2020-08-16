package Erwine.Leonard.T.wguscheduler356334.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.time.LocalDate;
import java.util.Objects;

import Erwine.Leonard.T.wguscheduler356334.db.AppDb;
import Erwine.Leonard.T.wguscheduler356334.util.Values;

@Entity(tableName = AppDb.TABLE_NAME_ASSESSMENTS, indices = {
        @Index(value = AssessmentEntity.COLNAME_COURSE_ID, name = AssessmentEntity.INDEX_COURSE),
        @Index(value = AssessmentEntity.COLNAME_CODE, name = AssessmentEntity.INDEX_CODE)
})
public class AssessmentEntity {

    public static final String INDEX_COURSE = "IDX_ASSESSMENT_COURSE";
    public static final String INDEX_CODE = "IDX_ASSESSMENT_CODE";
    public static final String COLNAME_COURSE_ID = "courseId";
    public static final String COLNAME_CODE = "code";
    @PrimaryKey(autoGenerate = true)
    private Integer id;
    @ForeignKey(entity = CourseEntity.class, parentColumns = {TermEntity.COLNAME_ID}, childColumns = {COLNAME_COURSE_ID}, onDelete = ForeignKey.RESTRICT, deferred = true)
    @ColumnInfo(name = COLNAME_COURSE_ID)
    private int courseId;
    @ColumnInfo(name = COLNAME_CODE)
    private String code;
    private AssessmentStatus status;
    private LocalDate goalDate;
    private LocalDate evaluationDate;
    private AssessmentType type;
    private String notes;

    public AssessmentEntity(String code, AssessmentStatus status, LocalDate goalDate, AssessmentType type, String notes, LocalDate evaluationDate, int courseId,
                            int id) {
        this(code, status, goalDate, type, notes, evaluationDate, courseId);
        this.id = id;
    }

    @Ignore
    public AssessmentEntity(String code, AssessmentStatus status, LocalDate goalDate, AssessmentType type, String notes, LocalDate evaluationDate, int courseId) {
        this(code, status, goalDate, type, notes, evaluationDate);
        this.courseId = courseId;
    }

    @Ignore
    public AssessmentEntity(String code, AssessmentStatus status, LocalDate goalDate, AssessmentType type, String notes, LocalDate evaluationDate) {
        this(code, status, goalDate, type, notes);
        this.evaluationDate = evaluationDate;
    }

    @Ignore
    public AssessmentEntity(String code, AssessmentStatus status, LocalDate goalDate, AssessmentType type, String notes) {
        this.code = Values.asNonNullAndWsNormalized(code);
        this.status = (null == status) ? AssessmentStatus.NOT_STARTED : status;
        this.goalDate = goalDate;
        this.type = (null == type) ? AssessmentType.OBJECTIVE_ASSESSMENT : type;
        this.notes = Values.asNonNullAndWsNormalizedMultiLine(notes);
    }

    @Ignore
    public AssessmentEntity() {
        this(null, null, null, null, null);
    }

    public Integer getId() {
        return id;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = Values.asNonNullAndWsNormalized(code);
    }

    public AssessmentStatus getStatus() {
        return status;
    }

    public void setStatus(AssessmentStatus status) {
        this.status = (null == status) ? AssessmentStatus.NOT_STARTED : status;
    }

    public LocalDate getGoalDate() {
        return goalDate;
    }

    public void setGoalDate(LocalDate goalDate) {
        this.goalDate = goalDate;
    }

    public AssessmentType getType() {
        return type;
    }

    public void setType(AssessmentType type) {
        this.type = (null == type) ? AssessmentType.OBJECTIVE_ASSESSMENT : type;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = Values.asNonNullAndWsNormalizedMultiLine(notes);
    }

    public LocalDate getEvaluationDate() {
        return evaluationDate;
    }

    public void setEvaluationDate(LocalDate evaluationDate) {
        this.evaluationDate = evaluationDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AssessmentEntity that = (AssessmentEntity) o;
        if (null != id) {
            return id.equals(that.id);
        }
        return null == that.id &&
                courseId == that.courseId &&
                type == that.type &&
                code.equals(that.code) &&
                status == that.status &&
                Objects.equals(goalDate, that.goalDate) &&
                Objects.equals(evaluationDate, that.evaluationDate) &&
                notes.equals(that.notes);
    }

    @Override
    public int hashCode() {
        if (id > 0) {
            return id;
        }
        return Objects.hash(id, courseId, code, status, goalDate, evaluationDate, type, notes);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public String toString() {
        return "AssessmentEntity{" +
                "id=" + id +
                ", courseId=" + courseId +
                ", number='" + code + '\'' +
                ", status=" + status +
                ", goalDate=" + goalDate +
                ", evaluationDate=" + evaluationDate +
                ", type=" + type +
                ", notes='" + notes + '\'' +
                '}';
    }
}
