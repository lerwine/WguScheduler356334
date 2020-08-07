package Erwine.Leonard.T.wguscheduler356334.ui.terms;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TermsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public TermsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Terms List/Edit goes here");
    }

    public LiveData<String> getText() {
        return mText;
    }
}