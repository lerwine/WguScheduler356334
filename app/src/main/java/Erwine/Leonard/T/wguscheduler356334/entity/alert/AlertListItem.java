package Erwine.Leonard.T.wguscheduler356334.entity.alert;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.room.ColumnInfo;
import androidx.room.DatabaseView;
import androidx.room.Ignore;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.db.AppDb;
import Erwine.Leonard.T.wguscheduler356334.db.AssessmentStatusConverter;
import Erwine.Leonard.T.wguscheduler356334.db.AssessmentTypeConverter;
import Erwine.Leonard.T.wguscheduler356334.db.CourseStatusConverter;
import Erwine.Leonard.T.wguscheduler356334.db.LocalDateConverter;
import Erwine.Leonard.T.wguscheduler356334.entity.IdIndexedEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.assessment.Assessment;
import Erwine.Leonard.T.wguscheduler356334.entity.assessment.AssessmentStatus;
import Erwine.Leonard.T.wguscheduler356334.entity.assessment.AssessmentType;
import Erwine.Leonard.T.wguscheduler356334.entity.course.Course;
import Erwine.Leonard.T.wguscheduler356334.entity.course.CourseStatus;
import Erwine.Leonard.T.wguscheduler356334.util.ToStringBuilder;

@DatabaseView(
        viewName = AppDb.VIEW_NAME_ALERT_LIST,
        value = "SELECT assessmentAlerts.alertId AS id, assessmentAlerts.targetId, courses.termId, courses.mentorId, alerts.notificationId, assessments.type, assessments.code, assessments.name AS title," +
                "assessments.status, alerts.timeSpec, alerts.subsequent, alerts.customMessage, courses.number as courseNumber, courses.title as courseTitle,\n" +
                "\tCASE\n" +
                "\t\tWHEN alerts.subsequent IS NULL THEN alerts.timeSpec\n" +
                "\t\tWHEN alerts.subsequent=1 THEN assessments.completionDate\n" +
                "\t\tELSE assessments.goalDate\n" +
                "\tEND as eventDate,\n" +
                "\tCASE\n" +
                "\t\tWHEN alerts.subsequent IS NULL THEN alerts.timeSpec\n" +
                "\t\tWHEN alerts.subsequent=1 THEN\n" +
                "\t\t\tCASE WHEN assessments.completionDate IS NULL THEN NULL ELSE assessments.completionDate + alerts.timeSpec END\n" +
                "\t\tELSE\n" +
                "\t\t\tCASE WHEN assessments.goalDate IS NULL THEN NULL ELSE assessments.goalDate + alerts.timeSpec END\n" +
                "\tEND as alertDate, alerts.alertTime, 1 as assessment, assessments.courseId\n" +
                "\tFROM assessmentAlerts LEFT JOIN alerts on assessmentAlerts.alertId=alerts.id LEFT JOIN assessments on assessmentAlerts.targetId=assessments.id\n" +
                "\tLEFT JOIN courses on assessments.courseId=courses.id\n" +
                "UNION SELECT courseAlerts.alertId AS id, courseAlerts.targetId, courses.termId, courses.mentorId, alerts.notificationId, NULL as type, courses.number as code, courses.title," +
                "courses.status, alerts.timeSpec, alerts.subsequent, alerts.customMessage, courses.number as courseNumber, courses.title as courseTitle,\n" +
                "\tCASE\n" +
                "\t\tWHEN alerts.subsequent IS NULL THEN alerts.timeSpec\n" +
                "\t\tWHEN alerts.subsequent=1 THEN\n" +
                "\t\t\tCASE WHEN courses.actualEnd IS NULL THEN courses.expectedEnd ELSE courses.actualEnd END\n" +
                "\t\tELSE\n" +
                "\t\t\tCASE WHEN courses.actualStart IS NULL THEN courses.expectedStart ELSE courses.actualStart END\n" +
                "\tEND as eventDate,\n" +
                "\tCASE\n" +
                "\t\tWHEN alerts.subsequent IS NULL THEN alerts.timeSpec\n" +
                "\t\tWHEN alerts.subsequent=1 THEN\n" +
                "\t\t\tCASE\n" +
                "\t\t\t\tWHEN courses.actualEnd IS NULL THEN\n" +
                "\t\t\t\t\tCASE WHEN courses.expectedEnd IS NULL THEN NULL ELSE courses.expectedEnd + alerts.timeSpec END\n" +
                "\t\t\t\tELSE courses.actualEnd + alerts.timeSpec\n" +
                "\t\t\tEND\n" +
                "\t\tELSE\n" +
                "\t\t\tCASE\n" +
                "\t\t\t\tWHEN courses.actualStart IS NULL THEN\n" +
                "\t\t\t\t\tCASE WHEN courses.expectedStart IS NULL THEN NULL ELSE courses.expectedStart + alerts.timeSpec END\n" +
                "\t\t\t\tELSE courses.actualStart + alerts.timeSpec\n" +
                "\t\t\tEND\n" +
                "\tEND as alertDate, alerts.alertTime, 0 as assessment, courseAlerts.targetId as courseId\n" +
                "\tFROM courseAlerts LEFT JOIN alerts ON courseAlerts.alertId=alerts.id LEFT JOIN courses ON courseAlerts.targetId=courses.id"
)
public final class AlertListItem extends AlertEntity implements Comparable<AlertListItem> {

