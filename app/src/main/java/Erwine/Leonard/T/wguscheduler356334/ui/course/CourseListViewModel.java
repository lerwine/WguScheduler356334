package Erwine.Leonard.T.wguscheduler356334.ui.course;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import Erwine.Leonard.T.wguscheduler356334.db.DbLoader;
import Erwine.Leonard.T.wguscheduler356334.entity.AbstractCourseEntity;

public abstract class CourseListViewModel<T extends AbstractCourseEntity<T>> extends AndroidViewModel {

    private final DbLoader dbLoader;
    private LiveData<List<T>> courses;
    private long termId;

    public CourseListViewModel(@NonNull Application application) {
        super(application);
        dbLoader = DbLoader.getInstance(application.getApplicationContext());
    }

    public LiveData<List<T>> getCourses() {
        return courses;
    }

    public long getId() {
        return termId;
    }

    public void setId(long id) {
        courses = getLiveData(dbLoader, id);
    }

    protected abstract LiveData<List<T>> getLiveData(@NonNull DbLoader dbLoader, long id);
}
