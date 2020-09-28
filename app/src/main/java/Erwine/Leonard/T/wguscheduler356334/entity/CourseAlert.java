package Erwine.Leonard.T.wguscheduler356334.entity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import Erwine.Leonard.T.wguscheduler356334.db.AppDb;
import Erwine.Leonard.T.wguscheduler356334.util.ToStringBuilder;

public interface CourseAlert extends IdIndexedEntity {
    /**
     * The name of the {@code "courseId"} database column, which is the value of the {@link CourseEntity#COLNAME_ID primary key} for the {@link CourseEntity course}
     * associated with the alert.
     */
    String COLNAME_COURSE_ID = "courseId";

    /**
     * The name of the {@code "endAlert"} database column.
     */
    String COLNAME_END_ALERT = "endAlert";

    /**
     * The name of the {@code "leadTime"} database column.
     */
    String COLNAME_LEAD_TIME = "leadTime";

    /**
     * Gets the value of the {@link CourseEntity#COLNAME_ID primary key} for the {@link CourseEntity course} associated with the assessment.
     *
     * @return The value of the {@link CourseEntity#COLNAME_ID primary key} for the {@link CourseEntity course} associated with the assessment.
     */
    @Nullable
    Long getCourseId();

    /**
     * Sets the {@link CourseEntity#COLNAME_ID primary key} value for the {@link CourseEntity course} to be associated with the assessment.
     *
     * @param courseId The {@link CourseEntity#COLNAME_ID primary key} value of the {@link CourseEntity course} to be associated with the assessment.
     */
    void setCourseId(long courseId);

    /**
     * Gets the value that indicates whether the alert is for the course end date.
     *
     * @return {@code true} if this is an alert for the course end date; otherwise, it is for the start date.
     */
    boolean isEndAlert();

    void setEndAlert(boolean isEndAlert);

    int getLeadTime();

    void setLeadTime(int days);

    @Override
    default String dbTableName() {
        return AppDb.TABLE_NAME_COURSE_ALERTS;
    }

    @Override
    default void restoreState(@NonNull Bundle bundle, boolean isOriginal) {
        IdIndexedEntity.super.restoreState(bundle, isOriginal);
        String key = IdIndexedEntity.stateKey(AppDb.TABLE_NAME_COURSES, Course.COLNAME_ID, isOriginal);
        if (bundle.containsKey(key)) {
            setCourseId(bundle.getLong(key));
        }
        setEndAlert(bundle.getBoolean(COLNAME_END_ALERT, false));
        setLeadTime(bundle.getInt(COLNAME_LEAD_TIME, 0));
    }

    @Override
    default void saveState(@NonNull Bundle bundle, boolean isOriginal) {
        IdIndexedEntity.super.saveState(bundle, isOriginal);
        Long id;
        try {
            id = getCourseId();
        } catch (NullPointerException ex) {
            id = null;
        }
        if (null != id) {
            bundle.putLong(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_COURSES, Course.COLNAME_ID, isOriginal), id);
        }
        bundle.putBoolean(COLNAME_END_ALERT, isEndAlert());
        bundle.putInt(COLNAME_LEAD_TIME, getLeadTime());
    }

    @Override
    default void appendPropertiesAsStrings(ToStringBuilder sb) {
        IdIndexedEntity.super.appendPropertiesAsStrings(sb);
        sb.append(COLNAME_COURSE_ID, getCourseId())
                .append(COLNAME_END_ALERT, isEndAlert())
                .append(COLNAME_LEAD_TIME, getLeadTime());
    }

}
