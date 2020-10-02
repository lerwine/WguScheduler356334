package Erwine.Leonard.T.wguscheduler356334.entity.assessment;

import androidx.annotation.NonNull;
import androidx.room.Embedded;
import androidx.room.Relation;

import Erwine.Leonard.T.wguscheduler356334.entity.IdIndexedEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.alert.Alert;
import Erwine.Leonard.T.wguscheduler356334.entity.alert.AlertEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.alert.AlertLink;
import Erwine.Leonard.T.wguscheduler356334.entity.alert.AlertLinkEntity;

public class AssessmentAlert implements AlertLinkEntity<AssessmentAlertLink> {
    @Embedded
    @NonNull
    AssessmentAlertLink link;

    @Relation(
            parentColumn = AlertLink.COLNAME_ALERT_ID,
            entityColumn = Alert.COLNAME_ID
    )
    @NonNull
    private AlertEntity alert;

    public AssessmentAlert(@NonNull AssessmentAlertLink link, @NonNull AlertEntity alert) {
        if (IdIndexedEntity.assertNotNewId(alert.getId()) != link.getAlertId()) {
            throw new IllegalArgumentException();
        }
        this.link = link;
        this.alert = alert;
    }

    @Override
    @NonNull
    public AssessmentAlertLink getLink() {
        return link;
    }

    public synchronized void setLink(@NonNull AssessmentAlertLink link) {
        if (link.getAlertId() != alert.getId()) {
            throw new IllegalArgumentException();
        }
        this.link = link;
    }

    @Override
    @NonNull
    public AlertEntity getAlert() {
        return alert;
    }

    public synchronized void setAlert(@NonNull AlertEntity alert) {
        link.setAlertId(IdIndexedEntity.assertNotNewId(alert.getId()));
        this.alert = alert;
    }

}
