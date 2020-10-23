package Erwine.Leonard.T.wguscheduler356334.db;

import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.util.Log;

import androidx.annotation.NonNull;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import Erwine.Leonard.T.wguscheduler356334.MainActivity;
import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.entity.alert.Alert;
import Erwine.Leonard.T.wguscheduler356334.entity.alert.AlertEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.assessment.Assessment;
import Erwine.Leonard.T.wguscheduler356334.entity.assessment.AssessmentAlert;
import Erwine.Leonard.T.wguscheduler356334.entity.assessment.AssessmentEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.assessment.AssessmentStatus;
import Erwine.Leonard.T.wguscheduler356334.entity.assessment.AssessmentType;
import Erwine.Leonard.T.wguscheduler356334.entity.course.Course;
import Erwine.Leonard.T.wguscheduler356334.entity.course.CourseAlert;
import Erwine.Leonard.T.wguscheduler356334.entity.course.CourseEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.course.CourseStatus;
import Erwine.Leonard.T.wguscheduler356334.entity.mentor.Mentor;
import Erwine.Leonard.T.wguscheduler356334.entity.mentor.MentorEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.term.Term;
import Erwine.Leonard.T.wguscheduler356334.entity.term.TermEntity;
import io.reactivex.functions.Action;

public class SampleDataLoader implements Action {

    private static final String LOG_TAG = MainActivity.getLogTag(SampleDataLoader.class);

    private static final String ELEMENT_NAME_SAMPLE_DATA = "SampleData";
    private static final String ELEMENT_NAME_ITEM = "item";
    private static final String NAMESPACE_SAMPLE_DATA = "";

    private final DbLoader dbLoader;
    private final XmlResourceParser xmlParser;

    SampleDataLoader(DbLoader dbLoader, Resources resources) {
        this.dbLoader = dbLoader;
        xmlParser = resources.getXml(R.xml.sample_data);
    }

    private LocalDate getAttributeLocalDate(@NonNull String name) {
        String s = xmlParser.getAttributeValue(null, name);
        return LocalDateConverter.fromString(s);
    }

    private LocalDate getAttributeLocalDate(@NonNull String name, @NonNull CourseEntity courseEntity) {
        String s = xmlParser.getAttributeValue(null, name);
        if (null == s) {
            return null;
        }
        switch (s) {
            case Course.COLNAME_EXPECTED_START:
                return courseEntity.getExpectedStart();
            case Course.COLNAME_ACTUAL_START:
                return courseEntity.getActualStart();
            case Course.COLNAME_EXPECTED_END:
                return courseEntity.getExpectedEnd();
            case Course.COLNAME_ACTUAL_END:
                return courseEntity.getActualEnd();
            default:
                return LocalDateConverter.fromString(s);
        }
    }

    private Optional<String> getText() throws XmlPullParserException, IOException {
        switch (xmlParser.getEventType()) {
            case XmlPullParser.START_DOCUMENT:
                xmlParser.next();
                return getText();
            case XmlPullParser.ENTITY_REF:
            case XmlPullParser.CDSECT:
            case XmlPullParser.TEXT:
                break;
            case XmlPullParser.START_TAG:
                if (xmlParser.isEmptyElementTag()) {
                    xmlParser.nextTag();
                    return Optional.empty();
                }
                xmlParser.next();
                return getText();
            case XmlPullParser.END_TAG:
            case XmlPullParser.END_DOCUMENT:
                return Optional.empty();
            default:
                xmlParser.next();
                return Optional.empty();
        }
        String text = xmlParser.getText();
        if (xmlParser.next() == XmlPullParser.END_TAG) {
            return Optional.ofNullable(text);
        }
        return Optional.ofNullable(getText().map(t -> text + t).orElse(text));
    }

