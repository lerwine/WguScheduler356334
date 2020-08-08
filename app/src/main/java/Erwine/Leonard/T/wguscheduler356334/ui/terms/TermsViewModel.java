package Erwine.Leonard.T.wguscheduler356334.ui.terms;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import Erwine.Leonard.T.wguscheduler356334.db.DbLoader;
import Erwine.Leonard.T.wguscheduler356334.db.TermEntity;

public class TermsViewModel extends AndroidViewModel {
    private final DbLoader dbLoader;
    private final LiveData<List<TermEntity>> terms;

    public TermsViewModel(@NonNull Application application) {
        super(application);
        dbLoader = DbLoader.getInstance(application.getApplicationContext());
        terms = dbLoader.getTerms();
    }

    public LiveData<List<TermEntity>> getTerms() {
        return terms;
    }

//    public LiveData<String> getText() {
//        return mText;
//    }
}