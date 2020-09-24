package Erwine.Leonard.T.wguscheduler356334.entity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.DatabaseView;

import java.time.LocalDate;

import Erwine.Leonard.T.wguscheduler356334.db.AppDb;
import Erwine.Leonard.T.wguscheduler356334.util.ComparisonHelper;
import Erwine.Leonard.T.wguscheduler356334.util.ToStringBuilder;

@DatabaseView(
        viewName = "termListView",
        value = "SELECT terms.*, SUM(courses.competencyUnits) AS [totalCompetencyUnits], COUNT(courses.id) AS [courseCount]\n" +
                "FROM terms LEFT JOIN courses ON terms.id = courses.termId\n" +
                "GROUP BY terms.id ORDER BY [start], [end]"
)
public final class TermListItem extends AbstractTermEntity<TermListItem> implements Comparable<TermListItem> {

    /**
     * The name of the {@link #courseCount "courseCount"} database column, which contains the number of courses for the term.
     */
    public static final String COLNAME_COURSE_COUNT = "courseCount";

    /**
     * The name of the {@link #totalCompetencyUnits "totalCompetencyUnits"} database column, which contains the sum of the {@link CourseEntity#getCompetencyUnits() competency units}
     * for courses in term.
     */
    public static final String COLNAME_TOTAL_COMPETENCY_UNITS = "totalCompetencyUnits";

    @ColumnInfo(name = COLNAME_COURSE_COUNT)
    private int courseCount;
    @ColumnInfo(name = COLNAME_TOTAL_COMPETENCY_UNITS)
    private int totalCompetencyUnits;

    /**
     * Initializes a new {@code TermListItem} object to represent an existing row of data in the {@link AppDb#TABLE_NAME_TERMS "terms"} database table.
     *
     * @param name                 The name of the term.
     * @param start                The inclusive start date of the term, which can be {@code null} if no start date has been determined.
     * @param end                  The inclusive end date of the term, which can be {@code null} if no end date has been determined.
     * @param notes                User notes to be associated with the term.
     * @param courseCount          The number of courses for the term.
     * @param totalCompetencyUnits The sum of the {@link CourseEntity#getCompetencyUnits() competency units} for courses in term.
     * @param id                   The value of the {@link #COLNAME_ID primary key column}.
     */
    public TermListItem(String name, LocalDate start, LocalDate end, String notes, int courseCount, Integer totalCompetencyUnits, long id) {
        super(id, name, start, end, notes);
        this.courseCount = Math.max(courseCount, 0);
        this.totalCompetencyUnits = (null == totalCompetencyUnits) ? 0 : totalCompetencyUnits;
    }

    public int getCourseCount() {
        return courseCount;
    }

    public void setCourseCount(int courseCount) {
        this.courseCount = Math.max(courseCount, 0);
    }

    public int getTotalCompetencyUnits() {
        return totalCompetencyUnits;
    }

    public void setTotalCompetencyUnits(int totalCompetencyUnits) {
        this.totalCompetencyUnits = Math.max(totalCompetencyUnits, 0);
    }

    @Override
    public void restoreState(@NonNull Bundle bundle, boolean isOriginal) {
        super.restoreState(bundle, isOriginal);
        setCourseCount(bundle.getInt(stateKey(COLNAME_COURSE_COUNT, isOriginal), 0));
        setTotalCompetencyUnits(bundle.getInt(stateKey(COLNAME_TOTAL_COMPETENCY_UNITS, isOriginal), 0));
    }

    @Override
    public void saveState(@NonNull Bundle bundle, boolean isOriginal) {
        super.saveState(bundle, isOriginal);
        bundle.putInt(stateKey(COLNAME_COURSE_COUNT, isOriginal), courseCount);
        bundle.putInt(stateKey(COLNAME_TOTAL_COMPETENCY_UNITS, isOriginal), totalCompetencyUnits);
    }

    @Override
    public synchronized int compareTo(TermListItem o) {
        if (this == o) return 0;
        if (null == o) return -1;
        LocalDate date = o.getStart();
        int result = ComparisonHelper.compareRanges(getStart(), getEnd(), o.getStart(), o.getEnd());
        if (result != 0) {
            return result;
        }
        Long i = o.getId();
        if (null == i) {
            return (null == getId()) ? getName().compareTo(o.getName()) : -1;
        }
        return (null == getId()) ? 1 : Long.compare(getId(), i);
    }

    @NonNull
    @Override
    public String toString() {
        return ToStringBuilder.toEscapedString(this, false);
    }

    @Override
    public void appendPropertiesAsStrings(ToStringBuilder sb) {
        super.appendPropertiesAsStrings(sb);
        sb.append("courseCount", courseCount).append("totalCompetencyUnits", totalCompetencyUnits);
    }

}