    private String getItemLines() throws IOException, XmlPullParserException {
        if (xmlParser.nextTag() != XmlPullParser.START_TAG) {
            return "";
        }
        xmlParser.require(XmlPullParser.START_TAG, NAMESPACE_SAMPLE_DATA, ELEMENT_NAME_ITEM);
        String text = getText().orElse("");
        xmlParser.require(XmlPullParser.END_TAG, NAMESPACE_SAMPLE_DATA, ELEMENT_NAME_ITEM);
        if (xmlParser.nextTag() != XmlPullParser.START_TAG) {
            return text;
        }
        StringBuilder sb = new StringBuilder(text);
        do {
            xmlParser.require(XmlPullParser.START_TAG, NAMESPACE_SAMPLE_DATA, ELEMENT_NAME_ITEM);
            getText().ifPresent(t -> sb.append('\n').append(t));
            xmlParser.require(XmlPullParser.END_TAG, NAMESPACE_SAMPLE_DATA, ELEMENT_NAME_ITEM);
        } while (xmlParser.nextTag() == XmlPullParser.START_TAG);
        return sb.toString();
    }

    @Override
    public void run() throws IOException, XmlPullParserException {
        dbLoader.resetDb();
        // Move to /SampleData/mentors tag
        xmlParser.next();
        xmlParser.next();
        xmlParser.require(XmlPullParser.START_TAG, NAMESPACE_SAMPLE_DATA, ELEMENT_NAME_SAMPLE_DATA);
        xmlParser.nextTag();
        xmlParser.require(XmlPullParser.START_TAG, NAMESPACE_SAMPLE_DATA, AppDb.TABLE_NAME_MENTORS);
        HashMap<String, MentorEntity> mentorMap = new HashMap<>();
        while (xmlParser.nextTag() == XmlPullParser.START_TAG) {
            addSampleMentor(mentorMap);
            xmlParser.require(XmlPullParser.END_TAG, NAMESPACE_SAMPLE_DATA, ELEMENT_NAME_ITEM);
        }
        xmlParser.require(XmlPullParser.END_TAG, NAMESPACE_SAMPLE_DATA, AppDb.TABLE_NAME_MENTORS);

        ArrayList<MentorEntity> mentors = new ArrayList<>(mentorMap.values());
        List<Long> ids = dbLoader.getAppDb().mentorDAO().insertAllSynchronous(mentors);
        for (int i = 0; i < ids.size(); i++) {
            mentors.get(i).setId(ids.get(i));
        }
        if (xmlParser.nextTag() == XmlPullParser.START_TAG) {
            xmlParser.require(XmlPullParser.START_TAG, NAMESPACE_SAMPLE_DATA, AppDb.TABLE_NAME_TERMS);
            while (xmlParser.nextTag() == XmlPullParser.START_TAG) {
                loadSampleTerm(mentorMap);
                xmlParser.require(XmlPullParser.END_TAG, NAMESPACE_SAMPLE_DATA, ELEMENT_NAME_ITEM);
            }
            xmlParser.require(XmlPullParser.END_TAG, NAMESPACE_SAMPLE_DATA, AppDb.TABLE_NAME_TERMS);
            xmlParser.nextTag();
        }
        xmlParser.require(XmlPullParser.END_TAG, NAMESPACE_SAMPLE_DATA, ELEMENT_NAME_SAMPLE_DATA);
    }

