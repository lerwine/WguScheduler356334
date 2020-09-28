package Erwine.Leonard.T.wguscheduler356334.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;

import Erwine.Leonard.T.wguscheduler356334.db.AppDb;
import Erwine.Leonard.T.wguscheduler356334.util.ToStringBuilder;

@Entity(tableName = AppDb.TABLE_NAME_COURSE_ALERTS, indices = {
        @Index(value = CourseAlertEntity.COLNAME_COURSE_ID, name = CourseAlertEntity.INDEX_COURSE)
})
public class CourseAlertEntity extends AbstractCourseAlertEntity<CourseAlertEntity> {

    /**
     * The name of the foreign key index for the {@link #COLNAME_COURSE_ID "courseId"} database column.
     */
    public static final String INDEX_COURSE = "IDX_GOAL_ALERT_COURSE";

    public CourseAlertEntity(long courseId, boolean endAlert, int leadTime, long id) {
        super(id, courseId, endAlert, leadTime);
    }

    @Ignore
    public CourseAlertEntity(long courseId, boolean endAlert, int leadTime) {
        super(null, courseId, endAlert, leadTime);
    }

    @Ignore
    protected CourseAlertEntity(AbstractCourseAlertEntity<?> source) {
        super(source);
    }

    @NonNull
    @Override
    public String toString() {
        return ToStringBuilder.toEscapedString(this, false);
    }
}
