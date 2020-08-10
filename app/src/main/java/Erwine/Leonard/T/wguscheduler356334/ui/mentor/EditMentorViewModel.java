package Erwine.Leonard.T.wguscheduler356334.ui.mentor;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Entity;

import java.util.List;

import Erwine.Leonard.T.wguscheduler356334.db.AppDb;
import Erwine.Leonard.T.wguscheduler356334.db.DbLoader;
import Erwine.Leonard.T.wguscheduler356334.entity.MentorEntity;
import io.reactivex.Completable;
import io.reactivex.Single;

@Entity(tableName = AppDb.TABLE_NAME_MENTORS)
public class EditMentorViewModel extends AndroidViewModel {

    private final MutableLiveData<MentorEntity> liveData;
    private final DbLoader dbLoader;

    public EditMentorViewModel(@NonNull Application application) {
        super(application);
        dbLoader = DbLoader.getInstance(getApplication());
        liveData = new MutableLiveData<>();
    }

    public MutableLiveData<MentorEntity> getLiveData() {
        return liveData;
    }

    public Completable save(String name, String emailAddresses, String phoneNumbers, String notes) {
        MentorEntity entity = liveData.getValue();
        if (null == entity) {
            entity = new MentorEntity(name, notes, emailAddresses, phoneNumbers);
        } else {
            entity.setName(name);
            entity.setEmailAddresses(emailAddresses);
            entity.setPhoneNumbers(phoneNumbers);
            entity.setNotes(notes);
        }
        return dbLoader.saveMentor(entity);
    }

    public Single<MentorEntity> load(int id) {
        return dbLoader.getMentorById(id).doAfterSuccess(liveData::postValue);
    }

    public Completable delete() {
        return dbLoader.deleteMentor(liveData.getValue());
    }

}
