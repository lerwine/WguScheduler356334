package Erwine.Leonard.T.wguscheduler356334.ui.term;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import java.util.List;

import Erwine.Leonard.T.wguscheduler356334.MainActivity;
import Erwine.Leonard.T.wguscheduler356334.db.DbLoader;
import Erwine.Leonard.T.wguscheduler356334.entity.term.TermListItem;
import Erwine.Leonard.T.wguscheduler356334.util.WguSchedulerViewModel;

public class TermListViewModel extends WguSchedulerViewModel {

    private static final String LOG_TAG = MainActivity.getLogTag(TermListViewModel.class);
    @SuppressWarnings("FieldCanBeLocal")
    private final DbLoader dbLoader;
    private final LiveData<List<TermListItem>> terms;

    public TermListViewModel(@NonNull Application application) {
        super(application);
        dbLoader = DbLoader.getInstance(application.getApplicationContext());
        terms = dbLoader.getAllTerms();
    }

    public LiveData<List<TermListItem>> getTerms() {
        return terms;
    }

}