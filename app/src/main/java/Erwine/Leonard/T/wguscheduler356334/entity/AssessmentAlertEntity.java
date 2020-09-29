package Erwine.Leonard.T.wguscheduler356334.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;

import Erwine.Leonard.T.wguscheduler356334.db.AppDb;
import Erwine.Leonard.T.wguscheduler356334.util.ToStringBuilder;

@Entity(tableName = AppDb.TABLE_NAME_ASSESSMENT_ALERTS, indices = {
        @Index(value = AssessmentAlertEntity.COLNAME_ASSESSMENT_ID, name = AssessmentAlertEntity.INDEX_ASSESSMENT)
})
public class AssessmentAlertEntity extends AbstractAssessmentAlertEntity<AssessmentAlertEntity> {

    /**
     * The name of the foreign key index for the {@link #COLNAME_ASSESSMENT_ID "assessmentId"} database column.
     */
    public static final String INDEX_ASSESSMENT = "IDX_GOAL_ALERT_ASSESSMENT";

    public AssessmentAlertEntity(long assessmentId, boolean subsequent, int leadTime, long id) {
        super(id, assessmentId, subsequent, leadTime);
    }

    @Ignore
    public AssessmentAlertEntity(long assessmentId, boolean subsequent, int leadTime) {
        super(null, assessmentId, subsequent, leadTime);
    }

    @Ignore
    public AssessmentAlertEntity(AbstractAssessmentAlertEntity<?> source) {
        super(source);
    }

    @NonNull
    @Override
    public String toString() {
        return ToStringBuilder.toEscapedString(this, false);
    }
}
