package Erwine.Leonard.T.wguscheduler356334.entity.assessment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Embedded;
import androidx.room.Ignore;
import androidx.room.Relation;

import java.time.LocalDate;
import java.util.Objects;

import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.db.LocalDateConverter;
import Erwine.Leonard.T.wguscheduler356334.entity.IdIndexedEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.alert.Alert;
import Erwine.Leonard.T.wguscheduler356334.entity.alert.AlertEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.alert.AlertLink;
import Erwine.Leonard.T.wguscheduler356334.entity.alert.AlertLinkEntity;
import Erwine.Leonard.T.wguscheduler356334.ui.assessment.EditAssessmentViewModel;
import Erwine.Leonard.T.wguscheduler356334.util.ToStringBuilder;
import Erwine.Leonard.T.wguscheduler356334.util.validation.ResourceMessageBuilder;

public class AssessmentAlert implements AlertLinkEntity<AssessmentAlertLink> {
    @Embedded
    @NonNull
    private AssessmentAlertLink link;

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

    public AssessmentAlert(@NonNull AssessmentAlertLink link, @NonNull AlertEntity alert) {
        if (alert.getId() != link.getAlertId()) {
            throw new IllegalArgumentException();
        }
        this.link = link;
        this.alert = alert;
    }

    @Ignore
    public AssessmentAlert(@NonNull AssessmentAlert source) {
        alert = new AlertEntity(source.alert);
        link = new AssessmentAlertLink(source.link);
        alertDate = source.alertDate;
        relativeDays = source.relativeDays;
        messagePresent = source.messagePresent;
        message = source.message;
    }

    @Ignore
    public AssessmentAlert() {
        alert = new AlertEntity();
        link = new AssessmentAlertLink();
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

    public void calculate(EditAssessmentViewModel viewModel) {
        Boolean subsequent = alert.isSubsequent();
        if (null == subsequent) {
            alertDate = LocalDateConverter.toLocalDate(alert.getTimeSpec());
            relativeDays = 0L;
        } else {
            relativeDays = alert.getTimeSpec();
            LocalDate date = (subsequent) ?
                    viewModel.getCompletionDate() :
                    viewModel.getGoalDate();
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

    public boolean reCalculate(@Nullable LocalDate goalDate, @Nullable LocalDate completionDate) {
        Boolean subsequent = alert.isSubsequent();
        if (null == subsequent) {
            return false;
        }
        relativeDays = alert.getTimeSpec();
        LocalDate date = (subsequent) ? completionDate : goalDate;
        LocalDate oldValue = alertDate;
        alertDate = (null == date) ? null : date.plusDays(relativeDays);
        return !Objects.equals(oldValue, alertDate);
    }

    public synchronized void validate(ResourceMessageBuilder builder) {
        AlertLink.validate(builder, this);
        Boolean subsequent = alert.isSubsequent();
        if (null != subsequent && subsequent && alert.getTimeSpec() < 0L) {
            builder.acceptError(R.string.message_alert_relative_before_completion);
        }
    }

    @Override
    public void appendPropertiesAsStrings(@NonNull ToStringBuilder sb) {
        sb.append("link", getLink()).append("alert", getAlert()).append("alertDate", alertDate).append("relativeDays", relativeDays)
                .append("messagePresent", messagePresent).append("message", message);
    }
}
