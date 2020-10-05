package Erwine.Leonard.T.wguscheduler356334.util.live;

import androidx.lifecycle.LiveData;

public class PlaceHolderLiveData<T> extends LiveData<T> {
    public PlaceHolderLiveData() {
    }

    public PlaceHolderLiveData(T value) {
        super(value);
    }
}
