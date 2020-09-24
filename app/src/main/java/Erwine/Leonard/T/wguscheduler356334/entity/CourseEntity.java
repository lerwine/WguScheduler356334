package Erwine.Leonard.T.wguscheduler356334.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;

import java.time.LocalDate;

import Erwine.Leonard.T.wguscheduler356334.db.AppDb;
import Erwine.Leonard.T.wguscheduler356334.util.ToStringBuilder;

/**
 * Represents a row of data from the {@link AppDb#TABLE_NAME_COURSES "courses"} database table.
 */
@Entity(tableName = AppDb.TABLE_NAME_COURSES, indices = {
        @Index(value = CourseEntity.COLNAME_TERM_ID, name = CourseEntity.INDEX_TERM),
        @Index(value = CourseEntity.COLNAME_MENTOR_ID, name = CourseEntity.INDEX_MENTOR),
        @Index(value = {CourseEntity.COLNAME_NUMBER, CourseEntity.COLNAME_TERM_ID}, name = CourseEntity.INDEX_NUMBER, unique = true)
})
public final class CourseEntity extends AbstractCourseEntity<CourseEntity> {

    /**
     * The name of the foreign key index for the {@link #getTermId() "termId"} database column.
     */
    public static final String INDEX_TERM = "IDX_COURSE_TERM";
    /**
     * The name of the foreign key index for the {@link #getMentorId() "mentorId"} database column.
     */
    public static final String INDEX_MENTOR = "IDX_COURSE_MENTOR";
    /**
     * The name of the unique index for the {@link #getNumber() "number"} and {@link #getTermId() "termId"} database columns.
     */
    public static final String INDEX_NUMBER = "IDX_COURSE_NUMBER";

    /**
     * Initializes a new {@code CourseEntity} object to represent an existing row of data in the {@link AppDb#TABLE_NAME_COURSES "courses"} database table.
     *
     * @param number          The WGU-proprietary number/code that is used to refer to the course.
     * @param title           The course title.
     * @param status          The current or final status of the course.
     * @param expectedStart   The date that the user expects to start the course or {@code null} if the expected start date has not been determined.
     * @param actualStart     The date that the user actually started the course or {@code null} if the course hasn't been started, yet.
     * @param expectedEnd     The date that the user expects to finish the course or {@code null} if the expected finish date has not yet been determined.
     * @param actualEnd       The date that the course was actually concluded or {@code null} if the course hasn't yet concluded.
     * @param competencyUnits The number of competency units attributed to the course or {@code 0} if the competency units value is unknown.
     * @param notes           Multi-line text containing notes about the course.
     * @param termId          The value of the {@link TermEntity#COLNAME_ID primary key} for the {@link TermEntity term} associated with the course.
     * @param mentorId        The value of the {@link MentorEntity#COLNAME_ID primary key} for the student's {@link MentorEntity course mentor} for the course.
     * @param id              The value of the {@link #COLNAME_ID primary key column}.
     */
    public CourseEntity(String number, String title, CourseStatus status, LocalDate expectedStart, LocalDate actualStart,
                        LocalDate expectedEnd, LocalDate actualEnd, int competencyUnits, String notes, long termId, Long mentorId, long id) {
        super(id, termId, mentorId, number, title, status, expectedStart, actualStart, expectedEnd, actualEnd, competencyUnits, notes);
    }

    /**
     * Initializes a new {@code CourseEntity} object to represent a new row of data for the {@link AppDb#TABLE_NAME_COURSES "courses"} database table.
     *
     * @param number          The WGU-proprietary number/code that is used to refer to the course.
     * @param title           The course title.
     * @param status          The current or final status of the course.
     * @param expectedStart   The date that the user expects to start the course or {@code null} if the expected start date has not been determined.
     * @param actualStart     The date that the user actually started the course or {@code null} if the course hasn't been started, yet.
     * @param expectedEnd     The date that the user expects to finish the course or {@code null} if the expected finish date has not yet been determined.
     * @param actualEnd       The date that the course was actually concluded or {@code null} if the course hasn't yet concluded.
     * @param competencyUnits The number of competency units attributed to the course or {@code 0} if the competency units value is unknown.
     * @param notes           Multi-line text containing notes about the course.
     * @param termId          The value of the {@link TermEntity#COLNAME_ID primary key} for the {@link TermEntity term} associated with the course.
     * @param mentorId        The value of the {@link MentorEntity#COLNAME_ID primary key} for the student's {@link MentorEntity course mentor} for the course.
     */
    @Ignore
    public CourseEntity(String number, String title, CourseStatus status, LocalDate expectedStart, LocalDate actualStart,
                        LocalDate expectedEnd, LocalDate actualEnd, int competencyUnits, String notes, long termId, Long mentorId) {
        super(null, termId, mentorId, number, title, status, expectedStart, actualStart, expectedEnd, actualEnd, competencyUnits, notes);
    }

    @Ignore
    public CourseEntity(String number, String title, CourseStatus status, LocalDate expectedStart, LocalDate actualStart,
                        LocalDate expectedEnd, LocalDate actualEnd, int competencyUnits, String notes, Long mentorId) {
        super(null, null, mentorId, number, title, status, expectedStart, actualStart, expectedEnd, actualEnd, competencyUnits, notes);
    }

    /**
     * Initializes a new {@code CourseEntity} object to represent a new row of data for the {@link AppDb#TABLE_NAME_COURSES "courses"} database table.
     *
     * @param termId The value of the {@link TermEntity#COLNAME_ID primary key} for the {@link TermEntity term} associated with the course.
     */
    @Ignore
    public CourseEntity(long termId) {
        super(null, termId, null, null, null, CourseStatus.UNPLANNED, null, null, null, null, 0, null);
    }

    @Ignore
    public CourseEntity(AbstractCourseEntity<?> source) {
        super(source);
    }

    /**
     * Initializes a new {@code CourseEntity} object with empty values to represent a new row of data for the {@link AppDb#TABLE_NAME_COURSES "courses"} database table.
     */
    @Ignore
    public CourseEntity() {
        super(null, null, null, null, null, null, null, null, null, null, 0, null);
    }

    @Override
    public void setTermId(long termId) {
        super.setTermId(termId);
    }

    @Override
    public void setMentorId(Long mentorId) {
        super.setMentorId(mentorId);
    }

    @NonNull
    @Override
    public String toString() {
        return ToStringBuilder.toEscapedString(this, false);
    }

}
