package Erwine.Leonard.T.wguscheduler356334.entity.assessment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.DatabaseView;
import androidx.room.Ignore;

import java.time.LocalDate;

import Erwine.Leonard.T.wguscheduler356334.db.AppDb;
import Erwine.Leonard.T.wguscheduler356334.entity.IdIndexedEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.course.AbstractCourseEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.course.Course;
import Erwine.Leonard.T.wguscheduler356334.entity.course.CourseEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.course.CourseStatus;
import Erwine.Leonard.T.wguscheduler356334.util.ToStringBuilder;

@DatabaseView(
        viewName = AppDb.VIEW_NAME_ASSESSMENT_DETAIL,
        value = "SELECT assessments.*," +
                "courses.number as [courseNumber], courses.title as [courseTitle], courses.expectedStart as [expectedCourseStart]," +
                "courses.actualStart as [actualCourseStart], courses.expectedEnd as [expectedCourseEnd], courses.actualEnd as [actualCourseEnd]," +
                "courses.status as [courseStatus], courses.competencyUnits, courses.notes as [courseNotes], courses.termId, courses.mentorId\n" +
                "FROM assessments LEFT JOIN courses ON assessments.courseId = courses.id\n" +
                "GROUP BY assessments.id ORDER BY [completionDate], [goalDate]"
)
public final class AssessmentDetails extends AbstractAssessmentEntity<AssessmentDetails> {

    /**
     * The name of the {@link #termId "termId"} view column, which contains the name of the term.
     */
    public static final String COLNAME_TERM_ID = "termId";

    /**
     * The name of the {@link #mentorId "mentorId"} view column, which contains the name of the term.
     */
    public static final String COLNAME_MENTOR_ID = "mentorId";

    /**
     * The name of the {@link #courseNumber "courseNumber"} view column, which contains the name of the term.
     */
    public static final String COLNAME_COURSE_NUMBER = "courseNumber";

    /**
     * The name of the {@link #courseTitle "courseTitle"} view column, which contains the name of the term.
     */
    public static final String COLNAME_COURSE_TITLE = "courseTitle";

    /**
     * The name of the {@link #expectedCourseStart "expectedCourseStart"} view column, which contains the name of the term.
     */
    public static final String COLNAME_EXPECTED_COURSE_START = "expectedCourseStart";

    /**
     * The name of the {@link #actualCourseStart "actualCourseStart"} view column, which contains the name of the term.
     */
    public static final String COLNAME_ACTUAL_COURSE_START = "actualCourseStart";

    /**
     * The name of the {@link #expectedCourseEnd "expectedCourseEnd"} view column, which contains the name of the term.
     */
    public static final String COLNAME_EXPECTED_COURSE_END = "expectedCourseEnd";

    /**
     * The name of the {@link #actualCourseEnd "actualCourseEnd"} view column, which contains the name of the term.
     */
    public static final String COLNAME_ACTUAL_COURSE_END = "actualCourseEnd";

    /**
     * The name of the {@link #courseStatus "courseStatus"} view column, which contains the name of the term.
     */
    public static final String COLNAME_COURSE_STATUS = "courseStatus";

    /**
     * The name of the {@link #competencyUnits "competencyUnits"} view column, which contains the name of the term.
     */
    public static final String COLNAME_COMPETENCY_UNITS = "competencyUnits";

    /**
     * The name of the {@link #courseNotes "courseNotes"} view column, which contains the name of the term.
     */
    public static final String COLNAME_COURSE_NOTES = "courseNotes";

    @Ignore
    private AbstractCourseEntity<?> course;
    @ColumnInfo(name = COLNAME_TERM_ID)
    private long termId;
    @ColumnInfo(name = COLNAME_MENTOR_ID)
    private Long mentorId;
    @ColumnInfo(name = COLNAME_COURSE_NUMBER)
    private String courseNumber;
    @ColumnInfo(name = COLNAME_COURSE_TITLE)
    private String courseTitle;
    @ColumnInfo(name = COLNAME_EXPECTED_COURSE_START)
    private LocalDate expectedCourseStart;
    @ColumnInfo(name = COLNAME_ACTUAL_COURSE_START)
    private LocalDate actualCourseStart;
    @ColumnInfo(name = COLNAME_EXPECTED_COURSE_END)
    private LocalDate expectedCourseEnd;
    @ColumnInfo(name = COLNAME_ACTUAL_COURSE_END)
    private LocalDate actualCourseEnd;
    @ColumnInfo(name = COLNAME_COURSE_STATUS)
    private CourseStatus courseStatus;
    @ColumnInfo(name = COLNAME_COMPETENCY_UNITS)
    private Integer competencyUnits;
    @ColumnInfo(name = COLNAME_COURSE_NOTES)
    private String courseNotes;

    public AssessmentDetails(String code, String name, AssessmentStatus status, LocalDate goalDate, AssessmentType type, String notes, LocalDate completionDate, long courseId,
                             String courseNumber, String courseTitle, CourseStatus courseStatus, LocalDate expectedCourseStart, LocalDate actualCourseStart,
                             LocalDate expectedCourseEnd, LocalDate actualCourseEnd, int competencyUnits, String courseNotes, long termId, Long mentorId, long id) {
        super(IdIndexedEntity.assertNotNewId(id), IdIndexedEntity.assertNotNewId(courseId), code, name, status, goalDate, type, notes, completionDate);
        setCourse(new CourseEntity(courseNumber, courseTitle, courseStatus, expectedCourseStart, actualCourseStart, expectedCourseEnd, actualCourseEnd,
                competencyUnits, courseNotes, termId, mentorId, courseId));
    }

