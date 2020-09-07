package Erwine.Leonard.T.wguscheduler356334.ui.mentor;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import Erwine.Leonard.T.wguscheduler356334.db.DbLoader;
import Erwine.Leonard.T.wguscheduler356334.entity.EmailAddressEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.MentorEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.PhoneNumberEntity;
import io.reactivex.Completable;
import io.reactivex.Single;

public class MentorDetailViewModel extends AndroidViewModel {

    private final MutableLiveData<MentorEntity> mLiveData;
    private final DbLoader dbLoader;
    private LiveData<List<PhoneNumberEntity>> phoneNumbers;
    private LiveData<List<EmailAddressEntity>> emailAddresses;

    public MentorDetailViewModel(@NonNull Application application) {
        super(application);
        dbLoader = DbLoader.getInstance(getApplication());
        mLiveData = new MutableLiveData<>();
        phoneNumbers = dbLoader.getPhoneNumbers();
        emailAddresses = dbLoader.getEmailAddresses();
    }

    public MutableLiveData<MentorEntity> getLiveData() {
        return mLiveData;
    }

    public LiveData<List<PhoneNumberEntity>> getPhoneNumbers() {
        return phoneNumbers;
    }

    public LiveData<List<EmailAddressEntity>> getEmailAddresses() {
        return emailAddresses;
    }

    public Completable save(String name, String notes) {
        MentorEntity currentEntity = mLiveData.getValue();
        Completable result;
        if (null == currentEntity) {
            MentorEntity newEntity = new MentorEntity(name, notes, emailAddresses.toString(), phoneNumbers.toString());
            result = dbLoader.saveMentor(newEntity, false).doOnComplete(() -> mLiveData.postValue(newEntity));
        } else {
            currentEntity.setName(name);
            currentEntity.setEmailAddresses(emailAddresses.toString());
            currentEntity.setPhoneNumbers(phoneNumbers.toString());
            currentEntity.setNotes(notes);
            result = dbLoader.saveMentor(currentEntity, false);
        }
        return result.doOnError(throwable -> Log.e(getClass().getName(), "Error saving mentor", throwable));
    }

    public Single<MentorEntity> getEntity(long id) {
        return dbLoader.getMentorById(id, true).doAfterSuccess(mLiveData::postValue)
                .doOnError(throwable -> Log.e(getClass().getName(), "Error loading mentor", throwable));
    }

    public Completable delete() {
        return dbLoader.deleteMentor(mLiveData.getValue(), true).doOnComplete(() -> mLiveData.postValue(null))
                .doOnError(throwable -> Log.e(getClass().getName(), "Error deleting mentor", throwable));
    }

}
