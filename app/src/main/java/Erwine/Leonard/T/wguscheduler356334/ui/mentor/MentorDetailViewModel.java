package Erwine.Leonard.T.wguscheduler356334.ui.mentor;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import Erwine.Leonard.T.wguscheduler356334.db.DbLoader;
import Erwine.Leonard.T.wguscheduler356334.entity.MentorEntity;

public class MentorDetailViewModel extends AndroidViewModel {

    private final LiveData<MentorEntity> mLiveData;
    private final DbLoader dbLoader;

    public MentorDetailViewModel(@NonNull Application application) {
        super(application);
        dbLoader = DbLoader.getInstance(getApplication());
        mLiveData = dbLoader.getEditedMentor().getLiveData();
    }

    public LiveData<MentorEntity> getLiveData() {
        return mLiveData;
    }

}
