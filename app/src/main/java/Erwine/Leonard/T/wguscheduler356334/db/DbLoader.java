package Erwine.Leonard.T.wguscheduler356334.db;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import Erwine.Leonard.T.wguscheduler356334.util.SampleData;

public class DbLoader {
    private static DbLoader instance;
    private final AppDb appDb;
    private LiveData<List<TermEntity>> terms;
    private Executor dataExecutor = Executors.newSingleThreadExecutor();

    public DbLoader(Context context) {
        appDb = AppDb.getInstance(context);
    }

    public static DbLoader getInstance(Context context) {
        if (null == instance) {
            instance = new DbLoader(context);
        }

        return instance;
    }

    public void insertTerm(TermEntity viewModel) {
        dataExecutor.execute(() -> appDb.termDAO().insertTerm(viewModel));
    }

    public void insertAllTerms(List<TermEntity> list) {
        dataExecutor.execute(() -> appDb.termDAO().insertAll(list));
    }

    public LiveData<List<TermEntity>> getTerms() {
        if (null == terms) {
            terms = appDb.termDAO().getAll();
        }
        return terms;
    }

    public void loadTermById(int id, MutableLiveData<TermEntity> liveData) {
        dataExecutor.execute(() -> {
            TermEntity entity = appDb.termDAO().getTermById(id);
            liveData.postValue(entity);
        });
    }

    public void deleteTerm(TermEntity viewModel) {
        dataExecutor.execute(() -> appDb.termDAO().deleteTerm(viewModel));
    }

}
