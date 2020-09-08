package Erwine.Leonard.T.wguscheduler356334.ui.mentor;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import Erwine.Leonard.T.wguscheduler356334.db.DbLoader;
import Erwine.Leonard.T.wguscheduler356334.entity.MentorEntity;
import io.reactivex.Single;

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

    public Single<MentorEntity> getEntity(long id) {
        return dbLoader.ensureEditedMentorId(id)
                .doOnError(throwable -> Log.e(getClass().getName(), "Error loading mentor", throwable));
    }

}
