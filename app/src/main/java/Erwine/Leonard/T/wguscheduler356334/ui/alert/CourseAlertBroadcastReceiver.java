package Erwine.Leonard.T.wguscheduler356334.ui.alert;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.time.LocalDate;
import java.time.LocalDateTime;

import Erwine.Leonard.T.wguscheduler356334.MainActivity;
import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.db.DbLoader;
import Erwine.Leonard.T.wguscheduler356334.db.LocalDateConverter;
import Erwine.Leonard.T.wguscheduler356334.entity.course.CourseAlertDetails;
import Erwine.Leonard.T.wguscheduler356334.entity.course.CourseAlertLink;
import Erwine.Leonard.T.wguscheduler356334.entity.course.CourseEntity;
import io.reactivex.Single;

public class CourseAlertBroadcastReceiver extends AlertBroadcastReceiver<CourseAlertLink, CourseAlertDetails> {
    private static final String LOG_TAG = MainActivity.getLogTag(CourseAlertBroadcastReceiver.class);

    @NonNull
    @Override
    protected Single<CourseAlertDetails> getEntity(@NonNull DbLoader dbLoader, long alertId, long targetId) {
        return dbLoader.getCourseAlertDetailsById(alertId, targetId);
    }

    @NonNull
    @Override
    protected CharSequence getNotificationTitle(@NonNull CourseAlertDetails entity, @NonNull Context context) {
        return "Course " + entity.getCourse().getNumber() + " Alert";
    }

    @NonNull
    @Override
    protected CharSequence getNotificationContent(@NonNull CourseAlertDetails entity, @NonNull Context context) {
        CourseEntity course = entity.getCourse();
        Resources resources = context.getResources();
        StringBuilder sb = new StringBuilder(course.getTitle());
        String n = entity.getMessage();
        if (null != n) {
            return sb.append("\n\n").append(n);
        }
        sb.append("\nStatus: ").append(resources.getString(course.getStatus().displayResourceId()));
        LocalDate d = course.getActualStart();
        if (null != d) {
            sb.append("Started: ").append(LocalDateConverter.LONG_FORMATTER.format(d));
        } else if (null != (d = course.getExpectedStart())) {
            sb.append("Expected Start: ").append(LocalDateConverter.LONG_FORMATTER.format(d));
        }
        if (null != (d = course.getActualEnd())) {
            return sb.append("Ended: ").append(LocalDateConverter.LONG_FORMATTER.format(d));
        }
        if (null != (d = course.getExpectedStart())) {
            return sb.append("Expected End: ").append(LocalDateConverter.LONG_FORMATTER.format(d));
        }
        return sb;
    }

    @Override
    protected int getChannelId() {
        return R.string.notification_channel_course_alert;
    }

    @Override
    protected int getChannelName() {
        return R.string.name_channel_course_alert;
    }

    @NonNull
    static PendingIntent createAlertIntent(long alertId, long targetId, int notificationId, @NonNull Context packageContext) {
        Log.d(LOG_TAG, "Enter createAlertIntent(alertId: " + alertId + ", targetId: " + targetId + ", notificationId: " + notificationId + ", packageContext: " + packageContext + ")");
        return createAlertIntent(alertId, targetId, notificationId, packageContext, CourseAlertBroadcastReceiver.class);
    }

    @Nullable
    static PendingIntent getAlertIntent(long alertId, long targetId, int notificationId, @NonNull Context packageContext) {
        Log.d(LOG_TAG, "Enter getAlertIntent(alertId: " + alertId + ", targetId: " + targetId + ", notificationId: " + notificationId + ", packageContext: " + packageContext + ")");
        return getAlertIntent(alertId, targetId, notificationId, packageContext, CourseAlertBroadcastReceiver.class);
    }

    public static void setPendingAlert(@NonNull LocalDateTime dateTime, @NonNull CourseAlertLink alertLink, int notificationId, @NonNull Context packageContext) {
        Log.d(LOG_TAG, "Enter setPendingAlert(dateTime: " + dateTime + ", alertLink: " + alertLink + ", notificationId: " + notificationId + ", packageContext: " + packageContext + ")");
        PendingIntent intent = createAlertIntent(alertLink.getAlertId(), alertLink.getTargetId(), notificationId, packageContext);
        setPendingAlert(dateTime, intent, packageContext);
    }

    static void cancelPendingAlert(long alertId, long targetId, int notificationId, @NonNull Context packageContext) {
        Log.d(LOG_TAG, "Enter cancelPendingAlert(alertId: " + alertId + ", targetId: " + targetId + ", notificationId: " + notificationId + ", packageContext: " + packageContext + ")");
        PendingIntent intent = getAlertIntent(alertId, targetId, notificationId, packageContext);
        if (null != intent) {
            AlarmManager alarmManager = (AlarmManager) packageContext.getSystemService(Context.ALARM_SERVICE);
            if (null != alarmManager) {
                alarmManager.cancel(intent);
            }
        }
    }

    public static void cancelPendingAlert(@NonNull CourseAlertLink alertLink, int notificationId, @NonNull Context packageContext) {
        cancelPendingAlert(alertLink.getAlertId(), alertLink.getTargetId(), notificationId, packageContext);
    }

}
