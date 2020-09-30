package Erwine.Leonard.T.wguscheduler356334.ui.course;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import java.util.List;

import Erwine.Leonard.T.wguscheduler356334.db.DbLoader;
import Erwine.Leonard.T.wguscheduler356334.entity.course.TermCourseListItem;

public class TermCourseListViewModel extends CourseListViewModel<TermCourseListItem> {
    public TermCourseListViewModel(@NonNull Application application) {
        super(application);
    }

    @Override
    protected LiveData<List<TermCourseListItem>> getLiveData(@NonNull DbLoader dbLoader, long id) {
        return dbLoader.getCoursesByTermId(id);
    }
}
