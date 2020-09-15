package Erwine.Leonard.T.wguscheduler356334.entity;

import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;

import java.time.LocalDate;
import java.util.Objects;

import Erwine.Leonard.T.wguscheduler356334.db.AppDb;
import Erwine.Leonard.T.wguscheduler356334.util.Values;

/**
 * Represents a row of data from the {@link AppDb#TABLE_NAME_COURSES "courses"} database table.
 */
@Entity(tableName = AppDb.TABLE_NAME_COURSES, indices = {
        @Index(value = CourseEntity.COLNAME_TERM_ID, name = CourseEntity.INDEX_TERM),
        @Index(value = CourseEntity.COLNAME_MENTOR_ID, name = CourseEntity.INDEX_MENTOR),
        @Index(value = {CourseEntity.COLNAME_NUMBER, CourseEntity.COLNAME_TERM_ID}, name = CourseEntity.INDEX_NUMBER, unique = true)
})
public final class CourseEntity extends AbstractNotedEntity<CourseEntity> implements Comparable<CourseEntity> {

    /**
     * The name of the foreign key index for the {@link #termId "termId"} database column.
     */
    public static final String INDEX_TERM = "IDX_COURSE_TERM";
    /**
     * The name of the foreign key index for the {@link #mentorId "mentorId"} database column.
     */
    public static final String INDEX_MENTOR = "IDX_COURSE_MENTOR";
    /**
     * The name of the unique index for the {@link #number "number"} and {@link #termId "termId"} database columns.
     */
    public static final String INDEX_NUMBER = "IDX_COURSE_NUMBER";
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

    @ForeignKey(entity = TermEntity.class, parentColumns = {TermEntity.COLNAME_ID}, childColumns = {COLNAME_TERM_ID}, onDelete = ForeignKey.CASCADE, deferred = true)
    @ColumnInfo(name = COLNAME_TERM_ID)
    private long termId;

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

    @Ignore
    private CourseEntity(Long id, Long termId, Long mentorId, String number, String title, CourseStatus status, LocalDate expectedStart, LocalDate actualStart,
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
        this(id, termId, mentorId, number, title, status, expectedStart, actualStart, expectedEnd, actualEnd, competencyUnits, notes);
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
        this(null, termId, mentorId, number, title, status, expectedStart, actualStart, expectedEnd, actualEnd, competencyUnits, notes);
    }

    /**
     * Initializes a new {@code CourseEntity} object to represent a new row of data for the {@link AppDb#TABLE_NAME_COURSES "courses"} database table.
     *
     * @param termId The value of the {@link TermEntity#COLNAME_ID primary key} for the {@link TermEntity term} associated with the course.
     */
    @Ignore
    public CourseEntity(long termId) {
        this(null, termId, null, null, null, CourseStatus.UNPLANNED, null, null, null, null, 0, null);
    }

    /**
     * Initializes a new {@code CourseEntity} object with empty values to represent a new row of data for the {@link AppDb#TABLE_NAME_COURSES "courses"} database table.
     */
    @Ignore
    public CourseEntity() {
        this(null, null, null, null, null, null, null, null, null, null, 0, null);
    }

    /**
     * Gets the value of the {@link TermEntity#COLNAME_ID primary key} for the {@link TermEntity term} associated with the course.
     *
     * @return The value of the {@link TermEntity#COLNAME_ID primary key} for the {@link TermEntity term} associated with the course.
     */
    public long getTermId() {
        return termId;
    }

    /**
     * Sets the {@link TermEntity#COLNAME_ID primary key} value for the {@link TermEntity term} to be associated with the course.
     *
     * @param termId The new {@link TermEntity#COLNAME_ID primary key} value for the {@link TermEntity term} to be associated with the course.
     */
    public void setTermId(long termId) {
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
    public void setMentorId(Long mentorId) {
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
    public void setExpectedStart(LocalDate expectedStart) {
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
    public void setActualStart(LocalDate actualStart) {
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
    public void setExpectedEnd(LocalDate expectedEnd) {
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
    public void setActualEnd(LocalDate actualEnd) {
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
    protected boolean equalsEntity(@NonNull CourseEntity other) {
        return termId == other.termId &&
                Objects.equals(mentorId, other.mentorId) &&
                number.equals(other.number) &&
                title.equals(other.title) &&
                Objects.equals(expectedStart, other.expectedStart) &&
                Objects.equals(actualStart, other.actualStart) &&
                Objects.equals(expectedEnd, other.expectedEnd) &&
                Objects.equals(actualEnd, other.actualEnd) &&
                status == other.status &&
                getNotes().equals(other.getNotes());
    }

    @Override
    protected int hashCodeFromProperties() {
        return Objects.hash(termId, mentorId, number, title, expectedStart, actualStart, expectedEnd, actualEnd, status, getNotes());
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    @Override
    public synchronized int compareTo(CourseEntity o) {
        if (this == o) return 0;
        if (o == null) return -1;
        CourseEntity that = (CourseEntity) o;
        Pair<LocalDate, LocalDate> thisRange0, thisRange1;
        if (null != actualStart || null != actualEnd) {
            thisRange0 = new Pair<>(actualStart, actualEnd);
            thisRange1 = (null != expectedStart || null != actualEnd) ? new Pair<>(expectedStart, expectedEnd) : null;
        } else {
            if (null == expectedStart && null == expectedEnd) {
                thisRange0 = null;
            } else {
                thisRange0 = new Pair<>(expectedStart, expectedEnd);
            }
            thisRange1 = null;
        }
        Pair<LocalDate, LocalDate> thatRange0 = new Pair<>(that.actualStart, that.actualEnd);
        Pair<LocalDate, LocalDate> thatRange1 = new Pair<>(that.expectedStart, that.expectedStart);
        if (null == thatRange1.first && null == thatRange1.second)
            thatRange1 = null;
        if (null == thatRange0.first && null == thatRange0.second) {
            thatRange0 = thatRange1;
            thatRange1 = null;
        }
        int result;
        if (null == thisRange0) {
            if (null != thatRange0) {
                return 1;
            }
        } else {
            if (null == thatRange0) {
                return -1;
            }
            if ((result = Values.compareDateRanges(thisRange0.first, thisRange0.second, thatRange0.first, thatRange0.second)) != 0) {
                return result;
            }
            if (null != thisRange1) {
                if (null == thatRange1) {
                    thatRange1 = thatRange0;
                }
                if ((result = Values.compareDateRanges(thisRange1.first, thisRange1.second, thatRange1.first, thatRange1.second)) != 0) {
                    return result;
                }
            } else if (null != thatRange1 && (result = Values.compareDateRanges(thisRange0.first, thisRange0.second, thatRange1.first, thatRange1.second)) != 0) {
                return result;
            }
        }
        if ((result = status.compareTo(that.status)) != 0) {
            return result;
        }
        Long i = that.getId();
        if (null != i) {
            return (null == getId()) ? 1 : Long.compare(getId(), i);
        }
        if (null != getId()) {
            return -1;
        }
        if ((result = number.compareTo(that.number)) != 0) {
            return result;
        }
        return title.compareTo(that.title);
    }

    @NonNull
    @Override
    public String toString() {
        return "CourseEntity{" +
                "id=" + getId() +
                ", termId=" + termId +
                ", mentorId=" + mentorId +
                ", number='" + number + '\'' +
                ", title='" + title + '\'' +
                ", expectedStart=" + expectedStart +
                ", actualStart=" + actualStart +
                ", expectedEnd=" + expectedEnd +
                ", actualEnd=" + actualEnd +
                ", status=" + status +
                ", notes='" + getNotes() + '\'' +
                '}';
    }
}
