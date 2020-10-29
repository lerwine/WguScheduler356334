package Erwine.Leonard.T.wguscheduler356334.ui.alert;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.time.LocalDate;
import java.util.List;

import Erwine.Leonard.T.wguscheduler356334.MainActivity;
import Erwine.Leonard.T.wguscheduler356334.db.DbLoader;
import Erwine.Leonard.T.wguscheduler356334.entity.alert.AlertListItem;
import Erwine.Leonard.T.wguscheduler356334.util.WguSchedulerViewModel;

public class AllAlertsListViewModel extends WguSchedulerViewModel {

    private static final String LOG_TAG = MainActivity.getLogTag(AllAlertsListViewModel.class);
    private final DbLoader dbLoader;
    private LiveData<List<AlertListItem>> liveData;
    private int position = -1;

    public AllAlertsListViewModel(@NonNull Application application) {
        super(application);
        dbLoader = DbLoader.getInstance(application.getApplicationContext());
        liveData = new MutableLiveData<>();
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position, Observer<List<AlertListItem>> observer, LifecycleOwner viewLifecycleOwner) {
        if (position != this.position) {
            Log.d(LOG_TAG, "Enter setPosition(position: " + position + ", observer, viewLifecycleOwner); this.position = " + this.position);
            this.position = position;
            liveData.removeObservers(viewLifecycleOwner);
            switch (position) {
                case 1:
                    liveData = dbLoader.getActiveAlertsAfterDate(LocalDate.now());
                    break;
                case 2:
                    liveData = dbLoader.getActiveAlertsBeforeDate(LocalDate.now());
                    break;
                default:
                    liveData = dbLoader.getActiveAlertsOnDate(LocalDate.now());
                    break;
            }
            liveData.observe(viewLifecycleOwner, observer);
        }
    }
}
