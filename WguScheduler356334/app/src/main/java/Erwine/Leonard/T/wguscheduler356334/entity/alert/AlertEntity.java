package Erwine.Leonard.T.wguscheduler356334.entity.alert;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;

import java.time.LocalTime;
import java.util.Objects;

import Erwine.Leonard.T.wguscheduler356334.db.AppDb;
import Erwine.Leonard.T.wguscheduler356334.entity.AbstractEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.IdIndexedEntity;

@Entity(
        tableName = AppDb.TABLE_NAME_ALERTS,
        indices = {
                @Index(value = Alert.COLNAME_NOTIFICATION_ID, name = AlertEntity.INDEX_NOTIFICATION_ID, unique = true)
        }
)
public class AlertEntity extends AbstractEntity<AlertEntity> implements Alert {

    /**
     * The name of the unique index for the {@link #COLNAME_NOTIFICATION_ID "notificationId"} database column.
     */
    public static final String INDEX_NOTIFICATION_ID = "IDX_ASSESSMENT_NOTIFICATION_ID";

    public static final long MAX_VALUE_RELATIVE_DAYS = 365L;
    public static final long MIN_VALUE_RELATIVE_DAYS = -365L;

    private long timeSpec;
    @Nullable
    private Boolean subsequent;
    @Nullable
    private String customMessage;
    @ColumnInfo(name = COLNAME_NOTIFICATION_ID)
    private int notificationId;
    @ColumnInfo(name = COLNAME_ALERT_TIME)
    @Nullable
    private LocalTime alertTime;

    @Ignore
    protected AlertEntity(long id, long timeSpec, @Nullable Boolean subsequent, @Nullable String customMessage, int notificationId, @Nullable LocalTime alertTime) {
        super(id);
        this.timeSpec = timeSpec;
        this.subsequent = subsequent;
        String s = SINGLE_LINE_NORMALIZER.apply(customMessage);
        this.customMessage = (s.isEmpty()) ? null : customMessage;
        this.notificationId = notificationId;
        this.alertTime = alertTime;
    }

    public AlertEntity(long timeSpec, @Nullable Boolean subsequent, @Nullable String customMessage, int notificationId, @Nullable LocalTime alertTime, long id) {
        this(IdIndexedEntity.assertNotNewId(id), timeSpec, subsequent, customMessage, notificationId, alertTime);
    }

    @Ignore
    public AlertEntity(long timeSpec, @Nullable Boolean subsequent, @Nullable String customMessage, @Nullable LocalTime alertTime) {
        this(ID_NEW, timeSpec, subsequent, customMessage, 0, alertTime);
    }

    @Ignore
    public AlertEntity() {
        this(ID_NEW, 0, false, null, 0, null);
    }

    @Ignore
    public AlertEntity(AlertEntity alertEntity) {
        super(alertEntity.getId());
        this.timeSpec = alertEntity.timeSpec;
        this.subsequent = alertEntity.subsequent;
        this.customMessage = alertEntity.customMessage;
        this.notificationId = alertEntity.notificationId;
        this.alertTime = alertEntity.alertTime;
    }

    @Override
    public long getTimeSpec() {
        return timeSpec;
    }

    @Override
    public void setTimeSpec(long timeSpec) {
        this.timeSpec = timeSpec;
    }

    @Nullable
    @Override
    public Boolean isSubsequent() {
        return subsequent;
    }

    @Override
    public void setSubsequent(@Nullable Boolean subsequent) {
        this.subsequent = subsequent;
    }

    @Nullable
    @Override
    public String getCustomMessage() {
        return customMessage;
    }

    @Override
    public void setCustomMessage(@Nullable String customMessage) {
        String s = SINGLE_LINE_NORMALIZER.apply(customMessage);
        this.customMessage = (s.isEmpty()) ? null : customMessage;
    }

    @Override
    public int getNotificationId() {
        return notificationId;
    }

    @Override
    public void setNotificationId(int notificationId) {
        // Set one time
        if (this.notificationId != 0 && notificationId != this.notificationId) {
            throw new IllegalStateException();
        }
        this.notificationId = notificationId;
    }

    @Override
    @Nullable
    public LocalTime getAlertTime() {
        return alertTime;
    }

    @Override
    public void setAlertTime(@Nullable LocalTime alertTime) {
        this.alertTime = alertTime;
    }

    @Override
    protected boolean equalsEntity(@NonNull AlertEntity other) {
        return timeSpec == other.timeSpec && notificationId == other.notificationId && Objects.equals(subsequent, other.subsequent) && Objects.equals(customMessage, other.customMessage) && Objects.equals(alertTime, other.alertTime);
    }

    @Override
    protected int hashCodeFromProperties() {
        return Objects.hash(timeSpec, subsequent, customMessage, notificationId, alertTime);
    }

    @NonNull
    @Override
    public String dbTableName() {
        return AppDb.TABLE_NAME_ALERTS;
    }

}
