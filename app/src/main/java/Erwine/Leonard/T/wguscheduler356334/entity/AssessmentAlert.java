package Erwine.Leonard.T.wguscheduler356334.entity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import Erwine.Leonard.T.wguscheduler356334.db.AppDb;
import Erwine.Leonard.T.wguscheduler356334.util.ToStringBuilder;

public interface AssessmentAlert extends IdIndexedEntity {
    /**
     * The name of the {@code "assessmentId"} database column, which is the value of the {@link AssessmentEntity#COLNAME_ID primary key} for the {@link AssessmentEntity assessment}
     * associated with the alert.
     */
    String COLNAME_ASSESSMENT_ID = "assessmentId";

    /**
     * The name of the {@code "goalAlert"} database column.
     */
    String COLNAME_GOAL_ALERT = "goalAlert";

    /**
     * The name of the {@code "leadTime"} database column.
     */
    String COLNAME_LEAD_TIME = "leadTime";

    /**
     * Gets the value of the {@link AssessmentEntity#COLNAME_ID primary key} for the {@link AssessmentEntity assessment} associated with the assessment.
     *
     * @return The value of the {@link AssessmentEntity#COLNAME_ID primary key} for the {@link AssessmentEntity assessment} associated with the assessment.
     */
    @Nullable
    Long getAssessmentId();

    /**
     * Sets the {@link AssessmentEntity#COLNAME_ID primary key} value for the {@link AssessmentEntity assessment} to be associated with the assessment.
     *
     * @param assessmentId The {@link AssessmentEntity#COLNAME_ID primary key} value of the {@link AssessmentEntity assessment} to be associated with the assessment.
     */
    void setAssessmentId(long assessmentId);

    boolean isGoalAlert();

    void setGoalAlert(boolean isGoalAlert);

    /**
     * Gets alert lead time in days.
     *
     * @return Number of days before date assessment to show alert.
     */
    int getLeadTime();

    void setLeadTime(int days);

    @Override
    default String dbTableName() {
        return AppDb.TABLE_NAME_ASSESSMENT_ALERTS;
    }

    @Override
    default void restoreState(@NonNull Bundle bundle, boolean isOriginal) {
        IdIndexedEntity.super.restoreState(bundle, isOriginal);
        String key = IdIndexedEntity.stateKey(AppDb.TABLE_NAME_ASSESSMENTS, Course.COLNAME_ID, isOriginal);
        if (bundle.containsKey(key)) {
            setAssessmentId(bundle.getLong(key));
        }
        setLeadTime(bundle.getInt(COLNAME_LEAD_TIME, 0));
    }

    @Override
    default void saveState(@NonNull Bundle bundle, boolean isOriginal) {
        IdIndexedEntity.super.saveState(bundle, isOriginal);
        Long id;
        try {
            id = getAssessmentId();
        } catch (NullPointerException ex) {
            id = null;
        }
        if (null != id) {
            bundle.putLong(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_ASSESSMENTS, Course.COLNAME_ID, isOriginal), id);
        }
        bundle.putInt(COLNAME_LEAD_TIME, getLeadTime());
    }

    @Override
    default void appendPropertiesAsStrings(ToStringBuilder sb) {
        IdIndexedEntity.super.appendPropertiesAsStrings(sb);
        sb.append(COLNAME_ASSESSMENT_ID, getAssessmentId())
                .append(COLNAME_LEAD_TIME, getLeadTime());
    }

}
