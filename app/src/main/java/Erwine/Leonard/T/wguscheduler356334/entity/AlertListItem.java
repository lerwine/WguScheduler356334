package Erwine.Leonard.T.wguscheduler356334.entity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.room.ColumnInfo;
import androidx.room.DatabaseView;
import androidx.room.Ignore;

import java.time.LocalDate;
import java.util.Objects;

import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.db.AppDb;
import Erwine.Leonard.T.wguscheduler356334.db.AssessmentStatusConverter;
import Erwine.Leonard.T.wguscheduler356334.db.AssessmentTypeConverter;
import Erwine.Leonard.T.wguscheduler356334.db.CourseStatusConverter;
import Erwine.Leonard.T.wguscheduler356334.entity.assessment.Assessment;
import Erwine.Leonard.T.wguscheduler356334.entity.assessment.AssessmentStatus;
import Erwine.Leonard.T.wguscheduler356334.entity.assessment.AssessmentType;
import Erwine.Leonard.T.wguscheduler356334.entity.course.Course;
import Erwine.Leonard.T.wguscheduler356334.entity.course.CourseStatus;
import Erwine.Leonard.T.wguscheduler356334.util.ToStringBuilder;

@DatabaseView(
        viewName = AppDb.VIEW_NAME_ALERT_LIST_ITEM,
        value = "SELECT courseAlerts.id, courseAlerts.leadTime, courseAlerts.subsequent, 0 AS assessment, CASE courseAlerts.subsequent\n" +
                "\tWHEN 1 THEN\n" +
                "\t\tCASE WHEN courses.actualEnd IS NULL THEN courses.expectedEnd ELSE courses.actualEnd END\n" +
                "\tELSE\n" +
                "\t\tCASE WHEN courses.actualStart IS NULL THEN courses.expectedStart ELSE courses.actualStart END\n" +
                "\tEND AS eventDate, CASE courseAlerts.subsequent\n" +
                "\tWHEN 1 THEN\n" +
                "\t\tCASE\n" +
                "\t\t\tWHEN courses.actualEnd IS NULL THEN\n" +
                "\t\t\t\tCASE WHEN courses.expectedEnd IS NULL THEN NULL ELSE courses.expectedEnd - courseAlerts.leadTime END\n" +
                "\t\t\tELSE\n" +
                "\t\t\t\tcourses.actualEnd - courseAlerts.leadTime\n" +
                "\t\t\tEND\n" +
                "\tELSE CASE\n" +
                "\t\tWHEN courses.actualStart IS NULL THEN\n" +
                "\t\t\tCASE WHEN courses.expectedStart IS NULL THEN NULL ELSE courses.expectedStart - courseAlerts.leadTime END\n" +
                "\t\tELSE\n" +
                "\t\t\tcourses.actualStart - courseAlerts.leadTime\n" +
                "\t\tEND\n" +
                "\tEND AS alertDate, courses.number AS code, courses.title, NULL as type, courses.status, courseAlerts.courseId\n" +
                "\tFROM courseAlerts LEFT JOIN courses ON courseAlerts.courseId=courses.id\n" +
                "UNION SELECT assessmentAlerts.id, assessmentAlerts.leadTime, assessmentAlerts.subsequent, 1 AS assessment,\n" +
                "\tCASE assessmentAlerts.subsequent WHEN 1 THEN assessments.completionDate ELSE assessments.goalDate END AS eventDate, CASE assessmentAlerts.subsequent\n" +
                "\t\tWHEN 1 THEN\n" +
                "\t\t\tCASE WHEN assessments.completionDate IS NULL THEN NULL ELSE assessments.completionDate - assessmentAlerts.leadTime END\n" +
                "\t\tELSE\n" +
                "\t\t\tCASE WHEN assessments.goalDate IS NULL THEN NULL ELSE assessments.goalDate - assessmentAlerts.leadTime END\n" +
                "\t\tEND AS alertDate, assessments.code, CASE WHEN assessments.name IS NULL THEN '' ELSE assessments.name END as title, assessments.type, assessments.status, assessments.courseId\n" +
                "\tFROM assessmentAlerts LEFT JOIN assessments ON assessmentAlerts.assessmentId=assessments.id"
)
public final class AlertListItem extends AbstractAlertEntity<AlertListItem> implements Comparable<AlertListItem> {

