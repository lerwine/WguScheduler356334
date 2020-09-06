package Erwine.Leonard.T.wguscheduler356334.ui.term;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.time.LocalDate;

import Erwine.Leonard.T.wguscheduler356334.db.DbLoader;
import Erwine.Leonard.T.wguscheduler356334.entity.TermEntity;
import io.reactivex.Completable;
import io.reactivex.Single;

public class EditTermViewModel extends AndroidViewModel {

    private final MutableLiveData<TermEntity> liveData;
    private final DbLoader dbLoader;

    public EditTermViewModel(@NonNull Application application) {
        super(application);
        dbLoader = DbLoader.getInstance(getApplication());
        liveData = new MutableLiveData<>();
    }

    public MutableLiveData<TermEntity> getLiveData() {
        return liveData;
    }

    public Completable save(String name, LocalDate start, LocalDate end, String notes) {
        TermEntity entity = liveData.getValue();
        if (null == entity) {
            entity = new TermEntity(name, start, end, notes);
        } else {
            entity.setName(name);
            entity.setStart(start);
            entity.setEnd(end);
            entity.setNotes(notes);
        }
        return dbLoader.saveTerm(entity);
    }

    public Single<TermEntity> load(int id) {
        return dbLoader.getTermById(id).doAfterSuccess(liveData::postValue);
    }

    public Completable delete() {
        return dbLoader.deleteTerm(liveData.getValue());
    }

}