    static final String COLNAME_TARGET_ID = "targetId";
    static final String COLNAME_TYPE = "type";
    static final String COLNAME_CODE = "code";
    static final String COLNAME_TITLE = "title";
    static final String COLNAME_STATUS = "status";
    static final String COLNAME_COURSE_NUMBER = "courseNumber";
    static final String COLNAME_COURSE_TITLE = "courseTitle";
    static final String COLNAME_EVENT_DATE = "eventDate";
    static final String COLNAME_ALERT_DATE = "alertDate";
    static final String COLNAME_ASSESSMENT = "assessment";
    static final String COLNAME_COURSE_ID = "courseId";
    static final String COLNAME_TERM_ID = "termId";
    static final String COLNAME_MENTOR_ID = "mentorId";

    @ColumnInfo(name = COLNAME_TARGET_ID)
    private long targetId;
    @ColumnInfo(name = COLNAME_ASSESSMENT)
    private boolean assessment;
    @ColumnInfo(name = COLNAME_COURSE_NUMBER)
    @NonNull
    private String courseNumber;
    @ColumnInfo(name = COLNAME_COURSE_TITLE)
    @NonNull
    private String courseTitle;
    @ColumnInfo(name = COLNAME_EVENT_DATE)
    @Nullable
    private LocalDate eventDate;
    @ColumnInfo(name = COLNAME_ALERT_DATE)
    @Nullable
    private LocalDate alertDate;
    @ColumnInfo(name = COLNAME_CODE)
    private String code;
    @ColumnInfo(name = COLNAME_TITLE)
    @Nullable
    private String title;
    @ColumnInfo(name = COLNAME_TYPE)
    private AssessmentType type;
    @ColumnInfo(name = COLNAME_STATUS)
    private int status;
    @ColumnInfo(name = COLNAME_COURSE_ID)
    private long courseId;
    @ColumnInfo(name = COLNAME_TERM_ID)
    private long termId;
    @ColumnInfo(name = COLNAME_MENTOR_ID)
    @Nullable
    private Long mentorId;
    @Ignore
    private AssessmentStatus assessmentStatus;
    @Ignore
    private CourseStatus courseStatus;
    @Ignore
    @StringRes
    private int statusDisplayResourceId;
    @Ignore
    @StringRes
    private int typeDisplayResourceId;

    public AlertListItem(@Nullable Boolean subsequent, long timeSpec, String customMessage, boolean assessment, @NonNull String courseNumber, @NonNull String courseTitle, @Nullable LocalDate eventDate, @Nullable LocalDate alertDate, @Nullable LocalTime alertTime, String code,
                         @Nullable String title, AssessmentType type, int status, long courseId, long termId, @Nullable Long mentorId, long targetId, int notificationId, long id) {
        super(IdIndexedEntity.assertNotNewId(id), timeSpec, subsequent, customMessage, notificationId, alertTime);
        this.assessment = assessment;
        this.courseNumber = courseNumber;
        this.courseTitle = courseTitle;
        this.eventDate = eventDate;
        this.alertDate = alertDate;
        this.code = SINGLE_LINE_NORMALIZER.apply(code);
        String s = SINGLE_LINE_NORMALIZER.apply(title);
        this.title = (s.isEmpty()) ? null : s;
        this.status = status;
        this.courseId = courseId;
        this.termId = termId;
        this.mentorId = mentorId;
        this.targetId = targetId;
        if (assessment) {
            assessmentStatus = AssessmentStatusConverter.toAssessmentStatus(status);
            courseStatus = CourseStatusConverter.fromAssessmentStatus(assessmentStatus);
            statusDisplayResourceId = assessmentStatus.displayResourceId();
            this.type = (null == type) ? AssessmentType.OBJECTIVE_ASSESSMENT : type;
            typeDisplayResourceId = this.type.displayResourceId();
        } else {
            courseStatus = CourseStatusConverter.toCourseStatus(status);
            assessmentStatus = AssessmentStatusConverter.fromCourseStatus(courseStatus);
            statusDisplayResourceId = courseStatus.displayResourceId();
            typeDisplayResourceId = R.string.label_course;
        }
    }

