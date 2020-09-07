package Erwine.Leonard.T.wguscheduler356334.entity;

import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.time.LocalDate;
import java.util.Objects;
import java.util.function.Function;

import Erwine.Leonard.T.wguscheduler356334.db.AppDb;
import Erwine.Leonard.T.wguscheduler356334.util.StringHelper;
import Erwine.Leonard.T.wguscheduler356334.util.StringNormalizationOption;
import Erwine.Leonard.T.wguscheduler356334.util.Values;

@Entity(tableName = AppDb.TABLE_NAME_COURSES, indices = {
        @Index(value = CourseEntity.COLNAME_TERM_ID, name = CourseEntity.INDEX_TERM),
        @Index(value = CourseEntity.COLNAME_MENTOR_ID, name = CourseEntity.INDEX_MENTOR),
        @Index(value = {CourseEntity.COLNAME_NUMBER, CourseEntity.COLNAME_TERM_ID}, name = CourseEntity.INDEX_NUMBER, unique = true)
})
public class CourseEntity implements Comparable<CourseEntity> {

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
    private Long id;
    @ForeignKey(entity = TermEntity.class, parentColumns = {TermEntity.COLNAME_ID}, childColumns = {COLNAME_TERM_ID}, onDelete = ForeignKey.RESTRICT, deferred = true)
    @ColumnInfo(name = COLNAME_TERM_ID)
    private long termId;
    @ForeignKey(entity = MentorEntity.class, parentColumns = {MentorEntity.COLNAME_ID}, childColumns = {COLNAME_MENTOR_ID}, onDelete = ForeignKey.RESTRICT, deferred = true)
    @ColumnInfo(name = COLNAME_MENTOR_ID)
    private Long mentorId;

    public CourseEntity(String number, String title, CourseStatus status, LocalDate expectedStart, LocalDate actualStart,
                        LocalDate expectedEnd, LocalDate actualEnd, Integer competencyUnits, String notes, long termId, Long mentorId, long id) {
        this(number, title, status, expectedStart, actualStart, expectedEnd, actualEnd, competencyUnits, notes, termId, mentorId);
        this.id = id;
    }

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
    public CourseEntity(String number, String title, CourseStatus status, LocalDate expectedStart, LocalDate actualStart,
                        LocalDate expectedEnd, LocalDate actualEnd, Integer competencyUnits, String notes, long termId, Long mentorId) {
        this(number, title, status, expectedStart, actualStart, expectedEnd, actualEnd, competencyUnits, notes);
        this.termId = termId;
        this.mentorId = mentorId;
    }

    public static void applyInsertedId(CourseEntity source, long id) {
        if (null != source.getId()) {
            throw new IllegalStateException();
        }
        source.id = id;
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

    public Long getId() {
        return id;
    }

    public long getTermId() {
        return termId;
    }

    public void setTermId(long termId) {
        this.termId = termId;
    }

    public Long getMentorId() {
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

    public void setMentorId(Long mentorId) {
        this.mentorId = mentorId;
    }

    public int getCompetencyUnits() {
        return competencyUnits;
    }

    public void setCompetencyUnits(int competencyUnits) {
        this.competencyUnits = competencyUnits;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = MULTI_LINE_NORMALIZER.apply(notes);
    }

    @Override
    public synchronized boolean equals(Object o) {
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
    public synchronized int hashCode() {
        if (null != id) {
            return id.hashCode();
        }
        return Objects.hash(id, termId, mentorId, number, title, expectedStart, actualStart, expectedEnd, actualEnd, status, notes);
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    @Override
    public synchronized int compareTo(CourseEntity o) {
        if (this == o) return 0;
        if (o == null || getClass() != o.getClass()) return -1;
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
        Long i = that.id;
        if (null != i) {
            return (null == id) ? 1 : Long.compare(id, i);
        }
        if (null != id) {
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
}
