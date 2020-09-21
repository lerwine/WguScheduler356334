package Erwine.Leonard.T.wguscheduler356334.entity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.ForeignKey;

import java.time.LocalDate;
import java.util.Objects;

import Erwine.Leonard.T.wguscheduler356334.db.AppDb;

public abstract class AbstractCourseEntity<T extends AbstractCourseEntity<T>> extends AbstractNotedEntity<T> {

    /**
     * The name of the {@link #termId "termId"} database column, which is the value of the {@link TermEntity#COLNAME_ID primary key} for the {@link TermEntity course's term}.
     */
    public static final String COLNAME_TERM_ID = "termId";
    /**
     * The name of the {@link #termId "mentorId"} database column, which is the value of the {@link MentorEntity#COLNAME_ID primary key} for the {@link MentorEntity course mentor}.
     */
    public static final String COLNAME_MENTOR_ID = "mentorId";
    /**
     * The name of the {@link #number "number"} database column, which contains the WGU-proprietary number/code that is used to refer to the course.
     */
    public static final String COLNAME_NUMBER = "number";
    /**
     * The name of the {@link #title "title"} database column, which contains the course title.
     */
    public static final String COLNAME_TITLE = "title";
    /**
     * The name of the {@link #expectedStart "expectedStart"} database column, which contains the date that the user expected to start teh course.
     */
    public static final String COLNAME_EXPECTED_START = "expectedStart";
    /**
     * The name of the {@link #actualStart "actualStart"} database column, which contains the actual start date of the course.
     */
    public static final String COLNAME_ACTUAL_START = "actualStart";
    /**
     * The name of the {@link #expectedEnd "expectedEnd"} database column, which contains the date the user expected to finish the course.
     */
    public static final String COLNAME_EXPECTED_END = "expectedEnd";
    /**
     * The name of the {@link #actualEnd "actualEnd"} database column, which contains the date that the course actually ended.
     */
    public static final String COLNAME_ACTUAL_END = "actualEnd";
    /**
     * The name of the {@link #status "status"} database column, which contains the current or final status of the course.
     */
    public static final String COLNAME_STATUS = "status";
    /**
     * The name of the {@link #competencyUnits "competencyUnits"} database column, which contains the competencyUnits attributed to the course.
     */
    public static final String COLNAME_COMPETENCY_UNITS = "competencyUnits";

    public static final String STATE_KEY_ID = AppDb.TABLE_NAME_COURSES + "." + COLNAME_ID;
    public static final String STATE_KEY_NUMBER = AppDb.TABLE_NAME_COURSES + "." + COLNAME_NUMBER;
    public static final String STATE_KEY_TITLE = AppDb.TABLE_NAME_COURSES + "." + COLNAME_TITLE;
    public static final String STATE_KEY_EXPECTED_START = AppDb.TABLE_NAME_COURSES + "." + COLNAME_EXPECTED_START;
    public static final String STATE_KEY_ACTUAL_START = AppDb.TABLE_NAME_COURSES + "." + COLNAME_ACTUAL_START;
    public static final String STATE_KEY_EXPECTED_END = AppDb.TABLE_NAME_COURSES + "." + COLNAME_EXPECTED_END;
    public static final String STATE_KEY_ACTUAL_END = AppDb.TABLE_NAME_COURSES + "." + COLNAME_ACTUAL_END;
    public static final String STATE_KEY_STATUS = AppDb.TABLE_NAME_COURSES + "." + COLNAME_STATUS;
    public static final String STATE_KEY_COMPETENCY_UNITS = AppDb.TABLE_NAME_COURSES + "." + COLNAME_COMPETENCY_UNITS;
    public static final String STATE_KEY_NOTES = AppDb.TABLE_NAME_COURSES + "." + COLNAME_NOTES;
    public static final String STATE_KEY_ORIGINAL_TERM_ID = "o:" + AbstractTermEntity.STATE_KEY_ID;
    public static final String STATE_KEY_ORIGINAL_MENTOR_ID = "o:" + AbstractMentorEntity.STATE_KEY_ID;
    public static final String STATE_KEY_ORIGINAL_NUMBER = "o:" + STATE_KEY_NUMBER;
    public static final String STATE_KEY_ORIGINAL_TITLE = "o:" + STATE_KEY_TITLE;
    public static final String STATE_KEY_ORIGINAL_EXPECTED_START = "o:" + STATE_KEY_EXPECTED_START;
    public static final String STATE_KEY_ORIGINAL_ACTUAL_START = "o:" + STATE_KEY_ACTUAL_START;
    public static final String STATE_KEY_ORIGINAL_EXPECTED_END = "o:" + STATE_KEY_EXPECTED_END;
    public static final String STATE_KEY_ORIGINAL_ACTUAL_END = "o:" + STATE_KEY_ACTUAL_END;
    public static final String STATE_KEY_ORIGINAL_STATUS = "o:" + STATE_KEY_STATUS;
    public static final String STATE_KEY_ORIGINAL_COMPETENCY_UNITS = "o:" + STATE_KEY_COMPETENCY_UNITS;
    public static final String STATE_KEY_ORIGINAL_NOTES = "o:" + STATE_KEY_NOTES;

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

