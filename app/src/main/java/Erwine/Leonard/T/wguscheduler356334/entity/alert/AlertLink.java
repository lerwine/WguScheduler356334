package Erwine.Leonard.T.wguscheduler356334.entity.alert;

import android.os.Bundle;

import androidx.annotation.NonNull;

import Erwine.Leonard.T.wguscheduler356334.entity.IdIndexedEntity;
import Erwine.Leonard.T.wguscheduler356334.util.ToStringBuildable;
import Erwine.Leonard.T.wguscheduler356334.util.ToStringBuilder;

public interface AlertLink extends ToStringBuildable {

    /**
     * The name of the {@code "alertId"} database column.
     */
    String COLNAME_ALERT_ID = "alertId";

    /**
     * The name of the {@code "targetId"} database column.
     */
    String COLNAME_TARGET_ID = "targetId";

    long getAlertId();

    void setAlertId(long alertId);

    long getTargetId();

    void setTargetId(long targetId);

    String dbTableName();

    default void restoreState(@NonNull Bundle bundle, boolean isOriginal) {
        String n = dbTableName();
        String key = IdIndexedEntity.stateKey(n, COLNAME_ALERT_ID, isOriginal);
        if (bundle.containsKey(key)) {
            setAlertId(bundle.getLong(key));
        }
        key = IdIndexedEntity.stateKey(n, COLNAME_TARGET_ID, isOriginal);
        if (bundle.containsKey(key)) {
            setTargetId(bundle.getLong(key));
        }
    }

    default void saveState(@NonNull Bundle bundle, boolean isOriginal) {
        String n = dbTableName();
        bundle.putLong(IdIndexedEntity.stateKey(n, COLNAME_ALERT_ID, isOriginal), getAlertId());
        bundle.putLong(IdIndexedEntity.stateKey(n, COLNAME_TARGET_ID, isOriginal), getTargetId());
    }

    default void appendPropertiesAsStrings(ToStringBuilder sb) {
        sb.append(COLNAME_ALERT_ID, getAlertId())
                .append(COLNAME_TARGET_ID, getTargetId());
    }

}
