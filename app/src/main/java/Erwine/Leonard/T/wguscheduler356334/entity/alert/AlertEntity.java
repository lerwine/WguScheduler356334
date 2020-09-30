package Erwine.Leonard.T.wguscheduler356334.entity.alert;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.Ignore;

import java.util.Objects;

import Erwine.Leonard.T.wguscheduler356334.db.AppDb;
import Erwine.Leonard.T.wguscheduler356334.entity.AbstractEntity;

@Entity(tableName = AppDb.TABLE_NAME_ALERTS)
public class AlertEntity extends AbstractEntity<AlertEntity> implements Alert {
    private long timeSpec;
    private Boolean subsequent;
    private String customMessage;

    @Ignore
    protected AlertEntity(Long id, long timeSpec, Boolean subsequent, String customMessage) {
        super(id);
        this.timeSpec = timeSpec;
        this.subsequent = subsequent;
        String s = SINGLE_LINE_NORMALIZER.apply(customMessage);
        this.customMessage = (s.isEmpty()) ? null : customMessage;
    }

    public AlertEntity(long timeSpec, Boolean subsequent, String customMessage, long id) {
        this(id, timeSpec, subsequent, customMessage);
    }

    @Ignore
    public AlertEntity(long timeSpec, Boolean subsequent, String customMessage) {
        this(null, timeSpec, subsequent, customMessage);
    }

    @Ignore
    public AlertEntity() {
        this(null, 0, false, null);
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
    public void setSubsequent(Boolean subsequent) {
        this.subsequent = subsequent;
    }

    @Nullable
    @Override
    public String getCustomMessage() {
        return customMessage;
    }

    @Override
    public void setCustomMessage(String customMessage) {
        String s = SINGLE_LINE_NORMALIZER.apply(customMessage);
        this.customMessage = (s.isEmpty()) ? null : customMessage;
    }

    @Override
    protected boolean equalsEntity(@NonNull AlertEntity other) {
        return timeSpec == other.timeSpec && Objects.equals(subsequent, other.subsequent) && Objects.equals(customMessage, other.customMessage);
    }

    @Override
    protected int hashCodeFromProperties() {
        return Objects.hash(timeSpec, subsequent, customMessage);
    }

    @Override
    public String dbTableName() {
        return AppDb.TABLE_NAME_ALERTS;
    }
}
