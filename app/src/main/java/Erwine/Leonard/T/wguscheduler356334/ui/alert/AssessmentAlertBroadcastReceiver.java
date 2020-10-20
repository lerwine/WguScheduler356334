package Erwine.Leonard.T.wguscheduler356334.ui.alert;

import android.app.PendingIntent;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.time.LocalDate;
import java.time.LocalDateTime;

import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.db.DbLoader;
import Erwine.Leonard.T.wguscheduler356334.db.LocalDateConverter;
import Erwine.Leonard.T.wguscheduler356334.entity.assessment.AssessmentAlertDetails;
import Erwine.Leonard.T.wguscheduler356334.entity.assessment.AssessmentAlertLink;
import Erwine.Leonard.T.wguscheduler356334.entity.assessment.AssessmentEntity;
import io.reactivex.Single;

public class AssessmentAlertBroadcastReceiver extends AlertBroadcastReceiver<AssessmentAlertLink, AssessmentAlertDetails> {
    private static final String LOG_TAG = AssessmentAlertBroadcastReceiver.class.getName();

    @NonNull
    @Override
    protected Single<AssessmentAlertDetails> getEntity(@NonNull DbLoader dbLoader, long alertId, long targetId) {
        return dbLoader.getAssessmentAlertDetailsById(alertId, targetId);
    }

    @NonNull
    @Override
    protected CharSequence getNotificationTitle(@NonNull AssessmentAlertDetails entity, @NonNull Context context) {
        Resources resources = context.getResources();
        AssessmentEntity assessment = entity.getAssessment();
        return resources.getString(R.string.format_assessment, resources.getString(assessment.getType().displayResourceId()), assessment.getCode()) + " Alert";
    }

    @NonNull
    @Override
    protected CharSequence getNotificationContent(@NonNull AssessmentAlertDetails entity, @NonNull Context context) {
        Resources resources = context.getResources();
        AssessmentEntity assessment = entity.getAssessment();
        String n = assessment.getName();
        StringBuilder sb;
        if (null != n) {
            sb = new StringBuilder(n);
            if (null != (n = entity.getMessage())) {
                return sb.append("\n\n").append(n);
            }
            sb.append("\nStatus: ");
        } else {
            if (null != (n = entity.getMessage())) {
                return n;
            }
            sb = new StringBuilder("Status: ");
        }
        sb.append(resources.getString(assessment.getStatus().displayResourceId()));
        LocalDate d = assessment.getCompletionDate();
        if (null != d) {
            return sb.append("\nCompleted: ").append(LocalDateConverter.LONG_FORMATTER.format(d));
        }
        if (null != (d = assessment.getGoalDate())) {
            return sb.append("\nGoal Date: ").append(LocalDateConverter.LONG_FORMATTER.format(d));
        }
        return sb;
    }

    @Override
    protected int getChannelId() {
        return R.string.notification_channel_assessment_alert;
    }

    @Override
    protected int getChannelName() {
        return R.string.name_channel_assessment_alert;
    }

    @NonNull
    static PendingIntent createAlertIntent(long alertId, long targetId, int notificationId, @NonNull Context packageContext) {
        Log.d(LOG_TAG, "Enter createAlertIntent(alertId: " + alertId + ", targetId: " + targetId + ", notificationId: " + notificationId + ", packageContext: " + packageContext + ")");
        return createAlertIntent(alertId, targetId, notificationId, packageContext, AssessmentAlertBroadcastReceiver.class);
    }

    @Nullable
    static PendingIntent getAlertIntent(long alertId, long targetId, int notificationId, @NonNull Context packageContext) {
        Log.d(LOG_TAG, "Enter getAlertIntent(alertId: " + alertId + ", targetId: " + targetId + ", notificationId: " + notificationId + ", packageContext: " + packageContext + ")");
        return getAlertIntent(alertId, targetId, notificationId, packageContext, AssessmentAlertBroadcastReceiver.class);
    }

    public static void setPendingAlert(@NonNull LocalDateTime dateTime, @NonNull AssessmentAlertLink alertLink, int notificationId, @NonNull Context packageContext) {
        Log.d(LOG_TAG, "Enter setPendingAlert(dateTime: " + dateTime + ", alertLink: " + alertLink + ", notificationId: " + notificationId + ", packageContext: " + packageContext + ")");
        PendingIntent intent = createAlertIntent(alertLink.getAlertId(), alertLink.getTargetId(), notificationId, packageContext);
        setPendingAlert(dateTime, intent, packageContext);
    }

    static void cancelPendingAlert(long alertId, long targetId, int notificationId, @NonNull Context packageContext) {
        Log.d(LOG_TAG, "Enter cancelPendingAlert(alertId: " + alertId + ", targetId: " + targetId + ", notificationId: " + notificationId + ", packageContext: " + packageContext + ")");
        cancelPendingAlert(getAlertIntent(alertId, targetId, notificationId, packageContext), packageContext);
    }

    public static void cancelPendingAlert(@NonNull AssessmentAlertLink alertLink, int notificationId, @NonNull Context packageContext) {
        cancelPendingAlert(alertLink.getAlertId(), alertLink.getTargetId(), notificationId, packageContext);
    }

}
