package Erwine.Leonard.T.wguscheduler356334.entity.course;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;

import Erwine.Leonard.T.wguscheduler356334.db.AppDb;
import Erwine.Leonard.T.wguscheduler356334.entity.IdIndexedEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.alert.AlertEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.alert.AlertLink;
import Erwine.Leonard.T.wguscheduler356334.util.ToStringBuilder;

import static Erwine.Leonard.T.wguscheduler356334.entity.IdIndexedEntity.ID_NEW;

@Entity(
        tableName = AppDb.TABLE_NAME_COURSE_ALERTS,
        primaryKeys = {AlertLink.COLNAME_ALERT_ID, AlertLink.COLNAME_TARGET_ID},
        indices = {
                @Index(value = AlertLink.COLNAME_ALERT_ID, name = CourseAlertLink.INDEX_ALERT, unique = true),
                @Index(value = AlertLink.COLNAME_NOTIFICATION_ID, name = CourseAlertLink.INDEX_NOTIFICATION_ID, unique = true),
                @Index(value = AlertLink.COLNAME_TARGET_ID, name = CourseAlertLink.INDEX_LINK)
        }
)
public class CourseAlertLink implements AlertLink {

    /**
     * The name of the foreign key index for the {@link #COLNAME_ALERT_ID "alertId"} database column.
     */
    public static final String INDEX_ALERT = "IDX_COURSE_ALERT";

    /**
     * The name of the foreign key index for the {@link #COLNAME_TARGET_ID "targetId"} database column.
     */
    public static final String INDEX_LINK = "IDX_COURSE_LINK";

    /**
     * The name of the unique index for the {@link #COLNAME_NOTIFICATION_ID "notificationId"} database column.
     */
    public static final String INDEX_NOTIFICATION_ID = "IDX_COURSE_NOTIFICATION_ID";

    @ForeignKey(entity = AlertEntity.class, parentColumns = {AlertEntity.COLNAME_ID}, childColumns = {COLNAME_ALERT_ID}, onDelete = ForeignKey.CASCADE, deferred = true)
    @ColumnInfo(name = COLNAME_ALERT_ID)
    private long alertId;
    @ForeignKey(entity = CourseEntity.class, parentColumns = {CourseEntity.COLNAME_ID}, childColumns = {COLNAME_TARGET_ID}, onDelete = ForeignKey.CASCADE, deferred = true)
    @ColumnInfo(name = COLNAME_TARGET_ID)
    private long targetId;
    @ColumnInfo(name = COLNAME_NOTIFICATION_ID)
    private int notificationId;

    public CourseAlertLink(long alertId, long targetId, int notificationId) {
        this.alertId = IdIndexedEntity.assertNotNewId(alertId);
        this.targetId = IdIndexedEntity.assertNotNewId(targetId);
        this.notificationId = notificationId;
    }

    @Ignore
    public CourseAlertLink(long alertId, long targetId) {
        this(alertId, targetId, 0);
    }

    @Ignore
    public CourseAlertLink(@NonNull CourseAlertLink source) {
        alertId = source.alertId;
        targetId = source.targetId;
        notificationId = source.notificationId;
    }

    @Ignore
    public CourseAlertLink() {
        this.alertId = ID_NEW;
        this.targetId = ID_NEW;
    }

    @Override
    public long getAlertId() {
        return alertId;
    }

    @Override
    public void setAlertId(long alertId) {
        this.alertId = alertId;
    }

    @Override
    public long getTargetId() {
        return targetId;
    }

    @Override
    public void setTargetId(long targetId) {
        this.targetId = targetId;
    }

    public void setAlertIdAndRun(long id, Runnable runnable) {
        alertId = id;
        try {
            runnable.run();
            id = alertId;
        } finally {
            alertId = id;
        }
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

    @NonNull
    @Override
    public String dbTableName() {
        return AppDb.TABLE_NAME_COURSE_ALERTS;
    }

    @NonNull
    @Override
    public String toString() {
        return ToStringBuilder.toEscapedString(this, false);
    }
}