    private void addSampleMentor(@NonNull HashMap<String, MentorEntity> mentorMap) throws IOException, XmlPullParserException {
        xmlParser.require(XmlPullParser.START_TAG, NAMESPACE_SAMPLE_DATA, ELEMENT_NAME_ITEM);
        MentorEntity mentorEntity = new MentorEntity(Objects.requireNonNull(xmlParser.getAttributeValue(null, Mentor.COLNAME_NAME)), "", "", "");
        mentorMap.put(Objects.requireNonNull(xmlParser.getAttributeValue(null, Mentor.COLNAME_ID)), mentorEntity);
        if (xmlParser.nextTag() != XmlPullParser.START_TAG) {
            Log.d(LOG_TAG, String.format("Loaded %s", mentorEntity));
            return;
        }
        if (xmlParser.getNamespace().equals(NAMESPACE_SAMPLE_DATA) && xmlParser.getName().equals(Mentor.COLNAME_NOTES)) {
            getText().ifPresent(mentorEntity::setNotes);
            xmlParser.require(XmlPullParser.END_TAG, NAMESPACE_SAMPLE_DATA, Mentor.COLNAME_NOTES);
            if (xmlParser.nextTag() != XmlPullParser.START_TAG) {
                Log.d(LOG_TAG, String.format("Loaded %s", mentorEntity));
                return;
            }
        }
        if (xmlParser.getNamespace().equals(NAMESPACE_SAMPLE_DATA) && xmlParser.getName().equals(Mentor.COLNAME_PHONE_NUMBER)) {
            mentorEntity.setPhoneNumber(getItemLines());
            xmlParser.require(XmlPullParser.END_TAG, NAMESPACE_SAMPLE_DATA, Mentor.COLNAME_PHONE_NUMBER);
            if (xmlParser.nextTag() != XmlPullParser.START_TAG) {
                Log.d(LOG_TAG, String.format("Loaded %s", mentorEntity));
                return;
            }
        }
        if (xmlParser.getNamespace().equals(NAMESPACE_SAMPLE_DATA) && xmlParser.getName().equals(Mentor.COLNAME_EMAIL_ADDRESS)) {
            mentorEntity.setEmailAddress(getItemLines());
            xmlParser.require(XmlPullParser.END_TAG, NAMESPACE_SAMPLE_DATA, Mentor.COLNAME_EMAIL_ADDRESS);
            xmlParser.nextTag();
        }
        Log.d(LOG_TAG, String.format("Loaded %s", mentorEntity));
    }

    private void loadSampleTerm(@NonNull HashMap<String, MentorEntity> mentorMap) throws IOException, XmlPullParserException {
        xmlParser.require(XmlPullParser.START_TAG, NAMESPACE_SAMPLE_DATA, ELEMENT_NAME_ITEM);
        TermEntity termEntity = new TermEntity(Objects.requireNonNull(xmlParser.getAttributeValue(null, Mentor.COLNAME_NAME)),
                getAttributeLocalDate(Term.COLNAME_START), getAttributeLocalDate(Term.COLNAME_END), "");
        if (xmlParser.nextTag() != XmlPullParser.START_TAG) {
            Log.d(LOG_TAG, String.format("Loaded %s", termEntity));
            dbLoader.getAppDb().termDAO().insertSynchronous(termEntity);
            return;
        }
        if (xmlParser.getNamespace().equals(NAMESPACE_SAMPLE_DATA) && xmlParser.getName().equals(Term.COLNAME_NOTES)) {
            getText().ifPresent(termEntity::setNotes);
            xmlParser.require(XmlPullParser.END_TAG, NAMESPACE_SAMPLE_DATA, Term.COLNAME_NOTES);
            if (xmlParser.nextTag() != XmlPullParser.START_TAG) {
                Log.d(LOG_TAG, String.format("Loaded %s", termEntity));
                dbLoader.getAppDb().termDAO().insertSynchronous(termEntity);
                return;
            }
        }
        Log.d(LOG_TAG, String.format("Loaded %s", termEntity));
        long termId = dbLoader.getAppDb().termDAO().insertSynchronous(termEntity);
        xmlParser.require(XmlPullParser.START_TAG, NAMESPACE_SAMPLE_DATA, AppDb.TABLE_NAME_COURSES);
        while (xmlParser.nextTag() == XmlPullParser.START_TAG) {
            loadSampleCourse(termId, mentorMap);
            xmlParser.require(XmlPullParser.END_TAG, NAMESPACE_SAMPLE_DATA, ELEMENT_NAME_ITEM);
        }
        xmlParser.require(XmlPullParser.END_TAG, NAMESPACE_SAMPLE_DATA, AppDb.TABLE_NAME_COURSES);
        xmlParser.nextTag();
    }

