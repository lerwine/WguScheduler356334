package Erwine.Leonard.T.wguscheduler356334.ui.mentor;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import java.util.List;

import Erwine.Leonard.T.wguscheduler356334.MainActivity;
import Erwine.Leonard.T.wguscheduler356334.db.DbLoader;
import Erwine.Leonard.T.wguscheduler356334.entity.mentor.MentorListItem;
import Erwine.Leonard.T.wguscheduler356334.util.WguSchedulerViewModel;

public class MentorListViewModel extends WguSchedulerViewModel {

    private static final String LOG_TAG = MainActivity.getLogTag(MentorListViewModel.class);
    @SuppressWarnings("FieldCanBeLocal")
    private final DbLoader dbLoader;
    private final LiveData<List<MentorListItem>> mentors;

    public MentorListViewModel(@NonNull Application application) {
        super(application);
        dbLoader = DbLoader.getInstance(application.getApplicationContext());
        mentors = dbLoader.getAllMentors();
    }

    public LiveData<List<MentorListItem>> getMentors() {
        return mentors;
    }
}