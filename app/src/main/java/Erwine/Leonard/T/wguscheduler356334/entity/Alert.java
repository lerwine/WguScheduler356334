package Erwine.Leonard.T.wguscheduler356334.entity;

import android.os.Bundle;

import androidx.annotation.NonNull;

import Erwine.Leonard.T.wguscheduler356334.util.ToStringBuilder;

public interface Alert extends IdIndexedEntity {

    /**
     * The name of the {@code "leadTime"} database column.
     */
    String COLNAME_LEAD_TIME = "leadTime";

    /**
     * The name of the {@code "subsequent"} database column.
     */
    String COLNAME_SUBSEQUENT = "subsequent";

    /**
     * Gets alert lead time in days.
     *
     * @return Number of days before target date to show alert.
     */
    int getLeadTime();

    void setLeadTime(int days);

    /**
     * Gets the value that indicates whether the alert is for the course end date.
     *
     * @return {@code true} if this is an alert for the course end date; otherwise, it is for the start date.
     */
    boolean isSubsequent();

    void setSubsequent(boolean subsequent);

    @Override
    default void restoreState(@NonNull Bundle bundle, boolean isOriginal) {
        IdIndexedEntity.super.restoreState(bundle, isOriginal);
        setSubsequent(bundle.getBoolean(COLNAME_SUBSEQUENT, false));
        setLeadTime(bundle.getInt(COLNAME_LEAD_TIME, 0));
    }

    @Override
    default void saveState(@NonNull Bundle bundle, boolean isOriginal) {
        IdIndexedEntity.super.saveState(bundle, isOriginal);
        bundle.putBoolean(COLNAME_SUBSEQUENT, isSubsequent());
        bundle.putInt(COLNAME_LEAD_TIME, getLeadTime());
    }

    @Override
    default void appendPropertiesAsStrings(ToStringBuilder sb) {
        IdIndexedEntity.super.appendPropertiesAsStrings(sb);
        sb.append(COLNAME_SUBSEQUENT, isSubsequent()).append(COLNAME_LEAD_TIME, getLeadTime());
    }

}