    public boolean isAssessment() {
        return assessment;
    }

    public synchronized void setAssessment(boolean assessment) {
        this.assessment = assessment;
        if (assessment) {
            typeDisplayResourceId = R.string.label_course;
        } else {
            typeDisplayResourceId = this.type.displayResourceId();
        }
    }

    @Override
    public synchronized void setSubsequent(Boolean subsequent) {
        if (null == isSubsequent()) {
            if (null != subsequent) {
                Long a = LocalDateConverter.fromLocalDate(alertDate);
                Long e = LocalDateConverter.fromLocalDate(eventDate);
                super.setTimeSpec((null != a && null != e) ? a - e : 0L);
            }
        } else if (null == subsequent) {
            if (null == eventDate) {
                if (null == alertDate) {
                    super.setTimeSpec(0L);
                } else {
                    super.setTimeSpec(LocalDateConverter.fromLocalDate(eventDate = alertDate));
                }
            } else {
                super.setTimeSpec(LocalDateConverter.fromLocalDate(alertDate = eventDate));
            }
        }
        super.setSubsequent(subsequent);
    }

    @Nullable
    public Long getMentorId() {
        return mentorId;
    }

    public void setMentorId(@Nullable Long mentorId) {
        this.mentorId = mentorId;
    }

    public long getTargetId() {
        return targetId;
    }

    public void setTargetId(long targetId) {
        this.targetId = targetId;
    }

    @NonNull
    public String getCourseNumber() {
        return courseNumber;
    }

    public void setCourseNumber(@NonNull String courseNumber) {
        this.courseNumber = courseNumber;
    }

    @NonNull
    public String getCourseTitle() {
        return courseTitle;
    }

    public void setCourseTitle(@NonNull String courseTitle) {
        this.courseTitle = courseTitle;
    }

    @Nullable
    public LocalDate getEventDate() {
        return eventDate;
    }

    public synchronized void setEventDate(@Nullable LocalDate eventDate) {
        this.eventDate = eventDate;
        Long e = LocalDateConverter.fromLocalDate(eventDate);
        if (null == isSubsequent()) {
            this.alertDate = eventDate;
            super.setTimeSpec((null != e) ? e : LocalDateConverter.fromLocalDate(LocalDate.now()));
        } else {
            Long a = LocalDateConverter.fromLocalDate(alertDate);
            if (null != e && null != a) {
                super.setTimeSpec(e - a);
            }
        }
    }

    @Nullable
    public LocalDate getAlertDate() {
        return alertDate;
    }

    public synchronized void setAlertDate(@Nullable LocalDate alertDate) {
        this.alertDate = alertDate;
        Long a = LocalDateConverter.fromLocalDate(alertDate);
        if (null == isSubsequent()) {
            this.eventDate = alertDate;
            super.setTimeSpec((null != a) ? a : LocalDateConverter.fromLocalDate(LocalDate.now()));
        } else {
            Long e = LocalDateConverter.fromLocalDate(eventDate);
            if (null != a && null != e) {
                super.setTimeSpec(e - a);
            }
        }
    }

    @Override
    public synchronized void setTimeSpec(long days) {
        super.setTimeSpec(days);
        if (null == isSubsequent()) {
            alertDate = eventDate = LocalDateConverter.toLocalDate(days);
        } else if (null != eventDate) {
            alertDate = eventDate.plusDays(days);
        } else if (null != alertDate) {
            eventDate = alertDate.minusDays(days);
        }
    }

