package Erwine.Leonard.T.wguscheduler356334.ui.course;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CourseListViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public CourseListViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Courses list / edit goes here");
    }

    public LiveData<String> getText() {
        return mText;
    }
}