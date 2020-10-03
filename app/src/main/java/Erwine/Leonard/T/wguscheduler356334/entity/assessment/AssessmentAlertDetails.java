package Erwine.Leonard.T.wguscheduler356334.entity.assessment;

import androidx.annotation.NonNull;
import androidx.room.Relation;

import Erwine.Leonard.T.wguscheduler356334.entity.IdIndexedEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.alert.AlertEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.alert.AlertLink;

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

}