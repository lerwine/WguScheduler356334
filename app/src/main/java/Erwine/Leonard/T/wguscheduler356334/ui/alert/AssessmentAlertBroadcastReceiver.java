package Erwine.Leonard.T.wguscheduler356334.ui.alert;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.db.DbLoader;
import Erwine.Leonard.T.wguscheduler356334.entity.alert.AlertLink;
import Erwine.Leonard.T.wguscheduler356334.util.OneTimeObservers;

import static android.content.Context.NOTIFICATION_SERVICE;

public class AssessmentAlertBroadcastReceiver extends BroadcastReceiver {
    private static final String LOG_TAG = AssessmentAlertBroadcastReceiver.class.getName();

    @Override
    public void onReceive(Context context, Intent intent) {
        long alertId = intent.getLongExtra(AlertLink.COLNAME_ALERT_ID, -1);
        long assessmentId = intent.getLongExtra(AlertLink.COLNAME_TARGET_ID, -1);
        createNotificationChannel(context);
        OneTimeObservers.subscribeOnce(DbLoader.getInstance(context.getApplicationContext()).getAssessmentAlertDetailsById(alertId, assessmentId), assessmentAlertDetails -> {
            // TODO: Configure notification
            Notification n = new NotificationCompat.Builder(context, context.getResources().getString(R.string.notification_channel_assessment_alert))
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentText(assessmentAlertDetails.getAssessment().toString())
                    .setContentTitle(assessmentAlertDetails.getAssessment().getCode())
                    .build();
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(assessmentAlertDetails.getLink().getNotificationId(), n);
        }, throwable -> Log.e(LOG_TAG, "Error loading assessment alert", throwable));
    }

    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Resources resources = context.getResources();
            NotificationChannel channel = new NotificationChannel(resources.getString(R.string.notification_channel_assessment_alert),
                    resources.getString(R.string.name_channel_assessment_alert), NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
