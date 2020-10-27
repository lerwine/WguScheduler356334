package Erwine.Leonard.T.wguscheduler356334.ui.alert;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
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

public class AssessmentAlertListViewModel extends AndroidViewModel {

    private static final String LOG_TAG = MainActivity.getLogTag(AssessmentAlertListViewModel.class);
    private final DbLoader dbLoader;
    private final Observer<List<AssessmentAlert>> alertsLoadedObserver;
    private final MutableLiveData<List<AssessmentAlert>> liveData;
    private LiveData<List<AssessmentAlert>> observed;
    private AssessmentDetails assessment;
    private LocalDate effectiveStartDate;
    private LocalDate effectiveEndDate;
    private List<AssessmentAlert> assessmentAlerts;

    public AssessmentAlertListViewModel(@NonNull Application application) {
        super(application);
        Log.d(LOG_TAG, "Constructing");
        dbLoader = DbLoader.getInstance(application.getApplicationContext());
        liveData = new MutableLiveData<>();
        alertsLoadedObserver = this::onAlertsLoaded;
    }

    @Override
    protected void onCleared() {
        Log.d(LOG_TAG, "Enter onCleared");
        super.onCleared();
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

    private void onAlertsLoaded(List<AssessmentAlert> assessmentAlerts) {
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
