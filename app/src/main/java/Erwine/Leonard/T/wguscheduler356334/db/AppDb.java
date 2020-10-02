package Erwine.Leonard.T.wguscheduler356334.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import Erwine.Leonard.T.wguscheduler356334.entity.alert.AlertEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.alert.AlertListItem;
import Erwine.Leonard.T.wguscheduler356334.entity.assessment.AssessmentAlertLink;
import Erwine.Leonard.T.wguscheduler356334.entity.assessment.AssessmentDetails;
import Erwine.Leonard.T.wguscheduler356334.entity.assessment.AssessmentEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.course.CourseAlertLink;
import Erwine.Leonard.T.wguscheduler356334.entity.course.CourseDetails;
import Erwine.Leonard.T.wguscheduler356334.entity.course.CourseEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.course.MentorCourseListItem;
import Erwine.Leonard.T.wguscheduler356334.entity.course.TermCourseListItem;
import Erwine.Leonard.T.wguscheduler356334.entity.mentor.MentorEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.mentor.MentorListItem;
import Erwine.Leonard.T.wguscheduler356334.entity.term.TermEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.term.TermListItem;

@Database(
        entities = {TermEntity.class, MentorEntity.class, CourseEntity.class, CourseAlertLink.class, AssessmentEntity.class, AssessmentAlertLink.class, AlertEntity.class},
//        views = {TermListItem.class, MentorListItem.class, TermCourseListItem.class, MentorCourseListItem.class, CourseDetails.class, AssessmentDetails.class},
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
    public static final String TABLE_NAME_ALERTS = "alerts";
    public static final String TABLE_NAME_COURSE_ALERTS = "courseAlerts";
    public static final String TABLE_NAME_ASSESSMENT_ALERTS = "assessmentAlerts";
    public static final String VIEW_NAME_ALERT_LIST = "alertListView";
    public static final String VIEW_NAME_MENTOR_COURSE = "mentorCourseView";
    public static final String VIEW_NAME_ASSESSMENT_DETAIL = "assessmentDetailView";
    public static final String VIEW_NAME_COURSE_DETAIL = "courseDetailView";
    public static final String VIEW_NAME_TERM_COURSE_LIST = "termCourseListView";
    public static final String VIEW_NAME_MENTOR_LIST = "mentorListView";
    public static final String VIEW_NAME_TERM_LIST = "termListView";
    private static volatile AppDb instance;
    private static final Object SYNC_ROOT = new Object();

    public abstract TermDAO termDAO();

    public abstract MentorDAO mentorDAO();

    public abstract CourseDAO courseDAO();

    public abstract CourseAlertDAO courseAlertDAO();

    public abstract AssessmentDAO assessmentDAO();

    public abstract AssessmentAlertDAO assessmentAlertDAO();

    public abstract AlertDAO alertDAO();

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
