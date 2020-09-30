package Erwine.Leonard.T.wguscheduler356334.entity.mentor;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.DatabaseView;

import Erwine.Leonard.T.wguscheduler356334.db.AppDb;
import Erwine.Leonard.T.wguscheduler356334.util.ToStringBuilder;

@DatabaseView(
        viewName = AppDb.VIEW_NAME_MENTOR_LIST,
        value = "SELECT mentors.*, COUNT(courses.id) AS [courseCount]\n" +
                "FROM mentors LEFT JOIN courses ON mentors.id = courses.mentorId\n" +
                "GROUP BY mentors.id\n" +
                "ORDER BY [name];"
)
public class MentorListItem extends AbstractMentorEntity<MentorListItem> implements Comparable<MentorListItem> {

    /**
     * The name of the {@link #courseCount "courseCount"} database column, which contains the number of courses associated with course mentor.
     */
    public static final String COLNAME_COURSE_COUNT = "courseCount";

    @ColumnInfo(name = COLNAME_COURSE_COUNT)
    private int courseCount;

    /**
     * Initializes a new {@code MentorListItem} object to represent an existing row of data in the {@link AppDb#TABLE_NAME_MENTORS "mentors"} database table.
     *
     * @param name         the name of the course mentor.
     * @param notes        multi-line text containing notes about the course mentor.
     * @param phoneNumber  the course mentor's phone number.
     * @param emailAddress the course mentor's email address.
     * @param courseCount  the number of courses associated with course mentor.
     * @param id           The value of the {@link #COLNAME_ID primary key column}.
     */
    public MentorListItem(String name, String notes, String phoneNumber, String emailAddress, int courseCount, long id) {
        super(id, name, notes, phoneNumber, emailAddress);
        this.courseCount = Math.max(courseCount, 0);
    }

    public int getCourseCount() {
        return courseCount;
    }

    public void setCourseCount(int courseCount) {
        this.courseCount = Math.max(courseCount, 0);
    }

    @Override
    public void restoreState(@NonNull Bundle bundle, boolean isOriginal) {
        super.restoreState(bundle, isOriginal);
        setCourseCount(bundle.getInt(stateKey(COLNAME_COURSE_COUNT, isOriginal), 0));
    }

    @Override
    public void saveState(@NonNull Bundle bundle, boolean isOriginal) {
        super.saveState(bundle, isOriginal);
        bundle.putInt(stateKey(COLNAME_COURSE_COUNT, isOriginal), courseCount);
    }

    @Override
    public int compareTo(MentorListItem o) {
        if (this == o) return 0;
        if (o == null) return -1;
        int result = getName().compareTo(o.getName());
        if (result != 0 || (result = getPhoneNumber().compareTo(o.getPhoneNumber())) != 0 || (result = getEmailAddress().compareTo(o.getEmailAddress())) != 0) {
            return result;
        }
        if (null == getId()) {
            return (null == o.getId()) ? 0 : -1;
        }
        return (null == o.getId()) ? 1 : getId().compareTo(o.getId());
    }

    @NonNull
    @Override
    public String toString() {
        return ToStringBuilder.toEscapedString(this, false);
    }

    @Override
    public void appendPropertiesAsStrings(ToStringBuilder sb) {
        super.appendPropertiesAsStrings(sb);
        sb.append("courseCount", courseCount);
    }

}
