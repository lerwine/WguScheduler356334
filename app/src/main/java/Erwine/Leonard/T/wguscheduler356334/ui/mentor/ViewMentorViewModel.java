package Erwine.Leonard.T.wguscheduler356334.ui.mentor;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.Objects;

import Erwine.Leonard.T.wguscheduler356334.db.DbLoader;
import Erwine.Leonard.T.wguscheduler356334.entity.MentorEntity;
import io.reactivex.Completable;
import io.reactivex.Single;

public class ViewMentorViewModel extends AndroidViewModel {
    private final MutableLiveData<MentorEntity> liveData;
    private final DbLoader dbLoader;

    public ViewMentorViewModel(@NonNull Application application) {
        super(application);
        dbLoader = DbLoader.getInstance(getApplication());
        liveData = new MutableLiveData<>();
    }

    public MutableLiveData<MentorEntity> getLiveData() {
        return liveData;
    }

    public Completable saveNotes(String notes) {
        MentorEntity entity = Objects.requireNonNull(liveData.getValue());
        entity.setNotes(notes);
        return dbLoader.saveMentor(entity, true);
    }

    public Completable saveName(String name) {
        MentorEntity entity = Objects.requireNonNull(liveData.getValue());
        entity.setName(name);
        return dbLoader.saveMentor(entity, true);
    }

    public Single<MentorEntity> load(int id) {
        return dbLoader.getMentorById(id, true).doAfterSuccess(liveData::postValue);
    }

    public Completable delete() {
        return dbLoader.deleteMentor(liveData.getValue(), true);
    }
}