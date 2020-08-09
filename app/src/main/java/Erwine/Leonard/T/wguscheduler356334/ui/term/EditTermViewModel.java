package Erwine.Leonard.T.wguscheduler356334.ui.term;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Entity;
import androidx.room.Ignore;

import java.time.LocalDate;

import Erwine.Leonard.T.wguscheduler356334.db.DbLoader;
import Erwine.Leonard.T.wguscheduler356334.entity.TermEntity;
import io.reactivex.Completable;
import io.reactivex.Single;

@Entity(tableName = "terms")
public class EditTermViewModel extends AndroidViewModel {

    private MutableLiveData<TermEntity> liveData = new MutableLiveData<>();
    private DbLoader dbLoader;

    //    private MutableLiveData<String> mText;

    @Ignore
    public EditTermViewModel(@NonNull Application application) {
        super(application);
        dbLoader = DbLoader.getInstance(getApplication());
    }

    public MutableLiveData<TermEntity> getLiveData() {
        return liveData;
    }

    public Completable save(String name, LocalDate start, LocalDate end) {
        TermEntity entity = liveData.getValue();
        if (null == entity) {
            entity = new TermEntity(name, start, end);
        } else {
            entity.setName(name);
            entity.setStart(start);
            entity.setEnd(end);
        }
        return dbLoader.saveTerm(entity);
    }

    public Single<TermEntity> load(int id) {
        return dbLoader.getTermById(id).doAfterSuccess((termEntity) -> liveData.postValue(termEntity));
    }

    public Completable delete() {
        return dbLoader.deleteTerm(liveData.getValue());
    }
}