    private void loadSampleCourse(long termId, @NonNull HashMap<String, MentorEntity> mentorMap) throws IOException, XmlPullParserException {
        xmlParser.require(XmlPullParser.START_TAG, NAMESPACE_SAMPLE_DATA, ELEMENT_NAME_ITEM);
        String mentorId = xmlParser.getAttributeValue(null, Course.COLNAME_MENTOR_ID);
        CourseEntity courseEntity = new CourseEntity(Objects.requireNonNull(xmlParser.getAttributeValue(null, Course.COLNAME_NUMBER)),
                Objects.requireNonNull(xmlParser.getAttributeValue(null, Course.COLNAME_TITLE)),
                CourseStatus.valueOf(Objects.requireNonNull(xmlParser.getAttributeValue(null, Course.COLNAME_STATUS))), getAttributeLocalDate(Course.COLNAME_EXPECTED_START),
                getAttributeLocalDate(Course.COLNAME_ACTUAL_START), getAttributeLocalDate(Course.COLNAME_EXPECTED_END), getAttributeLocalDate(Course.COLNAME_ACTUAL_END),
                xmlParser.getAttributeIntValue(null, Course.COLNAME_COMPETENCY_UNITS, 0), "", termId,
                (null == mentorId) ? null : Objects.requireNonNull(mentorMap.get(mentorId)).getId());
        if (xmlParser.nextTag() != XmlPullParser.START_TAG) {
            Log.d(LOG_TAG, String.format("Loaded %s", courseEntity));
            dbLoader.getAppDb().courseDAO().insertSynchronous(courseEntity);
            return;
        }
        if (xmlParser.getNamespace().equals(NAMESPACE_SAMPLE_DATA) && xmlParser.getName().equals(Course.COLNAME_NOTES)) {
            getText().ifPresent(courseEntity::setNotes);
            xmlParser.require(XmlPullParser.END_TAG, NAMESPACE_SAMPLE_DATA, Course.COLNAME_NOTES);
            if (xmlParser.nextTag() != XmlPullParser.START_TAG) {
                dbLoader.getAppDb().courseDAO().insertSynchronous(courseEntity);
                return;
            }
        }
        Log.d(LOG_TAG, String.format("Loaded %s", courseEntity));
        long courseId = dbLoader.getAppDb().courseDAO().insertSynchronous(courseEntity);
        courseEntity.setId(courseId);
        if (xmlParser.getNamespace().equals(NAMESPACE_SAMPLE_DATA) && xmlParser.getName().equals(AppDb.TABLE_NAME_COURSE_ALERTS)) {
            while (xmlParser.nextTag() == XmlPullParser.START_TAG) {
                loadSampleCourseAlert(courseId);
                xmlParser.require(XmlPullParser.END_TAG, NAMESPACE_SAMPLE_DATA, ELEMENT_NAME_ITEM);
            }
            xmlParser.require(XmlPullParser.END_TAG, NAMESPACE_SAMPLE_DATA, AppDb.TABLE_NAME_COURSE_ALERTS);
            if (xmlParser.nextTag() != XmlPullParser.START_TAG) {
                return;
            }
        }
        xmlParser.require(XmlPullParser.START_TAG, NAMESPACE_SAMPLE_DATA, AppDb.TABLE_NAME_ASSESSMENTS);
        while (xmlParser.nextTag() == XmlPullParser.START_TAG) {
            loadSampleAssessment(courseEntity);
            xmlParser.require(XmlPullParser.END_TAG, NAMESPACE_SAMPLE_DATA, ELEMENT_NAME_ITEM);
        }
        xmlParser.require(XmlPullParser.END_TAG, NAMESPACE_SAMPLE_DATA, AppDb.TABLE_NAME_ASSESSMENTS);
        xmlParser.nextTag();
    }

