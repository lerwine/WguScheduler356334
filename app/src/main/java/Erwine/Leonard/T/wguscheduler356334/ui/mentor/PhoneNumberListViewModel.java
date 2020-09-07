package Erwine.Leonard.T.wguscheduler356334.ui.mentor;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import Erwine.Leonard.T.wguscheduler356334.db.DbLoader;
import Erwine.Leonard.T.wguscheduler356334.entity.PhoneNumberEntity;

public class PhoneNumberListViewModel extends AndroidViewModel {
    private final DbLoader dbLoader;
    private final LiveData<List<PhoneNumberEntity>> mLiveData;

    public PhoneNumberListViewModel(@NonNull Application application) {
        super(application);
        dbLoader = DbLoader.getInstance(application.getApplicationContext());
        mLiveData = dbLoader.getPhoneNumbers();
    }

    public LiveData<List<PhoneNumberEntity>> getLiveData() {
        return mLiveData;
    }
}
