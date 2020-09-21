package Erwine.Leonard.T.wguscheduler356334.entity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.ForeignKey;

import java.time.LocalDate;
import java.util.Objects;

public abstract class AbstractCourseEntity<T extends AbstractCourseEntity<T>> extends AbstractNotedEntity<T> implements Course {

    @ForeignKey(entity = TermEntity.class, parentColumns = {TermEntity.COLNAME_ID}, childColumns = {COLNAME_TERM_ID}, onDelete = ForeignKey.CASCADE, deferred = true)
    @ColumnInfo(name = COLNAME_TERM_ID)
    private Long termId;

    @ForeignKey(entity = MentorEntity.class, parentColumns = {MentorEntity.COLNAME_ID}, childColumns = {COLNAME_MENTOR_ID}, onDelete = ForeignKey.CASCADE, deferred = true)
    @ColumnInfo(name = COLNAME_MENTOR_ID)
    private Long mentorId;

    @ColumnInfo(name = COLNAME_NUMBER, collate = ColumnInfo.NOCASE)
    private String number;

    @ColumnInfo(name = COLNAME_TITLE)
    private String title;

    @ColumnInfo(name = COLNAME_EXPECTED_START)
    private LocalDate expectedStart;

    @ColumnInfo(name = COLNAME_ACTUAL_START)
    private LocalDate actualStart;

    @ColumnInfo(name = COLNAME_EXPECTED_END)
    private LocalDate expectedEnd;

    @ColumnInfo(name = COLNAME_ACTUAL_END)
    private LocalDate actualEnd;

    @ColumnInfo(name = COLNAME_STATUS)
    private CourseStatus status;

    @ColumnInfo(name = COLNAME_COMPETENCY_UNITS)
    private int competencyUnits;

    protected AbstractCourseEntity(Long id, Long termId, Long mentorId, String number, String title, CourseStatus status, LocalDate expectedStart, LocalDate actualStart,
                                   LocalDate expectedEnd, LocalDate actualEnd, int competencyUnits, String notes) {
        super(id, notes);
        this.termId = termId;
        this.mentorId = mentorId;
        this.number = SINGLE_LINE_NORMALIZER.apply(number);
        this.title = SINGLE_LINE_NORMALIZER.apply(title);
        this.status = (null == status) ? CourseStatus.UNPLANNED : status;
        this.expectedStart = expectedStart;
        this.actualStart = actualStart;
        this.expectedEnd = expectedEnd;
        this.actualEnd = actualEnd;
        this.competencyUnits = Math.max(competencyUnits, 0);
    }

    protected AbstractCourseEntity(@NonNull AbstractCourseEntity<?> source) {
        super(source);
    }

    @Override
    @Nullable
    public Long getTermId() {
        return termId;
    }

    @Override
    public void setTermId(long termId) {
        this.termId = termId;
    }

    @Override
    @Nullable
    public Long getMentorId() {
        return mentorId;
    }

    @Override
    public void setMentorId(@Nullable Long mentorId) {
        this.mentorId = mentorId;
    }

    /**
     * Gets the WGU-proprietary number/code for the course.
     *
     * @return The WGU-proprietary number/code that is used to refer to the course, which is always single-line, whitespace-normalized and trimmed..
     */
    @Override
    @NonNull
    public String getNumber() {
        return number;
    }

    @Override
    public void setNumber(String number) {
        this.number = SINGLE_LINE_NORMALIZER.apply(title);
    }

    /**
     * Gets the title for the course.
     *
     * @return The course title, which is always single-line, whitespace-normalized and trimmed..
     */
    @Override
    @NonNull
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(String title) {
        this.title = SINGLE_LINE_NORMALIZER.apply(title);
    }

    @Override
    @NonNull
    public CourseStatus getStatus() {
        return status;
    }

    @Override
    public void setStatus(CourseStatus status) {
        this.status = (null == status) ? CourseStatus.UNPLANNED : status;
    }

    @Override
    @Nullable
    public LocalDate getExpectedStart() {
        return expectedStart;
    }

    @Override
    public void setExpectedStart(@Nullable LocalDate expectedStart) {
        this.expectedStart = expectedStart;
    }

    @Override
    @Nullable
    public LocalDate getActualStart() {
        return actualStart;
    }

    @Override
    public void setActualStart(@Nullable LocalDate actualStart) {
        this.actualStart = actualStart;
    }

    @Override
    @Nullable
    public LocalDate getExpectedEnd() {
        return expectedEnd;
    }

    @Override
    public void setExpectedEnd(@Nullable LocalDate expectedEnd) {
        this.expectedEnd = expectedEnd;
    }

    @Override
    @Nullable
    public LocalDate getActualEnd() {
        return actualEnd;
    }

    @Override
    public void setActualEnd(@Nullable LocalDate actualEnd) {
        this.actualEnd = actualEnd;
    }

    @Override
    public int getCompetencyUnits() {
        return competencyUnits;
    }

    @Override
    public void setCompetencyUnits(int competencyUnits) {
        this.competencyUnits = competencyUnits;
    }

    @Override
    protected boolean equalsEntity(@NonNull T other) {
        return Objects.equals(termId, other.getTermId()) &&
                Objects.equals(mentorId, other.getMentorId()) &&
                number.equals(other.getNumber()) &&
                title.equals(other.getTitle()) &&
                Objects.equals(expectedStart, other.getExpectedStart()) &&
                Objects.equals(actualStart, other.getActualStart()) &&
                Objects.equals(expectedEnd, other.getExpectedEnd()) &&
                Objects.equals(actualEnd, other.getActualEnd()) &&
                status == other.getStatus() &&
                getNotes().equals(other.getNotes());
    }

    @Override
    protected int hashCodeFromProperties() {
        return Objects.hash(termId, mentorId, number, title, expectedStart, actualStart, expectedEnd, actualEnd, status, getNotes());
    }

}