    @Ignore
    public AssessmentDetails(AssessmentEntity source) {
        super(source);
    }

    @Ignore
    public AssessmentDetails(AbstractCourseEntity<?> course) {
        super(ID_NEW, ID_NEW, null, null, null, null, null, null, null);
        if (null == course) {
            courseNumber = courseTitle = courseNotes = "";
            courseStatus = CourseStatus.UNPLANNED;
            expectedCourseStart = actualCourseStart = expectedCourseEnd = actualCourseEnd = null;
            competencyUnits = 0;
            termId = ID_NEW;
            mentorId = null;
        } else {
            setCourse(course);
        }
    }

    public long getTermId() {
        return course.getTermId();
    }

    public void setTermId(long termId) {
        course.setTermId(termId);
        this.termId = course.getTermId();
    }

    public Long getMentorId() {
        return course.getMentorId();
    }

    public void setMentorId(Long mentorId) {
        course.setMentorId(mentorId);
        this.mentorId = course.getMentorId();
    }

    public String getCourseNumber() {
        return course.getNumber();
    }

    public void setCourseNumber(String courseNumber) {
        course.setNumber(courseNumber);
        this.courseNumber = course.getNumber();
    }

    public String getCourseTitle() {
        return course.getTitle();
    }

    public void setCourseTitle(String courseTitle) {
        course.setTitle(courseTitle);
        this.courseTitle = course.getTitle();
    }

    public LocalDate getExpectedCourseStart() {
        return course.getExpectedStart();
    }

    public void setExpectedCourseStart(LocalDate expectedCourseStart) {
        course.setExpectedStart(expectedCourseStart);
        this.expectedCourseStart = course.getExpectedStart();
    }

    public LocalDate getActualCourseStart() {
        return course.getActualStart();
    }

    public void setActualCourseStart(LocalDate actualCourseStart) {
        course.setActualStart(actualCourseStart);
        this.actualCourseStart = course.getActualStart();
    }

    public LocalDate getExpectedCourseEnd() {
        return course.getExpectedEnd();
    }

    public void setExpectedCourseEnd(LocalDate expectedCourseEnd) {
        course.setExpectedEnd(expectedCourseEnd);
        this.expectedCourseEnd = course.getExpectedEnd();
    }

    public LocalDate getActualCourseEnd() {
        return course.getActualEnd();
    }

    public void setActualCourseEnd(LocalDate actualCourseEnd) {
        course.setActualEnd(actualCourseEnd);
        this.actualCourseEnd = course.getActualEnd();
    }

    public CourseStatus getCourseStatus() {
        return course.getStatus();
    }

    public void setCourseStatus(CourseStatus courseStatus) {
        course.setStatus(courseStatus);
        this.courseStatus = course.getStatus();
    }

    public Integer getCompetencyUnits() {
        return course.getCompetencyUnits();
    }

    public void setCompetencyUnits(Integer competencyUnits) {
        course.setCompetencyUnits(competencyUnits);
        this.competencyUnits = course.getCompetencyUnits();
    }

    public String getCourseNotes() {
        return course.getNotes();
    }

    public void setCourseNotes(String courseNotes) {
        course.setNotes(courseNotes);
        this.courseNotes = course.getNotes();
    }

    @Override
    public void restoreState(@NonNull Bundle bundle, boolean isOriginal) {
        super.restoreState(bundle, isOriginal);
        CourseEntity c = new CourseEntity();
        if (bundle.containsKey(c.stateKey(Course.COLNAME_ID, isOriginal)) || bundle.containsKey(c.stateKey(Course.COLNAME_STATUS, isOriginal))) {
            c.restoreState(bundle, isOriginal);
            setCourse(c);
        }
    }

    @Override
    public synchronized void saveState(@NonNull Bundle bundle, boolean isOriginal) {
        super.saveState(bundle, isOriginal);
        if (null != course) {
            course.saveState(bundle, isOriginal);
        }
    }

    public AbstractCourseEntity<?> getCourse() {
        return course;
    }

    public synchronized void setCourse(@NonNull AbstractCourseEntity<?> course) {
        this.course = course;
        super.setCourseId(IdIndexedEntity.assertNotNewId(course.getId()));
        courseNumber = course.getNumber();
        courseTitle = course.getTitle();
        courseNotes = course.getNotes();
        courseStatus = course.getStatus();
        expectedCourseStart = course.getExpectedStart();
        actualCourseStart = course.getActualStart();
        expectedCourseEnd = course.getExpectedEnd();
        actualCourseEnd = course.getActualEnd();
        competencyUnits = course.getCompetencyUnits();
        termId = course.getTermId();
        mentorId = course.getMentorId();
    }

    @Override
    public void setCourseId(long courseId) {
        throw new UnsupportedOperationException();
    }

    @NonNull
    @Override
    public String toString() {
        return ToStringBuilder.toEscapedString(this, false);
    }

    @Override
    public synchronized void appendPropertiesAsStrings(@NonNull ToStringBuilder sb) {
        super.appendPropertiesAsStrings(sb);
        sb.append("course", course, false);
    }

    public void applyChanges(AssessmentEntity entity, AbstractCourseEntity<?> courseEntity) {
        super.applyChanges(entity);
        course = courseEntity;
        courseNumber = course.getNumber();
        courseTitle = course.getTitle();
        courseNotes = course.getNotes();
        courseStatus = course.getStatus();
        expectedCourseStart = course.getExpectedStart();
        actualCourseStart = course.getActualStart();
        expectedCourseEnd = course.getExpectedEnd();
        actualCourseEnd = course.getActualEnd();
        competencyUnits = course.getCompetencyUnits();
        termId = course.getTermId();
        mentorId = course.getMentorId();
    }
}
