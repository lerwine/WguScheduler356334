package Erwine.Leonard.T.wguscheduler356334.entity;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import Erwine.Leonard.T.wguscheduler356334.db.AppDb;
import Erwine.Leonard.T.wguscheduler356334.db.DbLoader;
import Erwine.Leonard.T.wguscheduler356334.util.StringHelper;
import Erwine.Leonard.T.wguscheduler356334.util.StringNormalizationOption;

@Entity(tableName = AppDb.TABLE_NAME_COURSES, indices = {
        @Index(value = CourseEntity.COLNAME_TERM_ID, name = CourseEntity.INDEX_TERM),
        @Index(value = CourseEntity.COLNAME_MENTOR_ID, name = CourseEntity.INDEX_MENTOR),
        @Index(value = {CourseEntity.COLNAME_NUMBER, CourseEntity.COLNAME_TERM_ID}, name = CourseEntity.INDEX_NUMBER, unique = true)
})
public class CourseEntity {

    public static final String INDEX_TERM = "IDX_COURSE_TERM";
    public static final String INDEX_MENTOR = "IDX_COURSE_MENTOR";
    public static final String INDEX_NUMBER = "IDX_COURSE_NUMBER";
    public static final String COLNAME_ID = "id";
    public static final String COLNAME_TERM_ID = "termId";
    public static final String COLNAME_MENTOR_ID = "mentorId";
    public static final String COLNAME_NUMBER = "number";
    private static final Function<String, String> SINGLE_LINE_NORMALIZER = StringHelper.getNormalizer(StringNormalizationOption.SINGLE_LINE);
    private static final Function<String, String> MULTI_LINE_NORMALIZER = StringHelper.getNormalizer();

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COLNAME_ID)
    private Integer id;
    @ForeignKey(entity = TermEntity.class, parentColumns = {TermEntity.COLNAME_ID}, childColumns = {COLNAME_TERM_ID}, onDelete = ForeignKey.RESTRICT, deferred = true)
    @ColumnInfo(name = COLNAME_TERM_ID)
    private int termId;
    @ForeignKey(entity = MentorEntity.class, parentColumns = {MentorEntity.COLNAME_ID}, childColumns = {COLNAME_MENTOR_ID}, onDelete = ForeignKey.RESTRICT, deferred = true)
    @ColumnInfo(name = COLNAME_MENTOR_ID)
    private Integer mentorId;
    @ColumnInfo(name = COLNAME_NUMBER)
    private String number;
    private String title;
    private LocalDate expectedStart;
    private LocalDate actualStart;
    private LocalDate expectedEnd;
    private LocalDate actualEnd;
    private CourseStatus status;
    private int competencyUnits;
    private String notes;
    @Ignore
    private LiveData<List<AssessmentEntity>> assessments;

    public CourseEntity(String number, String title, CourseStatus status, LocalDate expectedStart, LocalDate actualStart,
                        LocalDate expectedEnd, LocalDate actualEnd, Integer competencyUnits, String notes, int termId, Integer mentorId, int id) {
        this(number, title, status, expectedStart, actualStart, expectedEnd, actualEnd, competencyUnits, notes, termId, mentorId);
        this.id = id;
    }

    @Ignore
    public CourseEntity(String number, String title, CourseStatus status, LocalDate expectedStart, LocalDate actualStart,
                        LocalDate expectedEnd, LocalDate actualEnd, Integer competencyUnits, String notes, int termId, Integer mentorId) {
        this(number, title, status, expectedStart, actualStart, expectedEnd, actualEnd, competencyUnits, notes);
        this.termId = termId;
        this.mentorId = mentorId;
    }

    @Ignore
    public CourseEntity(String number, String title, CourseStatus status, LocalDate expectedStart, LocalDate actualStart,
                        LocalDate expectedEnd, LocalDate actualEnd, Integer competencyUnits, String notes) {
        this.number = SINGLE_LINE_NORMALIZER.apply(number);
        this.title = SINGLE_LINE_NORMALIZER.apply(title);
        this.status = (null == status) ? CourseStatus.UNPLANNED : status;
        this.expectedStart = expectedStart;
        this.actualStart = actualStart;
        this.expectedEnd = expectedEnd;
        this.actualEnd = actualEnd;
        this.competencyUnits = (null == competencyUnits) ? 0 : competencyUnits;
        this.notes = MULTI_LINE_NORMALIZER.apply(notes);
    }

    @Ignore
    public CourseEntity() {
        this(null, null, null, null, null, null, null, null, null);
    }

    public Integer getId() {
        return id;
    }

    public int getTermId() {
        return termId;
    }

    public void setTermId(int termId) {
        this.termId = termId;
    }

    public Integer getMentorId() {
        return mentorId;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = SINGLE_LINE_NORMALIZER.apply(title);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = SINGLE_LINE_NORMALIZER.apply(title);
    }

    public CourseStatus getStatus() {
        return status;
    }

    public void setStatus(CourseStatus status) {
        this.status = (null == status) ? CourseStatus.UNPLANNED : status;
    }

    public LocalDate getExpectedStart() {
        return expectedStart;
    }

    public void setExpectedStart(LocalDate expectedStart) {
        this.expectedStart = expectedStart;
    }

    public LocalDate getActualStart() {
        return actualStart;
    }

    public void setActualStart(LocalDate actualStart) {
        this.actualStart = actualStart;
    }

    public LocalDate getExpectedEnd() {
        return expectedEnd;
    }

    public void setExpectedEnd(LocalDate expectedEnd) {
        this.expectedEnd = expectedEnd;
    }

    public LocalDate getActualEnd() {
        return actualEnd;
    }

    public void setActualEnd(LocalDate actualEnd) {
        this.actualEnd = actualEnd;
    }

    public void setMentorId(Integer mentorId) {
        this.mentorId = mentorId;
    }

    public int getCompetencyUnits() {
        return competencyUnits;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = MULTI_LINE_NORMALIZER.apply(notes);
    }

    @Ignore
    public LiveData<List<AssessmentEntity>> getAssessments(Context context) {
        if (null == assessments) {
            if (id < 1) {
                throw new IllegalStateException();
            }
            assessments = DbLoader.getInstance(context).getAssessmentsByCourseId(id);
        }
        return assessments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CourseEntity that = (CourseEntity) o;
        if (null != id) {
            return id.equals(that.id);
        }
        return null == that.id &&
                termId == that.termId &&
                Objects.equals(mentorId, that.mentorId) &&
                number.equals(that.number) &&
                title.equals(that.title) &&
                Objects.equals(expectedStart, that.expectedStart) &&
                Objects.equals(actualStart, that.actualStart) &&
                Objects.equals(expectedEnd, that.expectedEnd) &&
                Objects.equals(actualEnd, that.actualEnd) &&
                status == that.status &&
                notes.equals(that.notes);
    }

    @Override
    public int hashCode() {
        if (id > 0) {
            return id;
        }
        return Objects.hash(id, termId, mentorId, number, title, expectedStart, actualStart, expectedEnd, actualEnd, status, notes);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public String toString() {
        return "CourseEntity{" +
                "id=" + id +
                ", termId=" + termId +
                ", mentorId=" + mentorId +
                ", number='" + number + '\'' +
                ", title='" + title + '\'' +
                ", expectedStart=" + expectedStart +
                ", actualStart=" + actualStart +
                ", expectedEnd=" + expectedEnd +
                ", actualEnd=" + actualEnd +
                ", status=" + status +
                ", notes='" + notes + '\'' +
                '}';
    }

    public void setCompetencyUnits(int competencyUnits) {
        this.competencyUnits = competencyUnits;
    }

    class SampleData {
        private CourseEntity course;
        private MentorEntity mentor;
        private AssessmentEntity[] assessments;

        public CourseEntity getCourse() {
            return course;
        }

        public MentorEntity getMentor() {
            return mentor;
        }

        public AssessmentEntity[] getAssessments() {
            return assessments;
        }
    }
}
