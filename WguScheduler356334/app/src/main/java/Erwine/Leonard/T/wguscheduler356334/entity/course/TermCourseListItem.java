package Erwine.Leonard.T.wguscheduler356334.entity.course;

import android.os.Bundle;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.DatabaseView;

import java.time.LocalDate;

import Erwine.Leonard.T.wguscheduler356334.db.AppDb;
import Erwine.Leonard.T.wguscheduler356334.entity.IdIndexedEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.mentor.Mentor;
import Erwine.Leonard.T.wguscheduler356334.entity.mentor.MentorEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.term.TermEntity;
import Erwine.Leonard.T.wguscheduler356334.util.ComparisonHelper;
import Erwine.Leonard.T.wguscheduler356334.util.ToStringBuilder;

@DatabaseView(
        viewName = AppDb.VIEW_NAME_TERM_COURSE_LIST,
        value = "SELECT courses.*, CASE WHEN courses.actualStart IS NOT NULL THEN courses.actualStart ELSE courses.expectedStart END AS [effectiveStart], " +
                "CASE WHEN courses.actualEnd IS NOT NULL THEN courses.actualEnd ELSE courses.expectedEnd END AS [effectiveEnd], " +
                "COUNT(assessments.id) AS [assessmentCount], mentors.name as [mentorName], mentors.phoneNumber, mentors.emailAddress " +
                "FROM courses LEFT JOIN mentors ON courses.mentorId = mentors.id\n" +
                "LEFT JOIN assessments ON courses.id = assessments.courseId\n" +
                "GROUP BY courses.id ORDER BY [actualStart], [expectedStart], [actualEnd], [expectedEnd]"
)
public final class TermCourseListItem extends AbstractCourseEntity<TermCourseListItem> implements Comparable<TermCourseListItem> {

    public static final String COLNAME_COURSE_EFFECTIVE_START = "effectiveStart";

    public static final String COLNAME_COURSE_EFFECTIVE_END = "effectiveEnd";

    /**
     * The name of the {@link #assessmentCount "assessmentCount"} view column, which contains the number of assessments for the course.
     */
    public static final String COLNAME_ASSESSMENT_COUNT = "assessmentCount";

    /**
     * The name of the {@link #mentorName "mentorName"} view column, which contains the name of the course mentor.
     */
    public static final String COLNAME_MENTOR_NAME = "mentorName";

    /**
     * The name of the {@link #phoneNumber "phoneNumber"} view column, which contains the phone number for the course mentor.
     */
    public static final String COLNAME_PHONE_NUMBER = "phoneNumber";

    /**
     * The name of the {@link #emailAddress "emailAddress"} view column, which contains the email address for the course mentor.
     */
    public static final String COLNAME_EMAIL_ADDRESS = "emailAddress";

    @ColumnInfo(name = COLNAME_COURSE_EFFECTIVE_START)
    private LocalDate effectiveStart;
    @ColumnInfo(name = COLNAME_COURSE_EFFECTIVE_END)
    private LocalDate effectiveEnd;
    @ColumnInfo(name = COLNAME_ASSESSMENT_COUNT)
    private int assessmentCount;
    @ColumnInfo(name = COLNAME_MENTOR_NAME)
    private String mentorName;
    @ColumnInfo(name = COLNAME_PHONE_NUMBER)
    private String phoneNumber;
    @ColumnInfo(name = COLNAME_EMAIL_ADDRESS)
    private String emailAddress;

