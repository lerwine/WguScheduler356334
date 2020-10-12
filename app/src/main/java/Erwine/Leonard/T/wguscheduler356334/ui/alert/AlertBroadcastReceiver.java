package Erwine.Leonard.T.wguscheduler356334.ui.alert;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.app.NotificationCompat;

import java.time.LocalDateTime;
import java.util.Calendar;

import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.db.DbLoader;
import Erwine.Leonard.T.wguscheduler356334.entity.alert.AlertLink;
import Erwine.Leonard.T.wguscheduler356334.entity.alert.AlertLinkEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.alert.AlertListItem;
import Erwine.Leonard.T.wguscheduler356334.util.OneTimeObservers;
import io.reactivex.Single;

import static Erwine.Leonard.T.wguscheduler356334.entity.alert.AlertLink.COLNAME_ALERT_ID;
import static Erwine.Leonard.T.wguscheduler356334.entity.alert.AlertLink.COLNAME_TARGET_ID;
import static android.content.Context.NOTIFICATION_SERVICE;

public abstract class AlertBroadcastReceiver<T extends AlertLink, U extends AlertLinkEntity<T>> extends BroadcastReceiver {
    private static final String LOG_TAG = AlertBroadcastReceiver.class.getName();

    @NonNull
    protected abstract Single<U> getEntity(@NonNull DbLoader dbLoader, long alertId, long targetId);

    @NonNull
    protected abstract CharSequence getNotificationTitle(@NonNull U entity, @NonNull Context context);

    @NonNull
    protected abstract CharSequence getNotificationContent(@NonNull U entity, @NonNull Context context);

    @StringRes
    protected abstract int getChannelId();

    @StringRes
    protected abstract int getChannelName();

    @Override
    public void onReceive(Context context, Intent intent) {
        long alertId = intent.getLongExtra(AlertLink.COLNAME_ALERT_ID, -1);
        long assessmentId = intent.getLongExtra(AlertLink.COLNAME_TARGET_ID, -1);
        createNotificationChannel(context);
        OneTimeObservers.subscribeOnce(getEntity(DbLoader.getInstance(context.getApplicationContext()), alertId, assessmentId), entity -> {
            Notification n = new NotificationCompat.Builder(context, context.getResources().getString(getChannelId()))
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentText(getNotificationContent(entity, context))
                    .setContentTitle(getNotificationTitle(entity, context))
                    .build();
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(entity.getLink().getNotificationId(), n);
        }, throwable -> Log.e(LOG_TAG, "Error loading alert", throwable));
    }


    public void createNotificationChannel(@NonNull Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Resources resources = context.getResources();
            NotificationChannel channel = new NotificationChannel(resources.getString(getChannelId()),
                    resources.getString(getChannelName()), NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @NonNull
    public static <T extends AlertBroadcastReceiver<?, ?>> PendingIntent createAlertIntent(long alertId, long targetId, int notificationId, Context packageContext, Class<T> cls) {
        Intent intent = new Intent(packageContext, cls);
        intent.putExtra(COLNAME_ALERT_ID, alertId);
        intent.putExtra(COLNAME_TARGET_ID, targetId);
        return PendingIntent.getBroadcast(packageContext, notificationId, intent, 0);
    }

    @Nullable
    public static <T extends AlertBroadcastReceiver<?, ?>> PendingIntent getAlertIntent(long alertId, long targetId, int notificationId, Context packageContext, Class<T> cls) {
        Intent intent = new Intent(packageContext, cls);
        intent.putExtra(COLNAME_ALERT_ID, alertId);
        intent.putExtra(COLNAME_TARGET_ID, targetId);
        return PendingIntent.getBroadcast(packageContext, notificationId, intent, PendingIntent.FLAG_NO_CREATE);
    }

    protected static void setPendingAlert(@NonNull LocalDateTime dateTime, PendingIntent intent, @NonNull Context packageContext) {
        AlarmManager alarmManager = (AlarmManager) packageContext.getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.set(dateTime.getYear(), dateTime.getMonthValue() - 1, dateTime.getDayOfMonth(), dateTime.getHour(), dateTime.getMinute());
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), intent);
    }

    protected static void cancelPendingAlert(PendingIntent intent, Context packageContext) {
        if (null != intent) {
            AlarmManager alarmManager = (AlarmManager) packageContext.getSystemService(Context.ALARM_SERVICE);
            if (null != alarmManager) {
                alarmManager.cancel(intent);
            }
        }
    }

    public static void cancelPendingAlert(@NonNull AlertListItem alert, @NonNull Context packageContext) {
        if (alert.isAssessment()) {
            AssessmentAlertBroadcastReceiver.cancelPendingAlert(alert.getId(), alert.getTargetId(), alert.getNotificationId(), packageContext);
        } else {
            CourseAlertBroadcastReceiver.cancelPendingAlert(alert.getId(), alert.getTargetId(), alert.getNotificationId(), packageContext);
        }
    }
}
