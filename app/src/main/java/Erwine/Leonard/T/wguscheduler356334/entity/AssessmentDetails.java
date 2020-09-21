package Erwine.Leonard.T.wguscheduler356334.entity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.DatabaseView;
import androidx.room.Ignore;

import java.time.LocalDate;

@DatabaseView(
        viewName = "assessmentDetailView",
        value = "SELECT assessments.*," +
                "courses.number as [courseNumber], courses.title as [courseTitle], courses.expectedStart as [expectedCourseStart]," +
                "courses.actualStart as [actualCourseStart], courses.expectedEnd as [expectedCourseEnd], courses.actualEnd as [actualCourseEnd]," +
                "courses.status as [courseStatus], courses.competencyUnits, courses.notes as [courseNotes], courses.termId, courses.mentorId\n" +
                "FROM assessments LEFT JOIN courses ON assessments.courseId = courses.id\n" +
                "GROUP BY assessments.id ORDER BY [completionDate], [goalDate]"
)
public class AssessmentDetails extends AbstractAssessmentEntity<AssessmentDetails> {

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
    private Long termId;
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

    public AssessmentDetails(String code, AssessmentStatus status, LocalDate goalDate, AssessmentType type, String notes, LocalDate completionDate, long courseId,
                             String courseNumber, String courseTitle, CourseStatus courseStatus, LocalDate expectedCourseStart, LocalDate actualCourseStart,
                             LocalDate expectedCourseEnd, LocalDate actualCourseEnd, int competencyUnits, String courseNotes, long termId, Long mentorId, long id) {
        super(id, courseId, code, status, goalDate, type, notes, completionDate);
        setCourse(new CourseEntity(courseNumber, courseTitle, courseStatus, expectedCourseStart, actualCourseStart, expectedCourseEnd, actualCourseEnd,
                competencyUnits, courseNotes, termId, mentorId, courseId));
    }

    @Ignore
    public AssessmentDetails(AssessmentEntity source) {
        super(source);
    }

    @Ignore
    public AssessmentDetails(AssessmentEntity source, AbstractCourseEntity<?> course) {
        super(source);
        if (null == course) {
            courseNumber = courseTitle = courseNotes = "";
            courseStatus = CourseStatus.UNPLANNED;
            expectedCourseStart = actualCourseStart = expectedCourseEnd = actualCourseEnd = null;
            competencyUnits = 0;
            termId = mentorId = null;
        } else {
            setCourse(course);
        }
    }

    @Ignore
    public AssessmentDetails(@NonNull Bundle bundle, boolean original) {
        super(bundle, original);
        if (bundle.containsKey(AbstractCourseEntity.STATE_KEY_ID)) {
            setCourse(new CourseEntity(bundle, original));
        }
    }

    @Override
    public void saveState(@NonNull Bundle bundle, boolean original) {
        super.saveState(bundle, original);
    }

    public AbstractCourseEntity<?> getCourse() {
        return course;
    }

    public synchronized void setCourse(@NonNull AbstractCourseEntity<?> course) {
        this.course = course;
        Long id = course.getId();
        if (null == id) {
            throw new IllegalArgumentException();
        }
        super.setCourseId(id);
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
}
