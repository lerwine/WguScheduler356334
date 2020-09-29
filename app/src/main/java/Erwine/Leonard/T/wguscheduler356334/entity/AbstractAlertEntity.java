package Erwine.Leonard.T.wguscheduler356334.entity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Ignore;

public abstract class AbstractAlertEntity<T extends AbstractAlertEntity<T>> extends AbstractEntity<T> implements Alert {

    @ColumnInfo(name = COLNAME_SUBSEQUENT)
    private boolean subsequent;
    @ColumnInfo(name = COLNAME_LEAD_TIME)
    private int leadTime;

    @Ignore
    protected AbstractAlertEntity(Long id, boolean subsequent, int leadTime) {
        super(id);
        this.subsequent = subsequent;
        this.leadTime = Math.max(leadTime, 0);
    }

    @Ignore
    protected AbstractAlertEntity(AbstractAlertEntity<?> source) {
        super(source.getId());
        this.subsequent = source.subsequent;
        this.leadTime = source.leadTime;
    }

    @Override
    public boolean isSubsequent() {
        return subsequent;
    }

    @Override
    public void setSubsequent(boolean isEndAlert) {
        subsequent = isEndAlert;
    }

    @Override
    public int getLeadTime() {
        return leadTime;
    }

    @Override
    public void setLeadTime(int days) {
        leadTime = Math.max(days, 0);
    }

    @Override
    public void restoreState(@NonNull Bundle bundle, boolean isOriginal) {

    }

    @Override
    public void saveState(@NonNull Bundle bundle, boolean isOriginal) {

    }

    @Override
    protected boolean equalsEntity(@NonNull T other) {
        return subsequent == other.isSubsequent() && leadTime == other.getLeadTime();
    }

}
