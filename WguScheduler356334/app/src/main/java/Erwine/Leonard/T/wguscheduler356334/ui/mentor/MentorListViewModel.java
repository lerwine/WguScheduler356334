package Erwine.Leonard.T.wguscheduler356334.ui.mentor;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import Erwine.Leonard.T.wguscheduler356334.MainActivity;
import Erwine.Leonard.T.wguscheduler356334.db.DbLoader;
import Erwine.Leonard.T.wguscheduler356334.entity.mentor.MentorListItem;

public class MentorListViewModel extends AndroidViewModel {

    private static final String LOG_TAG = MainActivity.getLogTag(MentorListViewModel.class);
    @SuppressWarnings("FieldCanBeLocal")
    private final DbLoader dbLoader;
    private final LiveData<List<MentorListItem>> mentors;

    public MentorListViewModel(@NonNull Application application) {
        super(application);
        Log.d(LOG_TAG, "Constructing");
        dbLoader = DbLoader.getInstance(application.getApplicationContext());
        mentors = dbLoader.getAllMentors();
    }

    @Override
    protected void onCleared() {
        Log.d(LOG_TAG, "Enter onCleared");
        super.onCleared();
    }

    public LiveData<List<MentorListItem>> getMentors() {
        return mentors;
    }
}