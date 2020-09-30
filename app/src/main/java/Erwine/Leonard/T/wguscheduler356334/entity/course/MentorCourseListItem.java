package Erwine.Leonard.T.wguscheduler356334.entity.course;

import android.os.Bundle;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.DatabaseView;

import java.time.LocalDate;

import Erwine.Leonard.T.wguscheduler356334.db.AppDb;
import Erwine.Leonard.T.wguscheduler356334.entity.IdIndexedEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.assessment.AssessmentEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.mentor.MentorEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.term.Term;
import Erwine.Leonard.T.wguscheduler356334.entity.term.TermEntity;
import Erwine.Leonard.T.wguscheduler356334.util.ComparisonHelper;
import Erwine.Leonard.T.wguscheduler356334.util.ToStringBuilder;

@DatabaseView(
        viewName = AppDb.VIEW_NAME_MENTOR_COURSE_LIST,
        value = "SELECT courses.*, COUNT(assessments.id) AS [assessmentCount], terms.name as [termName]\n" +
                "FROM courses LEFT JOIN terms ON courses.termId = terms.id\n" +
                "LEFT JOIN assessments ON courses.id = assessments.courseId\n" +
                "GROUP BY courses.id ORDER BY [actualStart], [expectedStart], [actualEnd], [expectedEnd]"
)
public class MentorCourseListItem extends AbstractCourseEntity<MentorCourseListItem> implements Comparable<MentorCourseListItem> {

    /**
     * The name of the {@link #assessmentCount "assessmentCount"} view column, which contains the number of assessments for the course.
     */
    public static final String COLNAME_ASSESSMENT_COUNT = "assessmentCount";

    /**
     * The name of the {@link #termName "termName"} view column, which contains the name of the term.
     */
    public static final String COLNAME_TERM_NAME = "termName";

    @ColumnInfo(name = COLNAME_ASSESSMENT_COUNT)
    private int assessmentCount;
    @ColumnInfo(name = COLNAME_TERM_NAME)
    private String termName;

    /**
     * Initializes a new {@code MentorCourseListItem} object to represent an existing row of data in the {@link AppDb#TABLE_NAME_COURSES "courses"} database table.
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
     * @param assessmentCount The number of {@link AssessmentEntity assessments}.
     * @param termName        The name of the associated {@link TermEntity}.
     * @param id              The value of the {@link #COLNAME_ID primary key column}.
     */
    public MentorCourseListItem(String number, String title, CourseStatus status, LocalDate expectedStart, LocalDate actualStart, LocalDate expectedEnd, LocalDate actualEnd,
                                int competencyUnits, String notes, long termId, Long mentorId, Integer assessmentCount, String termName, long id) {
        super(id, termId, mentorId, number, title, status, expectedStart, actualStart, expectedEnd, actualEnd, competencyUnits, notes);
        this.assessmentCount = (null == assessmentCount || assessmentCount < 0) ? 0 : assessmentCount;
        this.termName = SINGLE_LINE_NORMALIZER.apply(termName);
    }

    public long getAssessmentCount() {
        return assessmentCount;
    }

    public void setAssessmentCount(int assessmentCount) {
        this.assessmentCount = Math.max(assessmentCount, 0);
    }

    public String getTermName() {
        return termName;
    }

    public void setTermName(String termName) {
        this.termName = SINGLE_LINE_NORMALIZER.apply(termName);
    }

    @Override
    public void restoreState(@NonNull Bundle bundle, boolean isOriginal) {
        super.restoreState(bundle, isOriginal);
        setAssessmentCount(bundle.getInt(stateKey(COLNAME_ASSESSMENT_COUNT, isOriginal), 0));
        setTermName(bundle.getString(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_TERMS, Term.COLNAME_NAME, isOriginal), ""));
    }

    @Override
    public void saveState(@NonNull Bundle bundle, boolean isOriginal) {
        super.saveState(bundle, isOriginal);
        bundle.putInt(stateKey(COLNAME_ASSESSMENT_COUNT, isOriginal), assessmentCount);
        bundle.putString(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_TERMS, Term.COLNAME_NAME, isOriginal), termName);
    }

    @Override
    public synchronized int compareTo(MentorCourseListItem o) {
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
        if ((result = getStatus().compareTo(o.getStatus())) != 0) {
            return result;
        }
        Long i = o.getId();
        if (null != i) {
            return (null == getId()) ? 1 : Long.compare(getId(), i);
        }
        if (null != getId()) {
            return -1;
        }
        if ((result = getNumber().compareTo(o.getNumber())) != 0) {
            return result;
        }
        return getTitle().compareTo(o.getTitle());
    }

    @NonNull
    @Override
    public String toString() {
        return ToStringBuilder.toEscapedString(this, false);
    }

    @Override
    public void appendPropertiesAsStrings(ToStringBuilder sb) {
        super.appendPropertiesAsStrings(sb);
        sb.append("assessmentCount", assessmentCount).append("termName", termName);
    }

}
