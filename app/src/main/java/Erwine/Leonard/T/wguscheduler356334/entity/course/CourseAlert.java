package Erwine.Leonard.T.wguscheduler356334.entity.course;

import androidx.annotation.NonNull;
import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.Objects;

import Erwine.Leonard.T.wguscheduler356334.entity.alert.Alert;
import Erwine.Leonard.T.wguscheduler356334.entity.alert.AlertEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.alert.AlertLink;

public class CourseAlert {
    @Embedded
    @NonNull
    private CourseAlertLink link;

    @Relation(
            parentColumn = AlertLink.COLNAME_ALERT_ID,
            entityColumn = Alert.COLNAME_ID
    )
    @NonNull
    private AlertEntity alert;

    public CourseAlert(@NonNull CourseAlertLink link, @NonNull AlertEntity alert) {
        this.link = link;
        this.alert = alert;
        if (null == alert.getId() || alert.getId() != link.getAlertId()) {
            throw new IllegalArgumentException();
        }
    }

    @NonNull
    public CourseAlertLink getLink() {
        return link;
    }

    public synchronized void setLink(@NonNull CourseAlertLink link) {
        if (!Objects.equals(link.getAlertId(), alert.getId())) {
            throw new IllegalArgumentException();
        }
        this.link = link;
    }

    @NonNull
    public AlertEntity getAlert() {
        return alert;
    }

    public synchronized void setAlert(@NonNull AlertEntity alert) {
        Long id = alert.getId();
        if (null == id) {
            throw new IllegalArgumentException();
        }
        link.setAlertId(id);
        this.alert = alert;
    }
}