    protected AbstractCourseEntity(@NonNull Bundle bundle, boolean original) {
        super(STATE_KEY_ID, (original) ? STATE_KEY_ORIGINAL_NOTES : STATE_KEY_NOTES, bundle);
        Long id = termId;
        String key = (original) ? STATE_KEY_ORIGINAL_TERM_ID : AbstractTermEntity.STATE_KEY_ID;
        if (bundle.containsKey(key)) {
            termId = bundle.getLong(key);
        }
        key = (original) ? STATE_KEY_ORIGINAL_MENTOR_ID : AbstractMentorEntity.STATE_KEY_ID;
        if (bundle.containsKey(key)) {
            mentorId = bundle.getLong(key);
        }
        number = bundle.getString((original) ? STATE_KEY_ORIGINAL_NUMBER : STATE_KEY_NUMBER, "");
        title = bundle.getString((original) ? STATE_KEY_ORIGINAL_TITLE : STATE_KEY_TITLE, "");
        key = (original) ? STATE_KEY_ORIGINAL_STATUS : STATE_KEY_STATUS;
        status = (bundle.containsKey(key)) ? CourseStatus.valueOf(bundle.getString(key)) : CourseStatus.UNPLANNED;
        key = (original) ? STATE_KEY_ORIGINAL_EXPECTED_START : STATE_KEY_EXPECTED_START;
        if (bundle.containsKey(key)) {
            expectedStart = LocalDate.ofEpochDay(bundle.getLong(key));
        }
        key = (original) ? STATE_KEY_ORIGINAL_ACTUAL_START : STATE_KEY_ACTUAL_START;
        if (bundle.containsKey(key)) {
            actualStart = LocalDate.ofEpochDay(bundle.getLong(key));
        }
        key = (original) ? STATE_KEY_ORIGINAL_EXPECTED_END : STATE_KEY_EXPECTED_END;
        if (bundle.containsKey(key)) {
            expectedEnd = LocalDate.ofEpochDay(bundle.getLong(key));
        }
        key = (original) ? STATE_KEY_ORIGINAL_ACTUAL_END : STATE_KEY_ACTUAL_END;
        if (bundle.containsKey(key)) {
            actualEnd = LocalDate.ofEpochDay(bundle.getLong(key));
        }
        competencyUnits = bundle.getInt((original) ? STATE_KEY_ORIGINAL_COMPETENCY_UNITS : STATE_KEY_COMPETENCY_UNITS, 0);
    }

    public void saveState(@NonNull Bundle bundle, boolean original) {
        Long id = getId();
        if (null != id) {
            bundle.putLong(STATE_KEY_ID, getId());
        }
        id = termId;
        if (null != id) {
            bundle.putLong((original) ? STATE_KEY_ORIGINAL_TERM_ID : AbstractTermEntity.STATE_KEY_ID, id);
        }
        id = termId;
        if (null != id) {
            bundle.putLong((original) ? STATE_KEY_ORIGINAL_MENTOR_ID : AbstractMentorEntity.STATE_KEY_ID, id);
        }
        bundle.putString((original) ? STATE_KEY_ORIGINAL_NUMBER : STATE_KEY_NUMBER, number);
        bundle.putString((original) ? STATE_KEY_ORIGINAL_TITLE : STATE_KEY_TITLE, title);
        bundle.putString((original) ? STATE_KEY_ORIGINAL_STATUS : STATE_KEY_STATUS, status.name());
        LocalDate d = expectedStart;
        if (null != d) {
            bundle.putLong((original) ? STATE_KEY_ORIGINAL_EXPECTED_START : STATE_KEY_EXPECTED_START, d.toEpochDay());
        }
        d = actualStart;
        if (null != d) {
            bundle.putLong((original) ? STATE_KEY_ORIGINAL_ACTUAL_START : STATE_KEY_ACTUAL_START, d.toEpochDay());
        }
        d = expectedEnd;
        if (null != d) {
            bundle.putLong((original) ? STATE_KEY_ORIGINAL_EXPECTED_END : STATE_KEY_EXPECTED_END, d.toEpochDay());
        }
        d = actualEnd;
        if (null != d) {
            bundle.putLong((original) ? STATE_KEY_ORIGINAL_ACTUAL_END : STATE_KEY_ACTUAL_END, d.toEpochDay());
        }
        bundle.putInt((original) ? STATE_KEY_ORIGINAL_COMPETENCY_UNITS : STATE_KEY_COMPETENCY_UNITS, competencyUnits);
        bundle.putString((original) ? STATE_KEY_ORIGINAL_NOTES : STATE_KEY_NOTES, getNotes());
    }

    /**
     * Gets the value of the {@link TermEntity#COLNAME_ID primary key} for the {@link TermEntity term} associated with the course.
     *
     * @return The value of the {@link TermEntity#COLNAME_ID primary key} for the {@link TermEntity term} associated with the course.
     */
    @Nullable
    public Long getTermId() {
        return termId;
    }

