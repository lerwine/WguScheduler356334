package Erwine.Leonard.T.wguscheduler356334.entity.course;

import androidx.annotation.NonNull;
import androidx.room.Embedded;
import androidx.room.Ignore;
import androidx.room.Relation;

import java.time.LocalDate;
import java.util.Objects;

import Erwine.Leonard.T.wguscheduler356334.db.LocalDateConverter;
import Erwine.Leonard.T.wguscheduler356334.entity.IdIndexedEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.alert.Alert;
import Erwine.Leonard.T.wguscheduler356334.entity.alert.AlertEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.alert.AlertLink;
import Erwine.Leonard.T.wguscheduler356334.entity.alert.AlertLinkEntity;
import Erwine.Leonard.T.wguscheduler356334.ui.alert.CourseAlertListViewModel;
import Erwine.Leonard.T.wguscheduler356334.util.ToStringBuilder;

public class CourseAlert implements AlertLinkEntity<CourseAlertLink> {
    @Embedded
    @NonNull
    private CourseAlertLink link;

    @Relation(
            parentColumn = AlertLink.COLNAME_ALERT_ID,
            entityColumn = Alert.COLNAME_ID
    )
    @NonNull
    private AlertEntity alert;
    @Ignore
    private LocalDate alertDate;
    @Ignore
    private long relativeDays = 0L;
    @Ignore
    private boolean messagePresent;
    @Ignore
    private String message;

    public CourseAlert(@NonNull CourseAlertLink link, @NonNull AlertEntity alert) {
        if (alert.getId() != link.getAlertId()) {
            throw new IllegalArgumentException();
        }
        this.link = link;
        this.alert = alert;
    }

    @Ignore
    public CourseAlert(@NonNull CourseAlert source) {
        alert = new AlertEntity(source.alert);
        link = new CourseAlertLink(source.link);
        alertDate = source.alertDate;
        relativeDays = source.relativeDays;
        messagePresent = source.messagePresent;
        message = source.message;
    }

    @Ignore
    public CourseAlert() {
        alert = new AlertEntity();
        link = new CourseAlertLink();
    }

    public boolean isMessagePresent() {
        return messagePresent;
    }

    public String getMessage() {
        return message;
    }

    public LocalDate getAlertDate() {
        return alertDate;
    }

    public long getRelativeDays() {
        return relativeDays;
    }

    @Override
    @NonNull
    public CourseAlertLink getLink() {
        return link;
    }

    public synchronized void setLink(@NonNull CourseAlertLink link) {
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

    public boolean reCalculate(CourseAlertListViewModel viewModel) {
        Boolean subsequent = alert.isSubsequent();
        if (null == subsequent) {
            return false;
        }
        relativeDays = alert.getTimeSpec();
        LocalDate date = (subsequent) ?
                viewModel.getEffectiveEndDate() :
                viewModel.getEffectiveStartDate();
        LocalDate oldValue = alertDate;
        alertDate = (null == date) ? null : date.plusDays(relativeDays);
        return !Objects.equals(oldValue, alertDate);
    }

    public void calculate(CourseAlertListViewModel viewModel) {
        Boolean subsequent = alert.isSubsequent();
        if (null == subsequent) {
            alertDate = LocalDateConverter.toLocalDate(alert.getTimeSpec());
            relativeDays = 0L;
        } else {
            relativeDays = alert.getTimeSpec();
            LocalDate date = (subsequent) ?
                    viewModel.getEffectiveEndDate() :
                    viewModel.getEffectiveStartDate();
            alertDate = (null == date) ? null : date.plusDays(relativeDays);
        }
        String m = alert.getCustomMessage();
        if (null == m) {
            messagePresent = false;
            message = "";
        } else {
            messagePresent = true;
            message = m;
        }
    }

    @Override
    public void appendPropertiesAsStrings(@NonNull ToStringBuilder sb) {
        sb.append("link", getLink()).append("alert", getAlert()).append("alertDate", alertDate).append("relativeDays", relativeDays)
                .append("messagePresent", messagePresent).append("message", message);
    }
}
