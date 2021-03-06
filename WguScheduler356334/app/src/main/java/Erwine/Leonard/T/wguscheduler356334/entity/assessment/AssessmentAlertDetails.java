package Erwine.Leonard.T.wguscheduler356334.entity.assessment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.room.Ignore;
import androidx.room.Relation;

import Erwine.Leonard.T.wguscheduler356334.entity.IdIndexedEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.alert.AlertEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.alert.AlertLink;
import Erwine.Leonard.T.wguscheduler356334.util.ToStringBuilder;

public class AssessmentAlertDetails extends AssessmentAlert {

    @Relation(
            parentColumn = AlertLink.COLNAME_TARGET_ID,
            entityColumn = Assessment.COLNAME_ID
    )
    @NonNull
    private AssessmentEntity assessment;

    public AssessmentAlertDetails(@NonNull AssessmentAlertLink link, @NonNull AlertEntity alert, @NonNull AssessmentEntity assessment) {
        super(link, alert);
        this.assessment = assessment;
    }

    @Ignore
    public AssessmentAlertDetails(@NonNull AlertEntity alert, @NonNull AssessmentEntity assessment) {
        super(new AssessmentAlertLink(alert.getId(), assessment.getId()), alert);
        this.assessment = assessment;
    }

    @Ignore
    public AssessmentAlertDetails(@NonNull AssessmentAlertDetails source) {
        super(source);
        assessment = new AssessmentEntity(source.assessment);
    }

    @Ignore
    public AssessmentAlertDetails() {
        super();
        assessment = new AssessmentEntity();
    }

    @NonNull
    public AssessmentEntity getAssessment() {
        return assessment;
    }

    public void setAssessment(@NonNull AssessmentEntity assessment) {
        getLink().setTargetId(IdIndexedEntity.assertNotNewId(assessment.getId()));
        this.assessment = assessment;
    }

    @Override
    public synchronized void setLink(@NonNull AssessmentAlertLink link) {
        if (link.getTargetId() != assessment.getId()) {
            throw new IllegalArgumentException();
        }
        super.setLink(link);
    }

    public void saveState(Bundle outState, boolean isOriginal) {
        assessment.saveState(outState, isOriginal);
        getAlert().saveState(outState, isOriginal);
    }

    public void restoreState(Bundle state, boolean isOriginal) {
        assessment.restoreState(state, isOriginal);
        AlertEntity alert = getAlert();
        alert.restoreState(state, isOriginal);
        AssessmentAlertLink link = getLink();
        link.setAlertId(alert.getId());
        link.setTargetId(assessment.getId());
    }

    @Override
    public void appendPropertiesAsStrings(@NonNull ToStringBuilder sb) {
        sb.append("assessment", assessment);
        super.appendPropertiesAsStrings(sb);
    }
}
