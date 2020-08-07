package Erwine.Leonard.T.wguscheduler356334.ui.courses;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CoursesViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public CoursesViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Courses list / edit goes here");
    }

    public LiveData<String> getText() {
        return mText;
    }
}