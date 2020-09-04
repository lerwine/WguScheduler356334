package Erwine.Leonard.T.wguscheduler356334.ui.mentor;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import Erwine.Leonard.T.wguscheduler356334.db.DbLoader;
import Erwine.Leonard.T.wguscheduler356334.entity.MentorEntity;

public class MentorListViewModel extends AndroidViewModel {
    private final DbLoader dbLoader;
    private final LiveData<List<MentorEntity>> mentors;

    public MentorListViewModel(@NonNull Application application) {
        super(application);
        dbLoader = DbLoader.getInstance(application.getApplicationContext());
        mentors = dbLoader.getAllMentors();
    }

    public LiveData<List<MentorEntity>> getMentors() {
        return mentors;
    }
}