    /**
     * Initializes a new {@code TermCourseListItem} object to represent an existing row of data in the {@link AppDb#TABLE_NAME_COURSES "courses"} database table.
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
    public TermCourseListItem(String number, String title, CourseStatus status, LocalDate expectedStart, LocalDate actualStart, LocalDate expectedEnd, LocalDate actualEnd,
                              int competencyUnits, String notes, long termId, Long mentorId, LocalDate effectiveStart, LocalDate effectiveEnd, Integer assessmentCount,
                              String mentorName, String phoneNumber, String emailAddress, long id) {
        super(IdIndexedEntity.assertNotNewId(id), termId, mentorId, number, title, status, expectedStart, actualStart, expectedEnd, actualEnd, competencyUnits, notes);
        this.effectiveStart = (null == actualStart) ? expectedStart : actualStart;
        if (null == this.effectiveStart && null != effectiveStart) {
            this.effectiveStart = effectiveStart;
        }
        this.effectiveEnd = (null == actualEnd) ? expectedEnd : actualEnd;
        if (null == this.effectiveEnd && null != effectiveEnd) {
            this.effectiveEnd = effectiveEnd;
        }
        this.assessmentCount = (null == assessmentCount || assessmentCount < 0) ? 0 : assessmentCount;
        this.mentorName = SINGLE_LINE_NORMALIZER.apply(mentorName);
        this.phoneNumber = SINGLE_LINE_NORMALIZER.apply(phoneNumber);
        this.emailAddress = SINGLE_LINE_NORMALIZER.apply(emailAddress);
    }

    @Override
    public synchronized void setExpectedStart(@Nullable LocalDate expectedStart) {
        super.setExpectedStart(expectedStart);
        LocalDate actualStart = getActualEnd();
        effectiveStart = (null == actualStart) ? expectedStart : actualStart;
    }

    @Override
    public synchronized void setActualStart(@Nullable LocalDate actualStart) {
        super.setActualStart(actualStart);
        effectiveStart = (null == actualStart) ? getExpectedStart() : actualStart;
    }

    @Override
    public synchronized void setExpectedEnd(@Nullable LocalDate expectedEnd) {
        super.setExpectedEnd(expectedEnd);
        LocalDate actualEnd = getActualEnd();
        effectiveEnd = (null == actualEnd) ? expectedEnd : actualEnd;
    }

    @Override
    public synchronized void setActualEnd(@Nullable LocalDate actualEnd) {
        super.setActualEnd(actualEnd);
        effectiveEnd = (null == actualEnd) ? getExpectedEnd() : actualEnd;
    }

    @Nullable
    public LocalDate getEffectiveStart() {
        return effectiveStart;
    }

    public synchronized void setEffectiveStart(LocalDate effectiveStart) {
        if (null != this.effectiveStart && !this.effectiveStart.equals(effectiveStart)) {
            throw new IllegalStateException();
        }
        this.effectiveStart = effectiveStart;
    }

    @Nullable
    public LocalDate getEffectiveEnd() {
        return effectiveEnd;
    }

    public synchronized void setEffectiveEnd(LocalDate effectiveEnd) {
        if (null != this.effectiveEnd && !this.effectiveEnd.equals(effectiveEnd)) {
            throw new IllegalStateException();
        }
        this.effectiveEnd = effectiveEnd;
    }

    public long getAssessmentCount() {
        return assessmentCount;
    }

    public void setAssessmentCount(int assessmentCount) {
        this.assessmentCount = Math.max(assessmentCount, 0);
    }

    public String getMentorName() {
        return mentorName;
    }

    public void setMentorName(String mentorName) {
        this.mentorName = SINGLE_LINE_NORMALIZER.apply(mentorName);
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = SINGLE_LINE_NORMALIZER.apply(phoneNumber);
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = SINGLE_LINE_NORMALIZER.apply(emailAddress);
    }

    @Override
    public void restoreState(@NonNull Bundle bundle, boolean isOriginal) {
        super.restoreState(bundle, isOriginal);
        setAssessmentCount(bundle.getInt(stateKey(COLNAME_ASSESSMENT_COUNT, isOriginal), 0));
        setMentorName(bundle.getString(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_MENTORS, Mentor.COLNAME_NAME, isOriginal), ""));
        setPhoneNumber(bundle.getString(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_MENTORS, Mentor.COLNAME_PHONE_NUMBER, isOriginal), ""));
        setEmailAddress(bundle.getString(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_MENTORS, Mentor.COLNAME_EMAIL_ADDRESS, isOriginal), ""));
    }

    @Override
    public void saveState(@NonNull Bundle bundle, boolean isOriginal) {
        super.saveState(bundle, isOriginal);
        bundle.putInt(stateKey(COLNAME_ASSESSMENT_COUNT, isOriginal), assessmentCount);
        bundle.putString(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_MENTORS, Mentor.COLNAME_NAME, isOriginal), mentorName);
        bundle.putString(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_MENTORS, Mentor.COLNAME_PHONE_NUMBER, isOriginal), phoneNumber);
        bundle.putString(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_MENTORS, Mentor.COLNAME_EMAIL_ADDRESS, isOriginal), emailAddress);
    }

    @Override
    public synchronized int compareTo(TermCourseListItem o) {
        if (this == o) return 0;
        if (o == null) return -1;
        Pair<LocalDate, LocalDate> thisRange0, thisRange1;
        if (null != getActualStart() || null != getActualEnd()) {
            thisRange0 = new Pair<>(getActualStart(), getActualEnd());
            thisRange1 = (null != getExpectedStart() || null != getActualEnd()) ? new Pair<>(getExpectedStart(), getExpectedEnd()) : null;
        } else {
            if (null == getExpectedStart() && null == getExpectedEnd()) {
                thisRange0 = null;
            } else {
                thisRange0 = new Pair<>(getExpectedStart(), getExpectedEnd());
            }
            thisRange1 = null;
        }
        Pair<LocalDate, LocalDate> thatRange0 = new Pair<>(o.getActualStart(), o.getActualEnd());
        Pair<LocalDate, LocalDate> thatRange1 = new Pair<>(o.getExpectedStart(), o.getExpectedEnd());
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
            if ((result = ComparisonHelper.compareRanges(thisRange0.first, thisRange0.second, thatRange0.first, thatRange0.second)) != 0) {
                return result;
            }
            if (null != thisRange1) {
                if (null == thatRange1) {
                    thatRange1 = thatRange0;
                }
                if ((result = ComparisonHelper.compareRanges(thisRange1.first, thisRange1.second, thatRange1.first, thatRange1.second)) != 0) {
                    return result;
                }
            } else if (null != thatRange1 && (result = ComparisonHelper.compareRanges(thisRange0.first, thisRange0.second, thatRange1.first, thatRange1.second)) != 0) {
                return result;
            }
        }
        if ((result = getStatus().compareTo(o.getStatus())) != 0 || (result = getNumber().compareTo(o.getNumber())) != 0 || (result = getTitle().compareTo(o.getTitle())) != 0) {
            return result;
        }
        return Long.compare(getId(), o.getId());
    }

    @NonNull
    @Override
    public String toString() {
        return ToStringBuilder.toEscapedString(this, false);
    }

    @Override
    public void appendPropertiesAsStrings(@NonNull ToStringBuilder sb) {
        super.appendPropertiesAsStrings(sb);
        sb.append("assessmentCount", assessmentCount).append("mentorName", mentorName).append("phoneNumber", phoneNumber).append("emailAddress", emailAddress);
    }

}
