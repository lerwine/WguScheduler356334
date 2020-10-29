package Erwine.Leonard.T.wguscheduler356334.ui.alert;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import Erwine.Leonard.T.wguscheduler356334.MainActivity;
import Erwine.Leonard.T.wguscheduler356334.db.DbLoader;
import Erwine.Leonard.T.wguscheduler356334.entity.course.CourseAlert;
import Erwine.Leonard.T.wguscheduler356334.entity.course.CourseDetails;
import Erwine.Leonard.T.wguscheduler356334.util.WguSchedulerViewModel;

public class CourseAlertListViewModel extends WguSchedulerViewModel {

    private static final String LOG_TAG = MainActivity.getLogTag(CourseAlertListViewModel.class);
    private final DbLoader dbLoader;
    private final Observer<List<CourseAlert>> alertsLoadedObserver;
    private final MutableLiveData<List<CourseAlert>> liveData;
    private LiveData<List<CourseAlert>> observed;
    private CourseDetails course;
    private LocalDate effectiveStartDate;
    private LocalDate effectiveEndDate;
    private List<CourseAlert> courseAlerts;

    public CourseAlertListViewModel(@NonNull Application application) {
        super(application);
        dbLoader = DbLoader.getInstance(application.getApplicationContext());
        liveData = new MutableLiveData<>();
        alertsLoadedObserver = this::onAlertsLoaded;
    }

    public LiveData<List<CourseAlert>> getLiveData() {
        return liveData;
    }

    public CourseDetails getCourse() {
        return course;
    }

    public void setCourse(CourseDetails course, LifecycleOwner viewLifecycleOwner) {
        this.course = course;
        if (null != observed) {
            observed.removeObserver(alertsLoadedObserver);
        }
        observed = dbLoader.getAlertsByCourseId(course.getId());
        observed.observe(viewLifecycleOwner, alertsLoadedObserver);
    }

    public LocalDate getEffectiveStartDate() {
        return effectiveStartDate;
    }

    public synchronized boolean setEffectiveStartDate(LocalDate effectiveStartDate) {
        if (Objects.equals(this.effectiveStartDate, effectiveStartDate)) {
            return false;
        }
        this.effectiveStartDate = effectiveStartDate;
        return recalculateAll();
    }

    public LocalDate getEffectiveEndDate() {
        return effectiveEndDate;
    }

    public synchronized boolean setEffectiveEndDate(LocalDate effectiveEndDate) {
        if (Objects.equals(this.effectiveEndDate, effectiveEndDate)) {
            return false;
        }
        this.effectiveEndDate = effectiveEndDate;
        return recalculateAll();
    }

    private synchronized void onAlertsLoaded(List<CourseAlert> courseAlerts) {
        courseAlerts.forEach(t -> t.calculate(this));
        this.courseAlerts = courseAlerts;
        liveData.postValue(courseAlerts);
    }

    private boolean recalculateAll() {
        if (null == courseAlerts || courseAlerts.isEmpty()) {
            return false;
        }
        Iterator<CourseAlert> iterator = courseAlerts.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().reCalculate(this)) {
                while (iterator.hasNext()) {
                    iterator.next().reCalculate(this);
                }
                return true;
            }
        }
        return false;
    }

}
