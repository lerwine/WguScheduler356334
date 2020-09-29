package Erwine.Leonard.T.wguscheduler356334.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import Erwine.Leonard.T.wguscheduler356334.entity.AlertListItem;
import Erwine.Leonard.T.wguscheduler356334.entity.AssessmentAlertEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.AssessmentDetails;
import Erwine.Leonard.T.wguscheduler356334.entity.AssessmentEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.CourseAlertEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.CourseDetails;
import Erwine.Leonard.T.wguscheduler356334.entity.CourseEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.MentorCourseListItem;
import Erwine.Leonard.T.wguscheduler356334.entity.MentorEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.MentorListItem;
import Erwine.Leonard.T.wguscheduler356334.entity.TermCourseListItem;
import Erwine.Leonard.T.wguscheduler356334.entity.TermEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.TermListItem;

@Database(
        entities = {TermEntity.class, MentorEntity.class, CourseEntity.class, CourseAlertEntity.class, AssessmentEntity.class, AssessmentAlertEntity.class},
        views = {TermListItem.class, MentorListItem.class, TermCourseListItem.class, MentorCourseListItem.class, CourseDetails.class, AssessmentDetails.class, AlertListItem.class},
        version = 1, exportSchema = false
)
@TypeConverters({LocalDateConverter.class, CourseStatusConverter.class, StringModelListConverter.class, AssessmentStatusConverter.class, AssessmentTypeConverter.class})
public abstract class AppDb extends RoomDatabase {

    public static final String DB_NAME = "WguScheduler.db";
    public static final String TABLE_NAME_TERMS = "terms";
    public static final String TABLE_NAME_MENTORS = "mentors";
    public static final String TABLE_NAME_COURSES = "courses";
    public static final String TABLE_NAME_ASSESSMENTS = "assessments";
    public static final String TABLE_NAME_COURSE_ALERTS = "courseAlerts";
    public static final String TABLE_NAME_ASSESSMENT_ALERTS = "assessmentAlerts";
    private static volatile AppDb instance;
    private static final Object SYNC_ROOT = new Object();

    public abstract TermDAO termDAO();

    public abstract MentorDAO mentorDAO();

    public abstract CourseDAO courseDAO();

    public abstract CourseAlertDAO courseAlertDAO();

    public abstract AssessmentDAO assessmentDAO();

    public abstract AssessmentAlertDAO assessmentAlertDAO();

    static AppDb getInstance(Context context) {
        if (null == instance) {
            synchronized (SYNC_ROOT) {
                if (null == instance) {
                    instance = Room.databaseBuilder(context.getApplicationContext(), AppDb.class, DB_NAME).build();
                }
            }
        }
        return instance;
    }

}
