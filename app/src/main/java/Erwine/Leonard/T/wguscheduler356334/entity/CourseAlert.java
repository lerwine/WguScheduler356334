package Erwine.Leonard.T.wguscheduler356334.entity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import Erwine.Leonard.T.wguscheduler356334.db.AppDb;
import Erwine.Leonard.T.wguscheduler356334.util.ToStringBuilder;

public interface CourseAlert extends Alert {
    /**
     * The name of the {@code "courseId"} database column, which is the value of the {@link CourseEntity#COLNAME_ID primary key} for the {@link CourseEntity course}
     * associated with the alert.
     */
    String COLNAME_COURSE_ID = "courseId";

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

    @Override
    default String dbTableName() {
        return AppDb.TABLE_NAME_COURSE_ALERTS;
    }

    @Override
    default void restoreState(@NonNull Bundle bundle, boolean isOriginal) {
        Alert.super.restoreState(bundle, isOriginal);
        String key = IdIndexedEntity.stateKey(AppDb.TABLE_NAME_COURSES, Course.COLNAME_ID, isOriginal);
        if (bundle.containsKey(key)) {
            setCourseId(bundle.getLong(key));
        }
    }

    @Override
    default void saveState(@NonNull Bundle bundle, boolean isOriginal) {
        Alert.super.saveState(bundle, isOriginal);
        Long id;
        try {
            id = getCourseId();
        } catch (NullPointerException ex) {
            id = null;
        }
        if (null != id) {
            bundle.putLong(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_COURSES, Course.COLNAME_ID, isOriginal), id);
        }
    }

    @Override
    default void appendPropertiesAsStrings(ToStringBuilder sb) {
        Alert.super.appendPropertiesAsStrings(sb);
        sb.append(COLNAME_COURSE_ID, getCourseId());
    }

}