    @NonNull
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = SINGLE_LINE_NORMALIZER.apply(code);
    }

    @Nullable
    public String getTitle() {
        return title;
    }

    public void setTitle(@Nullable String title) {
        String s = SINGLE_LINE_NORMALIZER.apply(title);
        this.title = (s.isEmpty()) ? null : s;
    }

    @Nullable
    public AssessmentType getType() {
        return (assessment) ? null : type;
    }

    public void setType(AssessmentType type) {
        this.type = (null == type) ? AssessmentType.OBJECTIVE_ASSESSMENT : type;
        if (!assessment) {
            typeDisplayResourceId = this.type.displayResourceId();
        }
    }

    public int getStatus() {
        return status;
    }

    public synchronized void setStatus(int status) {
        if (this.status != status) {
            if (assessment) {
                setAssessmentStatus(AssessmentStatusConverter.toAssessmentStatus(status));
            } else {
                setCourseStatus(CourseStatusConverter.toCourseStatus(status));
            }
        }
    }

    @Nullable
    public AssessmentStatus getAssessmentStatus() {
        return assessmentStatus;
    }

    public synchronized void setAssessmentStatus(AssessmentStatus assessmentStatus) {
        if (null == assessmentStatus) {
            if (!assessment || this.assessmentStatus == AssessmentStatusConverter.DEFAULT) {
                return;
            }
            this.assessmentStatus = AssessmentStatusConverter.DEFAULT;
        } else {
            if (this.assessmentStatus == assessmentStatus) {
                return;
            }
            this.assessmentStatus = assessmentStatus;
            if (assessment) {
                courseStatus = CourseStatusConverter.fromAssessmentStatus(assessmentStatus);
            } else {
                CourseStatus e = CourseStatusConverter.fromAssessmentStatus(assessmentStatus);
                if (courseStatus == e) {
                    return;
                }
                courseStatus = e;
                assessment = true;
                typeDisplayResourceId = R.string.label_course;
            }
        }
        status = this.assessmentStatus.ordinal();
        statusDisplayResourceId = this.assessmentStatus.displayResourceId();
    }

    @NonNull
    public CourseStatus getCourseStatus() {
        return courseStatus;
    }

    public synchronized void setCourseStatus(CourseStatus courseStatus) {
        if (null == courseStatus) {
            if (assessment || this.courseStatus == CourseStatus.UNPLANNED) {
                return;
            }
            this.courseStatus = CourseStatus.UNPLANNED;
        } else {
            if (this.courseStatus == courseStatus) {
                return;
            }
            this.courseStatus = courseStatus;
            if (assessment) {
                AssessmentStatus e = AssessmentStatusConverter.fromCourseStatus(courseStatus);
                if (assessmentStatus == e) {
                    return;
                }
                assessmentStatus = e;
                assessment = false;
                typeDisplayResourceId = this.type.displayResourceId();
            } else {
                assessmentStatus = AssessmentStatusConverter.fromCourseStatus(courseStatus);
            }
        }
        status = this.courseStatus.ordinal();
        statusDisplayResourceId = this.courseStatus.displayResourceId();
    }

    @StringRes
    public int getStatusDisplayResourceId() {
        return statusDisplayResourceId;
    }

    public int getTypeDisplayResourceId() {
        return typeDisplayResourceId;
    }

    public long getCourseId() {
        return courseId;
    }

    public void setCourseId(long courseId) {
        this.courseId = courseId;
    }

    public long getTermId() {
        return termId;
    }

    public void setTermId(long termId) {
        this.termId = termId;
    }

    @Override
    protected int hashCodeFromProperties() {
        return Objects.hash(courseId, isSubsequent(), getTimeSpec(), assessment, eventDate, code, title, type, status);
    }

    @NonNull
    @Override
    public String dbTableName() {
        return (assessment) ? AppDb.TABLE_NAME_ASSESSMENT_ALERTS : AppDb.TABLE_NAME_COURSE_ALERTS;
    }

    @Override
    public void restoreState(@NonNull Bundle bundle, boolean isOriginal) {
        super.restoreState(bundle, isOriginal);
        String key = stateKey(COLNAME_COURSE_ID, isOriginal);
        if (bundle.containsKey(key)) {
            courseId = bundle.getLong(key);
        }
        setAssessment(bundle.getBoolean(stateKey(COLNAME_ASSESSMENT, isOriginal), false));
        key = stateKey(COLNAME_EVENT_DATE, isOriginal);
        if (bundle.containsKey(key)) {
            setEventDate(LocalDateConverter.toLocalDate(bundle.getLong(key)));
        } else {
            setEventDate(null);
        }
        setCode(bundle.getString(stateKey(COLNAME_CODE, isOriginal), ""));
        setTitle(bundle.getString(stateKey(COLNAME_TITLE, isOriginal), ""));
        setType(AssessmentTypeConverter.toAssessmentType(bundle.getInt(stateKey(COLNAME_TYPE, isOriginal), 0)));
        if (assessment) {
            setCourseStatus(CourseStatusConverter.toCourseStatus(bundle.getInt(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_COURSES, Course.COLNAME_STATUS, isOriginal), 0)));
            setAssessmentStatus(AssessmentStatusConverter.toAssessmentStatus(bundle.getInt(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_ASSESSMENTS, Assessment.COLNAME_STATUS, isOriginal), 0)));
            setAssessment(true);
        } else {
            setAssessmentStatus(AssessmentStatusConverter.toAssessmentStatus(bundle.getInt(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_ASSESSMENTS, Assessment.COLNAME_STATUS, isOriginal), 0)));
            setCourseStatus(CourseStatusConverter.toCourseStatus(bundle.getInt(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_COURSES, Course.COLNAME_STATUS, isOriginal), 0)));
            setAssessment(false);
        }
    }

    @Override
    public void saveState(@NonNull Bundle bundle, boolean isOriginal) {
        super.saveState(bundle, isOriginal);
        bundle.putLong(stateKey(COLNAME_COURSE_ID, isOriginal), courseId);
        bundle.putBoolean(stateKey(COLNAME_ASSESSMENT, isOriginal), assessment);
        Long d = LocalDateConverter.fromLocalDate(eventDate);
        if (null != d) {
            bundle.putLong(stateKey(COLNAME_EVENT_DATE, isOriginal), d);
        }
        bundle.putString(stateKey(COLNAME_CODE, isOriginal), code);
        String s = title;
        if (!s.isEmpty()) {
            bundle.putString(stateKey(COLNAME_TITLE, isOriginal), s);
        }
        bundle.putInt(stateKey(COLNAME_TYPE, isOriginal), AssessmentTypeConverter.fromAssessmentType(type));
        bundle.putInt(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_ASSESSMENTS, Assessment.COLNAME_STATUS, isOriginal), AssessmentStatusConverter.fromAssessmentStatus(assessmentStatus));
        bundle.putInt(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_COURSES, Course.COLNAME_STATUS, isOriginal), CourseStatusConverter.fromCourseStatus(courseStatus));
    }

    @Override
    public void appendPropertiesAsStrings(@NonNull ToStringBuilder sb) {
        super.appendPropertiesAsStrings(sb);
        sb.append(COLNAME_COURSE_ID, getCourseId())
                .append(COLNAME_ASSESSMENT, isAssessment())
                .append(COLNAME_EVENT_DATE, getEventDate())
                .append(COLNAME_ALERT_DATE, getAlertDate())
                .append(COLNAME_CODE, getCode())
                .append(COLNAME_TITLE, getTitle())
                .append(COLNAME_TYPE, getType())
                .append(COLNAME_STATUS, getStatus());
    }

    @Override
    public synchronized int compareTo(AlertListItem o) {
        if (this == o) {
            return 0;
        }
        if (null == o) {
            return 1;
        }

        LocalDate a = o.alertDate;
        LocalDate e = o.eventDate;
        int result;
        if (null == a || null == e) {
            if (null != eventDate) {
                return 1;
            }
        } else {
            if (null == eventDate) {
                if (null == alertDate) {
                    return -1;
                }
                if ((result = alertDate.compareTo(a)) != 0) {
                    return result;
                }
            } else if ((result = eventDate.compareTo(e)) != 0 || (null != alertDate && (result = alertDate.compareTo(a)) != 0)) {
                return result;
            }
        }
        if (o.assessment) {
            if (!assessment) {
                return -1;
            }

            if ((result = AssessmentStatusConverter.compare(assessmentStatus, o.assessmentStatus)) != 0 ||
                    (result = AssessmentTypeConverter.compare(type, o.type)) != 0) {
                return result;
            }
        } else {
            if (assessment) {
                return 1;
            }
            if ((result = CourseStatusConverter.compare(courseStatus, o.courseStatus)) != 0) {
                return result;
            }
        }

        if ((result = code.compareTo(o.code)) != 0 || ((result = title.compareTo(o.title)) != 0) || (result = Long.compare(courseId, o.courseId)) != 0) {
            return result;
        }
        return Long.compare(getId(), o.getId());
    }
}
