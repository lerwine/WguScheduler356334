package Erwine.Leonard.T.wguscheduler356334.ui.assessment;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import Erwine.Leonard.T.wguscheduler356334.db.DbLoader;
import Erwine.Leonard.T.wguscheduler356334.entity.assessment.AssessmentEntity;

public final class AssessmentListViewModel extends AndroidViewModel {

    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("eee, MMM d, YYYY").withZone(ZoneId.systemDefault());
    private final DbLoader dbLoader;
    private LiveData<List<AssessmentEntity>> assessments;
    private long courseId;

    public AssessmentListViewModel(@NonNull Application application) {
        super(application);
        dbLoader = DbLoader.getInstance(application.getApplicationContext());
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
