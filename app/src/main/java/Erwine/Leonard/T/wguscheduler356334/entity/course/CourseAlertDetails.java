package Erwine.Leonard.T.wguscheduler356334.entity.course;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.room.Relation;

import java.util.Objects;

import Erwine.Leonard.T.wguscheduler356334.entity.IdIndexedEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.alert.AlertEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.alert.AlertLink;

public class CourseAlertDetails extends CourseAlert {

    @Relation(
            parentColumn = AlertLink.COLNAME_TARGET_ID,
            entityColumn = Course.COLNAME_ID
    )
    @NonNull
    private CourseEntity course;

    public CourseAlertDetails(@NonNull CourseAlertLink link, @NonNull AlertEntity alert, @NonNull CourseEntity course) {
        super(link, alert);
        this.course = course;
    }

    public CourseAlertDetails(@NonNull CourseAlertDetails courseAlert) {
        super(courseAlert);
        course = new CourseEntity(courseAlert.course);
    }

    @NonNull
    public CourseEntity getCourse() {
        return course;
    }

    public synchronized void setCourse(@NonNull CourseEntity course) {
        getLink().setTargetId(IdIndexedEntity.assertNotNewId(course.getId()));
        this.course = course;
    }

    @Override
    public synchronized void setLink(@NonNull CourseAlertLink link) {
        if (!Objects.equals(link.getTargetId(), course.getId())) {
            throw new IllegalArgumentException();
        }
        super.setLink(link);
    }

    public void saveState(Bundle outState, boolean isOriginal) {
        course.saveState(outState, isOriginal);
        getAlert().saveState(outState, isOriginal);
    }

    public void restoreState(Bundle state, boolean isOriginal) {
        course.restoreState(state, isOriginal);
        AlertEntity alert = getAlert();
        alert.restoreState(state, isOriginal);
        CourseAlertLink link = getLink();
        link.setAlertId(alert.getId());
        link.setTargetId(course.getId());
    }
}
