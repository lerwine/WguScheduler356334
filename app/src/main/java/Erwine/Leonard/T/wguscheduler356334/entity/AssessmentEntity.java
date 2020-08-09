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
        @Index(value = AssessmentEntity.COLNAME_CODE, name = AssessmentEntity.INDEX_CODE, unique = true)
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
    private String title;
    private AssessmentStatus status;
    private LocalDate goalDate;
    private LocalDate evaluationDate;
    private boolean performanceAsseessment;

    public AssessmentEntity(String code, String title, AssessmentStatus status, LocalDate goalDate, boolean performanceAsseessment, LocalDate evaluationDate, int courseId,
                            int id) {
        this(code, title, status, goalDate, performanceAsseessment, evaluationDate, courseId);
        this.id = id;
    }

    @Ignore
    public AssessmentEntity(String code, String title, AssessmentStatus status, LocalDate goalDate, boolean performanceAsseessment, LocalDate evaluationDate, int courseId) {
        this(code, title, status, goalDate, performanceAsseessment, evaluationDate);
        this.courseId = courseId;
    }

    @Ignore
    public AssessmentEntity(String code, String title, AssessmentStatus status, LocalDate goalDate, boolean performanceAsseessment, LocalDate evaluationDate) {
        this(code, title, status, goalDate, performanceAsseessment);
        this.evaluationDate = evaluationDate;
    }

    @Ignore
    public AssessmentEntity(String code, String title, AssessmentStatus status, LocalDate goalDate, boolean performanceAsseessment) {
        this.code = Values.asNonNullAndWsNormalized(code);
        this.title = Values.asNonNullAndWsNormalized(title);
        this.status = (null == status) ? AssessmentStatus.NOT_STARTED : status;
        this.goalDate = goalDate;
        this.performanceAsseessment = performanceAsseessment;
    }

    @Ignore
    public AssessmentEntity() {
        this(null, null, null, null, false);
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = Values.asNonNullAndWsNormalized(title);
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

    public boolean isPerformanceAsseessment() {
        return performanceAsseessment;
    }

    public void setPerformanceAsseessment(boolean performanceAsseessment) {
        this.performanceAsseessment = performanceAsseessment;
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
                performanceAsseessment == that.performanceAsseessment &&
                code.equals(that.code) &&
                title.equals(that.title) &&
                status == that.status &&
                Objects.equals(goalDate, that.goalDate) &&
                Objects.equals(evaluationDate, that.evaluationDate);
    }

    @Override
    public int hashCode() {
        if (id > 0) {
            return id;
        }
        return Objects.hash(id, courseId, code, title, status, goalDate, evaluationDate, performanceAsseessment);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public String toString() {
        return "AssessmentEntity{" +
                "id=" + id +
                ", courseId=" + courseId +
                ", number='" + code + '\'' +
                ", title='" + title + '\'' +
                ", status=" + status +
                ", goalDate=" + goalDate +
                ", evaluationDate=" + evaluationDate +
                ", performanceAsseessment=" + performanceAsseessment +
                '}';
    }
}
