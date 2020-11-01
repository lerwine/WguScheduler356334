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
import Erwine.Leonard.T.wguscheduler356334.entity.assessment.AssessmentAlert;
import Erwine.Leonard.T.wguscheduler356334.entity.assessment.AssessmentDetails;
import Erwine.Leonard.T.wguscheduler356334.util.WguSchedulerViewModel;

public class AssessmentAlertListViewModel extends WguSchedulerViewModel {

    private static final String LOG_TAG = MainActivity.getLogTag(AssessmentAlertListViewModel.class);
    private final DbLoader dbLoader;
    private final Observer<List<AssessmentAlert>> alertsLoadedObserver;
    private final MutableLiveData<List<AssessmentAlert>> liveData;
    private LiveData<List<AssessmentAlert>> observed;
    private AssessmentDetails assessment;
    private LocalDate goalDate;
    private LocalDate completionDate;
    private List<AssessmentAlert> assessmentAlerts;

    public AssessmentAlertListViewModel(@NonNull Application application) {
        super(application);
        dbLoader = DbLoader.getInstance(application.getApplicationContext());
        liveData = new MutableLiveData<>();
        alertsLoadedObserver = this::onAlertsLoaded;
    }

    public LiveData<List<AssessmentAlert>> getLiveData() {
        return liveData;
    }

    public AssessmentDetails getAssessment() {
        return assessment;
    }

    public void setAssessment(@NonNull AssessmentDetails assessment, @NonNull LifecycleOwner viewLifecycleOwner) {
        this.assessment = assessment;
        if (null != observed) {
            observed.removeObserver(alertsLoadedObserver);
        }
        observed = dbLoader.getAlertsByAssessmentId(assessment.getId());
        observed.observe(viewLifecycleOwner, alertsLoadedObserver);
    }

    public LocalDate getGoalDate() {
        return goalDate;
    }

    public synchronized boolean setGoalDate(LocalDate goalDate) {
        if (Objects.equals(this.goalDate, goalDate)) {
            return false;
        }
        this.goalDate = goalDate;
        return recalculateAll();
    }

    public LocalDate getCompletionDate() {
        return completionDate;
    }

    public synchronized boolean setCompletionDate(LocalDate completionDate) {
        if (Objects.equals(this.completionDate, completionDate)) {
            return false;
        }
        this.completionDate = completionDate;
        return recalculateAll();
    }

    private void onAlertsLoaded(@NonNull List<AssessmentAlert> assessmentAlerts) {
        assessmentAlerts.forEach(t -> t.calculate(this));
        this.assessmentAlerts = assessmentAlerts;
        liveData.postValue(assessmentAlerts);
    }

    private boolean recalculateAll() {
        if (null == assessmentAlerts || assessmentAlerts.isEmpty()) {
            return false;
        }
        Iterator<AssessmentAlert> iterator = assessmentAlerts.iterator();
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
