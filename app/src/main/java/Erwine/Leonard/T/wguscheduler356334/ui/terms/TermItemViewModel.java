package Erwine.Leonard.T.wguscheduler356334.ui.terms;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Entity;
import androidx.room.Ignore;

import java.time.LocalDate;

import Erwine.Leonard.T.wguscheduler356334.db.DbLoader;
import Erwine.Leonard.T.wguscheduler356334.db.TermEntity;

@Entity(tableName = "terms")
public class TermItemViewModel extends AndroidViewModel {

    private MutableLiveData<TermEntity> liveData = new MutableLiveData<>();
    private DbLoader dbLoader;

    //    private MutableLiveData<String> mText;

    @Ignore
    public TermItemViewModel(@NonNull Application application) {
        super(application);
        dbLoader = DbLoader.getInstance(getApplication());
    }

    public MutableLiveData<TermEntity> getLiveData() {
        return liveData;
    }

    public void save(String name, LocalDate start, LocalDate end) {
        TermEntity entity = liveData.getValue();
        if (null == entity) {
            entity = new TermEntity(name, start, end);
        } else {
            entity.setName(name);
            entity.setStart(start);
            entity.setEnd(end);
        }
        dbLoader.insertTerm(entity);
    }

    public void load(int id) {
        dbLoader.loadTermById(id, liveData);
    }

    public void delete() {
        dbLoader.deleteTerm(liveData.getValue());
    }
}
