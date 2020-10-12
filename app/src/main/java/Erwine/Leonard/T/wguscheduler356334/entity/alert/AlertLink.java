package Erwine.Leonard.T.wguscheduler356334.entity.alert;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;

import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.entity.IdIndexedEntity;
import Erwine.Leonard.T.wguscheduler356334.util.ToStringBuildable;
import Erwine.Leonard.T.wguscheduler356334.util.ToStringBuilder;
import Erwine.Leonard.T.wguscheduler356334.util.validation.ResourceMessageBuilder;

import static Erwine.Leonard.T.wguscheduler356334.entity.IdIndexedEntity.ID_NEW;

public interface AlertLink extends ToStringBuildable {

    /**
     * The name of the {@code "alertId"} database column.
     */
    String COLNAME_ALERT_ID = "alertId";

    /**
     * The name of the {@code "targetId"} database column.
     */
    String COLNAME_TARGET_ID = "targetId";

    /**
     * The name of the {@code "notificationId"} database column.
     */
    String COLNAME_NOTIFICATION_ID = "notificationId";

    static void validate(@NonNull ResourceMessageBuilder builder, @NonNull AlertLinkEntity<?> entity) {
        AlertLink link = entity.getLink();
        long id = link.getTargetId();
        if (id == ID_NEW) {
            builder.acceptError(R.string.message_alert_has_no_target);
        }
        AlertEntity alert = entity.getAlert();
        if (alert.getId() != id) {
            builder.acceptError(R.string.message_alert_mismatch);
        }
        Alert.validate(builder, alert);
    }

    long getAlertId();

    void setAlertId(long alertId);

    long getTargetId();

    void setTargetId(long targetId);

    /**
     * Gets the private request code for broadcast {@link android.app.PendingIntent PendingIntents}. This value must be unique.
     *
     * @return The private request code for broadcast {@link android.app.PendingIntent PendingIntents}. This value must be unique.
     */
    int getNotificationId();

    /**
     * Gets the private request code for broadcast {@link android.app.PendingIntent PendingIntents}.
     *
     * @param notificationId The new private request code for broadcast {@link android.app.PendingIntent PendingIntents}.
     */
    void setNotificationId(int notificationId);

    @NonNull
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

    default void appendPropertiesAsStrings(@NonNull ToStringBuilder sb) {
        sb.append(COLNAME_ALERT_ID, getAlertId())
                .append(COLNAME_TARGET_ID, getTargetId());
    }

    default <T extends BroadcastReceiver> PendingIntent createAlertIntent(Context packageContext, Class<T> cls) {
        Intent intent = new Intent(packageContext, cls);
        intent.putExtra(COLNAME_ALERT_ID, getAlertId());
        intent.putExtra(COLNAME_TARGET_ID, getTargetId());
        return PendingIntent.getBroadcast(packageContext, getNotificationId(), intent, 0);
    }

    default <T extends BroadcastReceiver> PendingIntent getAlertIntent(Context packageContext, Class<T> cls) {
        Intent intent = new Intent(packageContext, cls);
        intent.putExtra(COLNAME_ALERT_ID, getAlertId());
        intent.putExtra(COLNAME_TARGET_ID, getTargetId());
        return PendingIntent.getBroadcast(packageContext, getNotificationId(), intent, PendingIntent.FLAG_NO_CREATE);
    }

}
