package Erwine.Leonard.T.wguscheduler356334.entity.course;

import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.style.StyleSpan;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Ignore;

import java.time.LocalDate;
import java.util.Objects;

import Erwine.Leonard.T.wguscheduler356334.entity.AbstractNotedEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.IdIndexedEntity;

import static android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;

public abstract class AbstractCourseEntity<T extends AbstractCourseEntity<T>> extends AbstractNotedEntity<T> implements Course {

    public static Spanned toPickerItemDescription(AbstractCourseEntity<?> source) {
        if (null == source) {
            return SpannedString.valueOf("");
        }
        SpannableStringBuilder builder = new SpannableStringBuilder(source.getNumber());
        builder.setSpan(new StyleSpan(Typeface.BOLD), 0, builder.length(), SPAN_EXCLUSIVE_EXCLUSIVE);
        return builder.append(": ").append(source.getTitle());
    }

    @ColumnInfo(name = COLNAME_TERM_ID)
    private long termId;

    @ColumnInfo(name = COLNAME_MENTOR_ID)
    @Nullable
    private Long mentorId;

    @ColumnInfo(name = COLNAME_NUMBER, collate = ColumnInfo.NOCASE)
    @NonNull
    private String number;

    @ColumnInfo(name = COLNAME_TITLE)
    @NonNull
    private String title;

    @ColumnInfo(name = COLNAME_EXPECTED_START)
    @Nullable
    private LocalDate expectedStart;

    @ColumnInfo(name = COLNAME_ACTUAL_START)
    @Nullable
    private LocalDate actualStart;

    @ColumnInfo(name = COLNAME_EXPECTED_END)
    @Nullable
    private LocalDate expectedEnd;

    @ColumnInfo(name = COLNAME_ACTUAL_END)
    @Nullable
    private LocalDate actualEnd;

    @ColumnInfo(name = COLNAME_STATUS)
    @NonNull
    private CourseStatus status;

    @ColumnInfo(name = COLNAME_COMPETENCY_UNITS)
    private int competencyUnits;

    protected AbstractCourseEntity(long id, long termId, @Nullable Long mentorId, String number, String title, CourseStatus status, @Nullable LocalDate expectedStart, @Nullable LocalDate actualStart,
                                   @Nullable LocalDate expectedEnd, @Nullable LocalDate actualEnd, int competencyUnits, String notes) {
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

    @Ignore
    protected AbstractCourseEntity(@NonNull AbstractCourseEntity<?> source) {
        super(source);
        this.termId = source.termId;
        this.mentorId = source.mentorId;
        this.number = source.number;
        this.title = source.title;
        this.status = source.status;
        this.expectedStart = source.expectedStart;
        this.actualStart = source.actualStart;
        this.expectedEnd = source.expectedEnd;
        this.actualEnd = source.actualEnd;
        this.competencyUnits = source.competencyUnits;
    }

    @Override
    public long getTermId() {
        return termId;
    }

    @Override
    public void setTermId(long termId) {
        this.termId = IdIndexedEntity.assertNotNewId(termId);
    }

    @Override
    @Nullable
    public Long getMentorId() {
        return mentorId;
    }

    @Override
    public void setMentorId(@Nullable Long mentorId) {
        this.mentorId = IdIndexedEntity.nullIfNewId(mentorId);
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
        this.number = SINGLE_LINE_NORMALIZER.apply(number);
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
        return termId == other.getTermId() &&
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
