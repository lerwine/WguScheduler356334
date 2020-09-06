package Erwine.Leonard.T.wguscheduler356334.ui.mentor;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import Erwine.Leonard.T.wguscheduler356334.db.DbLoader;
import Erwine.Leonard.T.wguscheduler356334.entity.MentorEntity;
import Erwine.Leonard.T.wguscheduler356334.util.IndexedStringList;
import io.reactivex.Completable;
import io.reactivex.Single;

public class MentorDetailViewModel extends AndroidViewModel {

    private final MutableLiveData<MentorEntity> liveData;
    private final DbLoader dbLoader;

    public MentorDetailViewModel(@NonNull Application application) {
        super(application);
        dbLoader = DbLoader.getInstance(getApplication());
        liveData = new MutableLiveData<>();
    }

    public MutableLiveData<MentorEntity> getLiveData() {
        return liveData;
    }

    public Completable save(String name, IndexedStringList emailAddresses, IndexedStringList phoneNumbers, String notes) {
        MentorEntity entity = liveData.getValue();
        if (null == entity) {
            entity = new MentorEntity(name, notes, emailAddresses.toString(), phoneNumbers.toString());
        } else {
            entity.setName(name);
            entity.setEmailAddresses(emailAddresses.toString());
            entity.setPhoneNumbers(phoneNumbers.toString());
            entity.setNotes(notes);
        }
        return dbLoader.saveMentor(entity);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void load(int id) {
        Single<MentorEntity> result = dbLoader.getMentorById(id).doAfterSuccess(liveData::postValue);
        result.subscribe(liveData::postValue, throwable -> Log.e(getClass().getName(), "Error loading mentor", throwable));
    }

    public Completable delete() {
        return dbLoader.deleteMentor(liveData.getValue());
    }

}
