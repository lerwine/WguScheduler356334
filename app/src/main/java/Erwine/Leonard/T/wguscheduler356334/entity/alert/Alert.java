package Erwine.Leonard.T.wguscheduler356334.entity.alert;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.time.LocalDate;

import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.entity.IdIndexedEntity;
import Erwine.Leonard.T.wguscheduler356334.util.ToStringBuilder;
import Erwine.Leonard.T.wguscheduler356334.util.ValidationMessage;

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

    static void validate(@NonNull ValidationMessage.ResourceMessageBuilder builder, @NonNull AlertEntity entity) {
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

    @Override
    default void restoreState(@NonNull Bundle bundle, boolean isOriginal) {
        IdIndexedEntity.super.restoreState(bundle, isOriginal);
        if (bundle.containsKey(COLNAME_SUBSEQUENT)) {
            setSubsequent(bundle.getBoolean(COLNAME_SUBSEQUENT, false));
            setTimeSpec(bundle.getLong(COLNAME_TIME_SPEC, 0L));
        } else {
            setSubsequent(null);
            setTimeSpec(bundle.getLong(COLNAME_TIME_SPEC, LocalDate.now().toEpochDay()));
        }
        if (bundle.containsKey(COLNAME_CUSTOM_MESSAGE)) {
            setCustomMessage(bundle.getString(COLNAME_CUSTOM_MESSAGE));
        } else {
            setCustomMessage(null);
        }
    }

    @Override
    default void saveState(@NonNull Bundle bundle, boolean isOriginal) {
        IdIndexedEntity.super.saveState(bundle, isOriginal);
        Boolean subsequent = isSubsequent();
        if (null != subsequent) {
            bundle.putBoolean(COLNAME_SUBSEQUENT, subsequent);
        }
        bundle.putLong(COLNAME_TIME_SPEC, getTimeSpec());
        String message = getCustomMessage();
        if (null != message) {
            bundle.putString(COLNAME_CUSTOM_MESSAGE, message);
        }
    }

    @Override
    default void appendPropertiesAsStrings(@NonNull ToStringBuilder sb) {
        IdIndexedEntity.super.appendPropertiesAsStrings(sb);
        sb.append(COLNAME_SUBSEQUENT, isSubsequent()).append(COLNAME_TIME_SPEC, getTimeSpec()).append(COLNAME_CUSTOM_MESSAGE, getCustomMessage());
    }

}
