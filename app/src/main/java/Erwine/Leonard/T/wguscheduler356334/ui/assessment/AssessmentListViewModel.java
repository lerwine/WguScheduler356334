package Erwine.Leonard.T.wguscheduler356334.ui.assessment;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import Erwine.Leonard.T.wguscheduler356334.MainActivity;
import Erwine.Leonard.T.wguscheduler356334.db.DbLoader;
import Erwine.Leonard.T.wguscheduler356334.entity.assessment.AssessmentEntity;

public final class AssessmentListViewModel extends AndroidViewModel {

    private static final String LOG_TAG = MainActivity.getLogTag(AssessmentListViewModel.class);
    private final DbLoader dbLoader;
    private LiveData<List<AssessmentEntity>> assessments;
    private long courseId;

    public AssessmentListViewModel(@NonNull Application application) {
        super(application);
        Log.d(LOG_TAG, "Constructing");
        dbLoader = DbLoader.getInstance(application.getApplicationContext());
    }

    @Override
    protected void onCleared() {
        Log.d(LOG_TAG, "Enter onCleared");
        super.onCleared();
    }

    public LiveData<List<AssessmentEntity>> getAssessments() {
        return assessments;
    }

    public long getId() {
        return courseId;
    }

    public void setId(long id) {
        courseId = id;
        assessments = dbLoader.getAssessmentsLiveDataByCourseId(courseId);
    }

}
