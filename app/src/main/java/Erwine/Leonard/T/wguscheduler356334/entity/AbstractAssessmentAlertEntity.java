package Erwine.Leonard.T.wguscheduler356334.entity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.ForeignKey;
import androidx.room.Ignore;

import java.util.Objects;

public abstract class AbstractAssessmentAlertEntity<T extends AbstractAssessmentAlertEntity<T>> extends AbstractEntity<T> implements AssessmentAlert {

    @ForeignKey(entity = CourseEntity.class, parentColumns = {AssessmentEntity.COLNAME_ID}, childColumns = {COLNAME_ASSESSMENT_ID}, onDelete = ForeignKey.CASCADE, deferred = true)
    @ColumnInfo(name = COLNAME_ASSESSMENT_ID)
    private Long assessmentId;
    @ColumnInfo(name = COLNAME_GOAL_ALERT)
    private boolean goalAlert;
    @ColumnInfo(name = COLNAME_LEAD_TIME)
    private int leadTime;

    @Ignore
    protected AbstractAssessmentAlertEntity(Long id, Long assessmentId, boolean goalAlert, int leadTime) {
        super(id);
        this.assessmentId = assessmentId;
        this.goalAlert = goalAlert;
        this.leadTime = Math.max(leadTime, 0);
    }

    @Ignore
    protected AbstractAssessmentAlertEntity(AbstractAssessmentAlertEntity<?> source) {
        super(source.getId());
        this.assessmentId = source.assessmentId;
        this.goalAlert = source.goalAlert;
        this.leadTime = source.leadTime;
    }

    @Nullable
    @Override
    public Long getAssessmentId() {
        return assessmentId;
    }

    @Override
    public void setAssessmentId(long assessmentId) {
        this.assessmentId = assessmentId;
    }

    @Override
    public boolean isGoalAlert() {
        return goalAlert;
    }

    @Override
    public void setGoalAlert(boolean goalAlert) {
        this.goalAlert = goalAlert;
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
    protected boolean equalsEntity(@NonNull T other) {
        return Objects.equals(assessmentId, other.getAssessmentId()) &&
                goalAlert == other.isGoalAlert() && leadTime == other.getLeadTime();
    }

    @Override
    protected int hashCodeFromProperties() {
        return Objects.hash(assessmentId, goalAlert, leadTime);
    }

}
