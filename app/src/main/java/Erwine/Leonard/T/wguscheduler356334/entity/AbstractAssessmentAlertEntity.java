package Erwine.Leonard.T.wguscheduler356334.entity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.ForeignKey;
import androidx.room.Ignore;

import java.util.Objects;

import Erwine.Leonard.T.wguscheduler356334.util.ToStringBuilder;

public abstract class AbstractAssessmentAlertEntity<T extends AbstractAssessmentAlertEntity<T>> extends AbstractAlertEntity<T> implements AssessmentAlert {

    @ForeignKey(entity = CourseEntity.class, parentColumns = {AssessmentEntity.COLNAME_ID}, childColumns = {COLNAME_ASSESSMENT_ID}, onDelete = ForeignKey.CASCADE, deferred = true)
    @ColumnInfo(name = COLNAME_ASSESSMENT_ID)
    private Long assessmentId;

    @Ignore
    protected AbstractAssessmentAlertEntity(Long id, Long assessmentId, boolean subsequent, int leadTime) {
        super(id, subsequent, leadTime);
        this.assessmentId = assessmentId;
    }

    @Ignore
    protected AbstractAssessmentAlertEntity(AbstractAssessmentAlertEntity<?> source) {
        super(source);
        this.assessmentId = source.assessmentId;
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
    protected boolean equalsEntity(@NonNull T other) {
        return Objects.equals(assessmentId, other.getAssessmentId()) && super.equalsEntity(other);
    }

    @Override
    protected int hashCodeFromProperties() {
        return Objects.hash(assessmentId, isSubsequent(), getLeadTime());
    }

    @Override
    public void appendPropertiesAsStrings(ToStringBuilder sb) {
        super.appendPropertiesAsStrings(sb);
        sb.append(COLNAME_ASSESSMENT_ID, getAssessmentId());
    }
}
