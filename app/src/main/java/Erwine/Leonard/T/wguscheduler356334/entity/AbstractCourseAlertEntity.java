package Erwine.Leonard.T.wguscheduler356334.entity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.ForeignKey;
import androidx.room.Ignore;

import java.util.Objects;

public abstract class AbstractCourseAlertEntity<T extends AbstractCourseAlertEntity<T>> extends AbstractEntity<T> implements CourseAlert {

    @ForeignKey(entity = CourseEntity.class, parentColumns = {CourseEntity.COLNAME_ID}, childColumns = {COLNAME_COURSE_ID}, onDelete = ForeignKey.CASCADE, deferred = true)
    @ColumnInfo(name = COLNAME_COURSE_ID)
    private Long courseId;
    @ColumnInfo(name = COLNAME_END_ALERT)
    private boolean endAlert;
    @ColumnInfo(name = COLNAME_LEAD_TIME)
    private int leadTime;

    @Ignore
    protected AbstractCourseAlertEntity(Long id, Long courseId, boolean endAlert, int leadTime) {
        super(id);
        this.courseId = courseId;
        this.endAlert = endAlert;
        this.leadTime = Math.max(leadTime, 0);
    }

    @Ignore
    protected AbstractCourseAlertEntity(AbstractCourseAlertEntity<?> source) {
        super(source.getId());
        this.courseId = source.courseId;
        this.endAlert = source.endAlert;
        this.leadTime = source.leadTime;
    }

    @Nullable
    @Override
    public Long getCourseId() {
        return courseId;
    }

    @Override
    public void setCourseId(long courseId) {
        this.courseId = courseId;
    }

    @Override
    public boolean isEndAlert() {
        return endAlert;
    }

    @Override
    public void setEndAlert(boolean isEndAlert) {
        endAlert = isEndAlert;
    }

    @Override
    public int getLeadTime() {
        return leadTime;
    }

    @Override
    public void setLeadTime(int days) {
        leadTime = Math.max(days, 0);
    }

    @Override
    protected boolean equalsEntity(@NonNull T other) {
        return Objects.equals(courseId, other.getCourseId()) &&
                endAlert == other.isEndAlert() &&
                leadTime == other.getLeadTime();
    }

    @Override
    protected int hashCodeFromProperties() {
        return Objects.hash(courseId, endAlert, leadTime);
    }

}
