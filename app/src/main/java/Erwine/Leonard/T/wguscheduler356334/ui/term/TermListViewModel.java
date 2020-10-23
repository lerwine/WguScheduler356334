package Erwine.Leonard.T.wguscheduler356334.ui.term;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import Erwine.Leonard.T.wguscheduler356334.MainActivity;
import Erwine.Leonard.T.wguscheduler356334.db.DbLoader;
import Erwine.Leonard.T.wguscheduler356334.entity.term.TermListItem;

public class TermListViewModel extends AndroidViewModel {

    private static final String LOG_TAG = MainActivity.getLogTag(TermListViewModel.class);
    @SuppressWarnings("FieldCanBeLocal")
    private final DbLoader dbLoader;
    private final LiveData<List<TermListItem>> terms;

    public TermListViewModel(@NonNull Application application) {
        super(application);
        Log.d(LOG_TAG, "Constructing");
        dbLoader = DbLoader.getInstance(application.getApplicationContext());
        terms = dbLoader.getAllTerms();
    }

    @Override
    protected void onCleared() {
        Log.d(LOG_TAG, "Enter onCleared");
        super.onCleared();
    }

    public LiveData<List<TermListItem>> getTerms() {
        return terms;
    }

}