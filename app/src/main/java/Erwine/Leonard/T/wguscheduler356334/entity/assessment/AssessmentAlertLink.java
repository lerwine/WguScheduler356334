package Erwine.Leonard.T.wguscheduler356334.entity.assessment;

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
        tableName = AppDb.TABLE_NAME_ASSESSMENT_ALERTS,
        primaryKeys = {AlertLink.COLNAME_ALERT_ID, AlertLink.COLNAME_TARGET_ID},
        indices = {@Index(value = AlertLink.COLNAME_ALERT_ID, name = AssessmentAlertLink.INDEX_ALERT, unique = true)}
)
public class AssessmentAlertLink implements AlertLink {

    /**
     * The name of the foreign key index for the {@link #COLNAME_ALERT_ID "alertId"} database column.
     */
    public static final String INDEX_ALERT = "IDX_ASSESSMENT_ALERT";

    @ForeignKey(entity = AlertEntity.class, parentColumns = {AlertEntity.COLNAME_ID}, childColumns = {COLNAME_ALERT_ID}, onDelete = ForeignKey.CASCADE, deferred = true)
    @ColumnInfo(name = COLNAME_ALERT_ID)
    private long alertId;
    @ForeignKey(entity = AssessmentEntity.class, parentColumns = {AssessmentEntity.COLNAME_ID}, childColumns = {COLNAME_TARGET_ID}, onDelete = ForeignKey.CASCADE, deferred = true)
    @ColumnInfo(name = COLNAME_TARGET_ID)
    private long targetId;

    public AssessmentAlertLink(long alertId, long targetId) {
        this.alertId = IdIndexedEntity.assertNotNewId(alertId);
        this.targetId = IdIndexedEntity.assertNotNewId(targetId);
    }

    @Ignore
    public AssessmentAlertLink() {
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

    @NonNull
    @Override
    public String dbTableName() {
        return AppDb.TABLE_NAME_ASSESSMENT_ALERTS;
    }

    @NonNull
    @Override
    public String toString() {
        return ToStringBuilder.toEscapedString(this, false);
    }
}