    static final String COLNAME_EVENT_DATE = "eventDate";
    static final String COLNAME_ALERT_DATE = "alertDate";
    static final String COLNAME_ASSESSMENT = "assessment";
    static final String COLNAME_CODE = "code";
    static final String COLNAME_TITLE = "title";
    static final String COLNAME_TYPE = "type";
    static final String COLNAME_STATUS = "status";
    static final String COLNAME_COURSE_ID = "courseId";

    @ColumnInfo(name = COLNAME_ASSESSMENT)
    private boolean assessment;
    @ColumnInfo(name = COLNAME_EVENT_DATE)
    private LocalDate eventDate;
    @ColumnInfo(name = COLNAME_ALERT_DATE)
    private LocalDate alertDate;
    @ColumnInfo(name = COLNAME_CODE)
    private String code;
    @ColumnInfo(name = COLNAME_TITLE)
    private String title;
    @ColumnInfo(name = COLNAME_TYPE)
    private AssessmentType type;
    @ColumnInfo(name = COLNAME_STATUS)
    private int status;
    @ColumnInfo(name = COLNAME_COURSE_ID)
    private long courseId;
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

    @Ignore
    protected AlertListItem(Long id, boolean subsequent, int leadTime, boolean assessment, LocalDate eventDate, LocalDate alertDate, String code, String title, int status, AssessmentType type, Long courseId) {
        super(id, subsequent, leadTime);
        this.assessment = assessment;
        this.eventDate = eventDate;
        this.alertDate = alertDate;
        this.code = SINGLE_LINE_NORMALIZER.apply(code);
        this.title = SINGLE_LINE_NORMALIZER.apply(title);
        this.status = status;
        this.courseId = courseId;
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

    public AlertListItem(boolean subsequent, int leadTime, boolean assessment, LocalDate eventDate, LocalDate alertDate, String code, String title, AssessmentType type, int status, Long courseId, long id) {
        this(id, subsequent, leadTime, assessment, eventDate, alertDate, code, title, status, type, courseId);
    }

    @Ignore
    public AlertListItem(AlertListItem source) {
        super(source);
        this.assessment = source.assessment;
        this.eventDate = source.eventDate;
        this.code = source.code;
        this.title = source.title;
        this.type = source.type;
        this.status = source.status;
        this.courseId = source.courseId;
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

    public LocalDate getEventDate() {
        return eventDate;
    }

    public synchronized void setEventDate(LocalDate eventDate) {
        if (!Objects.equals(this.eventDate, eventDate)) {
            this.eventDate = eventDate;
            if (null == eventDate) {
                alertDate = null;
            } else {
                int leadTime = getLeadTime();
                alertDate = (leadTime < 1) ? eventDate : eventDate.minusDays(leadTime);
            }
        }
    }

    public LocalDate getAlertDate() {
        return alertDate;
    }

    public synchronized void setAlertDate(LocalDate alertDate) {
        if (!Objects.equals(this.alertDate, alertDate)) {
            this.alertDate = alertDate;
            if (null == alertDate) {
                eventDate = null;
            } else {
                int leadTime = getLeadTime();
                eventDate = (leadTime < 1) ? alertDate : alertDate.plusDays(leadTime);
            }
        }
    }

    @Override
    public synchronized void setLeadTime(int days) {
        int oldValue = getLeadTime();
        super.setLeadTime(days);
        if ((days = getLeadTime()) != oldValue) {
            alertDate = (days < 1) ? eventDate : eventDate.minusDays(days);
        }
    }

    @NonNull
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = SINGLE_LINE_NORMALIZER.apply(code);
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = SINGLE_LINE_NORMALIZER.apply(title);
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

    @Override
    protected int hashCodeFromProperties() {
        return Objects.hash(courseId, isSubsequent(), getLeadTime(), assessment, eventDate, code, title, type, status);
    }

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
            setEventDate(LocalDate.ofEpochDay(bundle.getLong(key)));
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
        LocalDate d = eventDate;
        if (null != d) {
            bundle.putLong(stateKey(COLNAME_EVENT_DATE, isOriginal), d.toEpochDay());
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
    public void appendPropertiesAsStrings(ToStringBuilder sb) {
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
                return -1;
            }
            if ((result = eventDate.compareTo(e)) != 0 || (result = alertDate.compareTo(a)) != 0) {
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
        Long x = getId();
        Long y = o.getId();
        return (null == x) ? ((null == y) ? 0 : -1) : ((null == y) ? 1 : Long.compare(x, y));
    }
}
