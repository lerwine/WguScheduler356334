package Erwine.Leonard.T.wguscheduler356334.entity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.time.LocalDate;

import Erwine.Leonard.T.wguscheduler356334.db.AppDb;
import Erwine.Leonard.T.wguscheduler356334.util.ToStringBuilder;

public interface Assessment extends NoteColumnIncludedEntity {
    /**
     * The name of the {@code "courseId"} database column, which is the value of the {@link CourseEntity#COLNAME_ID primary key} for the {@link CourseEntity course}
     * associated with the assessment.
     */
    String COLNAME_COURSE_ID = "courseId";
    /**
     * The name of the {@code "code"} database column, which contains the WGU-proprietary code that is used to refer to the assessment.
     */
    String COLNAME_CODE = "code";
    /**
     * The name of the {@code "status"} database column, which the current or final status value for the assessment.
     */
    String COLNAME_STATUS = "status";
    /**
     * The name of the {@code "goalDate"}  database column, which contains the pre-determined goal date for completing the assessment.
     */
    String COLNAME_GOAL_DATE = "goalDate";
    /**
     * The name of the  {@code "type"} database column, which indicates the assessment type.
     */
    String COLNAME_TYPE = "type";
    /**
     * The name of the {@code "completionDate"} database column, which contains the actual completion date for the assessment.
     */
    String COLNAME_COMPLETION_DATE = "completionDate";

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
     * Gets the WGU-proprietary code that is used to refer to the assessment.
     *
     * @return The WGU-proprietary code that is used to refer to the assessment.
     */
    @NonNull
    String getCode();

    /**
     * Sets the WGU-proprietary code that is used to refer to the assessment.
     *
     * @param code The new WGU-proprietary code that will to refer to the assessment.
     */
    void setCode(String code);

    /**
     * Gets the current or final status value for the assessment.
     *
     * @return The current or final status value for the assessment.
     */
    @NonNull
    AssessmentStatus getStatus();

    /**
     * Sets the status value for the assessment.
     *
     * @param status The new status value for the assessment.
     */
    void setStatus(AssessmentStatus status);

    /**
     * Gets the pre-determined goal date for completing the assessment.
     *
     * @return The pre-determined goal date for completing the assessment or {@code null} if no goal date has been established.
     */
    @Nullable
    LocalDate getGoalDate();

    /**
     * Sets the pre-determined goal date for completing the assessment.
     *
     * @param goalDate The new pre-determined goal date for completing the assessment or {@code null} if no goal date has been established.
     */
    void setGoalDate(LocalDate goalDate);

    /**
     * Gets the assessment type.
     *
     * @return The assessment type.
     */
    @NonNull
    AssessmentType getType();

    /**
     * Sets the assessment type.
     *
     * @param type The new assessment type.
     */
    void setType(AssessmentType type);

    /**
     * Gets the actual completion date for the assessment.
     *
     * @return The actual completion date for the assessment or {@code null} if the course has not yet been concluded.
     */
    @Nullable
    LocalDate getCompletionDate();

    /**
     * Sets the actual completion date for the assessment.
     *
     * @param completionDate The actual completion date for the assessment or {@code null} if the course has not yet been concluded.
     */
    void setCompletionDate(LocalDate completionDate);

    @Override
    default String dbTableName() {
        return AppDb.TABLE_NAME_ASSESSMENTS;
    }

    @Override
    default void restoreState(@NonNull Bundle bundle, boolean isOriginal) {
        NoteColumnIncludedEntity.super.restoreState(bundle, isOriginal);
        String key = IdIndexedEntity.stateKey(AppDb.TABLE_NAME_COURSES, Course.COLNAME_ID, isOriginal);
        if (bundle.containsKey(key)) {
            setCourseId(bundle.getLong(key));
        }
        setCode(bundle.getString(stateKey(COLNAME_CODE, isOriginal), ""));
        key = stateKey(COLNAME_STATUS, isOriginal);
        setStatus((bundle.containsKey(key)) ? AssessmentStatus.valueOf(bundle.getString(key)) : AssessmentStatus.NOT_STARTED);
        key = stateKey(COLNAME_GOAL_DATE, isOriginal);
        if (bundle.containsKey(key)) {
            setGoalDate(LocalDate.ofEpochDay(bundle.getLong(key)));
        } else {
            setGoalDate(null);
        }
        setType((bundle.containsKey(key)) ? AssessmentType.valueOf(bundle.getString(key)) : AssessmentType.OBJECTIVE_ASSESSMENT);
        key = stateKey(COLNAME_COMPLETION_DATE, isOriginal);
        if (bundle.containsKey(key)) {
            setCompletionDate(LocalDate.ofEpochDay(bundle.getLong(key)));
        } else {
            setCompletionDate(null);
        }
    }

    @Override
    default void saveState(@NonNull Bundle bundle, boolean isOriginal) {
        NoteColumnIncludedEntity.super.saveState(bundle, isOriginal);
        Long id;
        try {
            id = getCourseId();
        } catch (NullPointerException ex) {
            id = null;
        }
        if (null != id) {
            bundle.putLong(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_COURSES, Course.COLNAME_ID, isOriginal), id);
        }
        bundle.putString(stateKey(COLNAME_CODE, isOriginal), getCode());
        bundle.putString(stateKey(COLNAME_STATUS, isOriginal), getStatus().name());
        LocalDate d = getGoalDate();
        if (null != d) {
            bundle.putLong(stateKey(COLNAME_GOAL_DATE, isOriginal), d.toEpochDay());
        }
        bundle.putString(stateKey(COLNAME_TYPE, isOriginal), getType().name());
        d = getCompletionDate();
        if (null != d) {
            bundle.putLong(stateKey(COLNAME_COMPLETION_DATE, isOriginal), d.toEpochDay());
        }
    }

    @Override
    default void appendPropertiesAsStrings(ToStringBuilder sb) {
        NoteColumnIncludedEntity.super.appendPropertiesAsStrings(sb);
        sb.append(COLNAME_COURSE_ID, getCourseId())
                .append(COLNAME_CODE, getCode())
                .append(COLNAME_STATUS, getStatus())
                .append(COLNAME_GOAL_DATE, getGoalDate())
                .append(COLNAME_TYPE, getType())
                .append(COLNAME_COMPLETION_DATE, getCompletionDate())
                .append(COLNAME_NOTES, getNotes());
    }

}
