package Erwine.Leonard.T.wguscheduler356334.entity.alert;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.time.LocalDate;
import java.time.LocalTime;

import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.db.LocalDateConverter;
import Erwine.Leonard.T.wguscheduler356334.db.LocalTimeConverter;
import Erwine.Leonard.T.wguscheduler356334.entity.IdIndexedEntity;
import Erwine.Leonard.T.wguscheduler356334.util.ToStringBuilder;
import Erwine.Leonard.T.wguscheduler356334.util.validation.ResourceMessageBuilder;

public interface Alert extends IdIndexedEntity {

    /**
     * The name of the {@code "timeSpec"} database column.
     */
    String COLNAME_TIME_SPEC = "timeSpec";

    /**
     * The name of the {@code "subsequent"} database column.
     */
    String COLNAME_SUBSEQUENT = "subsequent";

    /**
     * The name of the {@code "customMessage"} database column.
     */
    String COLNAME_CUSTOM_MESSAGE = "customMessage";

    /**
     * The name of the {@code "notificationId"} database column.
     */
    String COLNAME_NOTIFICATION_ID = "notificationId";

    /**
     * The name of the {@code "alertTime"} database column.
     */
    String COLNAME_ALERT_TIME = "alertTime";

    static void validate(@NonNull ResourceMessageBuilder builder, @NonNull AlertEntity entity) {
        Boolean subsequent = entity.isSubsequent();
        if (null != subsequent) {
            long timeSpec = entity.getTimeSpec();
            if (timeSpec < AlertEntity.MIN_VALUE_RELATIVE_DAYS || timeSpec > AlertEntity.MAX_VALUE_RELATIVE_DAYS) {
                builder.acceptError(R.string.message_relative_days_out_of_range);
            }
        }
    }

    /**
     * Gets alert time value.
     *
     * @return The number of days relative to target date or the explicit date.
     */
    long getTimeSpec();

    void setTimeSpec(long days);

    /**
     * Gets the value that indicates whether the alert time is relative to the course/assessment end or start date, or if it is an explicit date.
     *
     * @return {@code null} if {@link #getTimeSpec()} refers to an explicit date; {@code true} if {@link #getTimeSpec()} is relative to the course/assessment end date;
     * otherwise, {@link #getTimeSpec()} is relative to the course/assessment start date.
     */
    @Nullable
    Boolean isSubsequent();

    void setSubsequent(@Nullable Boolean subsequent);

    /**
     * Gets the message to display with the alert.
     *
     * @return The message to display with the alert or {@code null} if there is no custom message;
     */
    @Nullable
    String getCustomMessage();

    void setCustomMessage(@Nullable String customMessage);

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

    @Nullable
    LocalTime getAlertTime();

    void setAlertTime(@Nullable LocalTime alertTime);

    @Override
    default void restoreState(@NonNull Bundle bundle, boolean isOriginal) {
        IdIndexedEntity.super.restoreState(bundle, isOriginal);
        String key = stateKey(COLNAME_SUBSEQUENT, isOriginal);
        if (bundle.containsKey(key)) {
            setSubsequent(bundle.getBoolean(key, false));
            setTimeSpec(bundle.getLong(stateKey(COLNAME_TIME_SPEC, isOriginal), 0L));
        } else {
            setSubsequent(null);
            setTimeSpec(bundle.getLong(stateKey(COLNAME_TIME_SPEC, isOriginal), LocalDateConverter.fromLocalDate(LocalDate.now())));
        }
        key = stateKey(COLNAME_CUSTOM_MESSAGE, isOriginal);
        if (bundle.containsKey(key)) {
            setCustomMessage(bundle.getString(key));
        } else {
            setCustomMessage(null);
        }
        setNotificationId(bundle.getInt(stateKey(COLNAME_NOTIFICATION_ID, isOriginal), 0));
        key = stateKey(COLNAME_ALERT_TIME, isOriginal);
        if (bundle.containsKey(key)) {
            setAlertTime(LocalTimeConverter.toLocalTime(bundle.getInt(key, 0)));
        } else {
            setAlertTime(null);
        }
    }

    @Override
    default void saveState(@NonNull Bundle bundle, boolean isOriginal) {
        IdIndexedEntity.super.saveState(bundle, isOriginal);
        Boolean subsequent = isSubsequent();
        if (null != subsequent) {
            bundle.putBoolean(stateKey(COLNAME_SUBSEQUENT, isOriginal), subsequent);
        }
        bundle.putLong(stateKey(COLNAME_TIME_SPEC, isOriginal), getTimeSpec());
        String message = getCustomMessage();
        if (null != message) {
            bundle.putString(stateKey(COLNAME_CUSTOM_MESSAGE, isOriginal), message);
        }
        bundle.putInt(stateKey(COLNAME_NOTIFICATION_ID, isOriginal), getNotificationId());
        Integer minutes = LocalTimeConverter.fromLocalTime(getAlertTime());
        if (null != minutes) {
            bundle.putInt(stateKey(COLNAME_ALERT_TIME, isOriginal), minutes);
        }
    }

    @Override
    default void appendPropertiesAsStrings(@NonNull ToStringBuilder sb) {
        IdIndexedEntity.super.appendPropertiesAsStrings(sb);
        sb.append(COLNAME_SUBSEQUENT, isSubsequent()).append(COLNAME_TIME_SPEC, getTimeSpec()).append(COLNAME_CUSTOM_MESSAGE, getCustomMessage()).append(COLNAME_NOTIFICATION_ID, getNotificationId())
                .append(COLNAME_ALERT_TIME, getAlertTime());
    }

}
