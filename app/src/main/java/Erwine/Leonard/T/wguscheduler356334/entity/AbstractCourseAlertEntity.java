package Erwine.Leonard.T.wguscheduler356334.entity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.ForeignKey;
import androidx.room.Ignore;

import java.util.Objects;

import Erwine.Leonard.T.wguscheduler356334.util.ToStringBuilder;

public abstract class AbstractCourseAlertEntity<T extends AbstractCourseAlertEntity<T>> extends AbstractAlertEntity<T> implements CourseAlert {

    @ForeignKey(entity = CourseEntity.class, parentColumns = {CourseEntity.COLNAME_ID}, childColumns = {COLNAME_COURSE_ID}, onDelete = ForeignKey.CASCADE, deferred = true)
    @ColumnInfo(name = COLNAME_COURSE_ID)
    private Long courseId;

    @Ignore
    protected AbstractCourseAlertEntity(Long id, Long courseId, boolean subsequent, int leadTime) {
        super(id, subsequent, leadTime);
        this.courseId = courseId;
    }

    @Ignore
    protected AbstractCourseAlertEntity(AbstractCourseAlertEntity<?> source) {
        super(source);
        this.courseId = source.courseId;
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
    protected boolean equalsEntity(@NonNull T other) {
        return Objects.equals(courseId, other.getCourseId()) && super.equalsEntity(other);
    }

    @Override
    protected int hashCodeFromProperties() {
        return Objects.hash(courseId, isSubsequent(), getLeadTime());
    }

    @Override
    public void appendPropertiesAsStrings(ToStringBuilder sb) {
        super.appendPropertiesAsStrings(sb);
        sb.append(COLNAME_COURSE_ID, getCourseId());
    }

}