    private void loadSampleCourseAlert(long courseId) throws IOException, XmlPullParserException {
        xmlParser.require(XmlPullParser.START_TAG, NAMESPACE_SAMPLE_DATA, ELEMENT_NAME_ITEM);
        CourseAlert courseAlert = new CourseAlert();
        AlertEntity alert = courseAlert.getAlert();
        alert.setTimeSpec(xmlParser.getAttributeIntValue(null, Alert.COLNAME_TIME_SPEC, 0));
        alert.setSubsequent(xmlParser.getAttributeBooleanValue(null, Alert.COLNAME_SUBSEQUENT, false));
        alert.setCustomMessage(xmlParser.getAttributeValue(null, Alert.COLNAME_CUSTOM_MESSAGE));
        courseAlert.getLink().setTargetId(courseId);
        dbLoader.insertCourseAlertSynchronous(courseAlert);
        xmlParser.nextTag();
    }

    private void loadSampleAssessment(@NonNull CourseEntity courseEntity) throws IOException, XmlPullParserException {
        xmlParser.require(XmlPullParser.START_TAG, NAMESPACE_SAMPLE_DATA, ELEMENT_NAME_ITEM);
        AssessmentEntity assessmentEntity = new AssessmentEntity(Objects.requireNonNull(xmlParser.getAttributeValue(null, Assessment.COLNAME_CODE)),
                xmlParser.getAttributeValue(null, Assessment.COLNAME_NAME),
                AssessmentStatus.valueOf(Objects.requireNonNull(xmlParser.getAttributeValue(null, Assessment.COLNAME_STATUS))),
                getAttributeLocalDate(Assessment.COLNAME_GOAL_DATE, courseEntity), AssessmentType.valueOf(Objects.requireNonNull(xmlParser.getAttributeValue(null, Assessment.COLNAME_TYPE))),
                "", getAttributeLocalDate(Assessment.COLNAME_COMPLETION_DATE, courseEntity), courseEntity.getId());
        if (xmlParser.nextTag() != XmlPullParser.START_TAG) {
            dbLoader.getAppDb().assessmentDAO().insertSynchronous(assessmentEntity);
            return;
        }
        if (xmlParser.getNamespace().equals(NAMESPACE_SAMPLE_DATA) && xmlParser.getName().equals(Assessment.COLNAME_NOTES)) {
            getText().ifPresent(assessmentEntity::setNotes);
            if (xmlParser.nextTag() != XmlPullParser.START_TAG) {
                dbLoader.getAppDb().assessmentDAO().insertSynchronous(assessmentEntity);
                return;
            }
        }
        Log.d(LOG_TAG, String.format("Loaded %s", assessmentEntity));
        long assessmentId = dbLoader.getAppDb().assessmentDAO().insertSynchronous(assessmentEntity);
        xmlParser.require(XmlPullParser.START_TAG, NAMESPACE_SAMPLE_DATA, AppDb.TABLE_NAME_ASSESSMENT_ALERTS);
        while (xmlParser.nextTag() == XmlPullParser.START_TAG) {
            loadSampleAssessmentAlert(assessmentId);
            xmlParser.require(XmlPullParser.END_TAG, NAMESPACE_SAMPLE_DATA, ELEMENT_NAME_ITEM);
        }
        xmlParser.require(XmlPullParser.END_TAG, NAMESPACE_SAMPLE_DATA, AppDb.TABLE_NAME_ASSESSMENT_ALERTS);
        xmlParser.nextTag();
    }

    private void loadSampleAssessmentAlert(long assessmentId) throws IOException, XmlPullParserException {
        xmlParser.require(XmlPullParser.START_TAG, NAMESPACE_SAMPLE_DATA, ELEMENT_NAME_ITEM);
        AssessmentAlert assessmentAlert = new AssessmentAlert();
        AlertEntity alert = assessmentAlert.getAlert();
        alert.setTimeSpec(xmlParser.getAttributeIntValue(null, Alert.COLNAME_TIME_SPEC, 0));
        alert.setSubsequent(xmlParser.getAttributeBooleanValue(null, Alert.COLNAME_SUBSEQUENT, false));
        alert.setCustomMessage(xmlParser.getAttributeValue(null, Alert.COLNAME_CUSTOM_MESSAGE));
        assessmentAlert.getLink().setTargetId(assessmentId);
        dbLoader.insertAssessmentAlertSynchronous(assessmentAlert);
        xmlParser.nextTag();
    }

}