    /**
     * Sets the {@link TermEntity#COLNAME_ID primary key} value for the {@link TermEntity term} to be associated with the course.
     *
     * @param termId The new {@link TermEntity#COLNAME_ID primary key} value for the {@link TermEntity term} to be associated with the course.
     */
    protected void setTermId(long termId) {
        this.termId = termId;
    }

    /**
     * Gets the value of the {@link MentorEntity#COLNAME_ID primary key} for the student's {@link MentorEntity course mentor} for the course.
     *
     * @return The value of the {@link MentorEntity#COLNAME_ID primary key} for the student's {@link MentorEntity course mentor} for the course or {@code null} if no mentor
     * has been associated with the course.
     */
    @Nullable
    public Long getMentorId() {
        return mentorId;
    }

    /**
     * Sets the {@link MentorEntity#COLNAME_ID primary key} value for the {@link MentorEntity course mentor} to be associated with the course.
     *
     * @param mentorId The new {@link MentorEntity#COLNAME_ID primary key} value for the {@link MentorEntity course mentor} to be associated with the course.
     */
    protected void setMentorId(@Nullable Long mentorId) {
        this.mentorId = mentorId;
    }

    /**
     * Gets the WGU-proprietary number/code for the course.
     *
     * @return The WGU-proprietary number/code that is used to refer to the course, which is always single-line, whitespace-normalized and trimmed..
     */
    @NonNull
    public String getNumber() {
        return number;
    }

    /**
     * Sets the WGU-proprietary number/code for the course.
     *
     * @param number The new WGU-proprietary number/code that will refer to the course.
     */
    public void setNumber(String number) {
        this.number = SINGLE_LINE_NORMALIZER.apply(title);
    }

    /**
     * Gets the title for the course.
     *
     * @return The course title, which is always single-line, whitespace-normalized and trimmed..
     */
    @NonNull
    public String getTitle() {
        return title;
    }

    /**
     * Sets the course title.
     *
     * @param title The new title for the course.
     */
    public void setTitle(String title) {
        this.title = SINGLE_LINE_NORMALIZER.apply(title);
    }

    /**
     * Gets the current or final status of the course.
     *
     * @return The current or final status of the course.
     */
    @NonNull
    public CourseStatus getStatus() {
        return status;
    }

    /**
     * Sets the course status.
     *
     * @param status The new course status value.
     */
    public void setStatus(CourseStatus status) {
        this.status = (null == status) ? CourseStatus.UNPLANNED : status;
    }

    /**
     * Gets the date that the user expects to start the course.
     *
     * @return The date that the user expects to start the course or {@code null} if the expected start date has not been determined.
     */
    @Nullable
    public LocalDate getExpectedStart() {
        return expectedStart;
    }

    /**
     * Sets the date that the user expects to start the course.
     *
     * @param expectedStart The date that the user expects to start the course or {@code null} if the expected start date has not been determined.
     */
    public void setExpectedStart(@Nullable LocalDate expectedStart) {
        this.expectedStart = expectedStart;
    }

    /**
     * Gets the date that the user actually started the course.
     *
     * @return The date that the user actually started the course or {@code null} if the course hasn't been started, yet.
     */
    @Nullable
    public LocalDate getActualStart() {
        return actualStart;
    }

    /**
     * Sets the date that the user actually started the course.
     *
     * @param actualStart The date that the user actually started the course or {@code null} if the course hasn't been started, yet.
     */
    public void setActualStart(@Nullable LocalDate actualStart) {
        this.actualStart = actualStart;
    }

    /**
     * Gets the date that the user expects to finish the course.
     *
     * @return The date that the user expects to finish the course or {@code null} if the expected finish date has not yet been determined.
     */
    @Nullable
    public LocalDate getExpectedEnd() {
        return expectedEnd;
    }

    /**
     * Sets the date that the user expects to finish the course.
     *
     * @param expectedEnd The date that the user expects to finish the course or {@code null} if the expected finish date has not yet been determined.
     */
    public void setExpectedEnd(@Nullable LocalDate expectedEnd) {
        this.expectedEnd = expectedEnd;
    }

    /**
     * Gets the date that the course was actually concluded.
     *
     * @return The date that the course was actually concluded or {@code null} if the course hasn't yet concluded.
     */
    @Nullable
    public LocalDate getActualEnd() {
        return actualEnd;
    }

    /**
     * Sets the date that the course was actually concluded.
     *
     * @param actualEnd The date that the course was actually concluded or {@code null} if the course hasn't yet concluded.
     */
    public void setActualEnd(@Nullable LocalDate actualEnd) {
        this.actualEnd = actualEnd;
    }

    /**
     * Gets the number of competency units attributed to the course.
     *
     * @return The number of competency units attributed to the course or {@code 0} if the competency units value is unknown.
     */
    public int getCompetencyUnits() {
        return competencyUnits;
    }

    /**
     * Sets the number of competency units attributed to the course.
     *
     * @param competencyUnits The number of competency units attributed to the course or {@code 0} if the competency units value is unknown.
     */
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
