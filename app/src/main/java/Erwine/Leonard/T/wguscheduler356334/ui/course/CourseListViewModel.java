package Erwine.Leonard.T.wguscheduler356334.ui.course;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import Erwine.Leonard.T.wguscheduler356334.db.DbLoader;
import Erwine.Leonard.T.wguscheduler356334.entity.CourseEntity;

public class CourseListViewModel extends AndroidViewModel {

    private final DbLoader dbLoader;
    private LiveData<List<CourseEntity>> courses;
    private long termId;

    public CourseListViewModel(@NonNull Application application) {
        super(application);
        dbLoader = DbLoader.getInstance(application.getApplicationContext());
    }

    public LiveData<List<CourseEntity>> getCourses() {
        return courses;
    }

    public long getTermId() {
        return termId;
    }

    public void setTermId(long id) {
        courses = dbLoader.getCoursesByTermId(id);
    }
}
