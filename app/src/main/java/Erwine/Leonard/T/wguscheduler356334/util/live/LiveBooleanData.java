package Erwine.Leonard.T.wguscheduler356334.util.live;

import androidx.annotation.NonNull;

public class LiveBooleanData extends PostTrackingLiveData<Boolean> {

    public LiveBooleanData(boolean initialValue) {
        super(initialValue);
    }

    public LiveBooleanData() {
        super(false);
    }

    @Override
    protected boolean areNotEqual(@NonNull Boolean a, @NonNull Boolean b) {
        return a != b;
    }

    @Override
    protected synchronized void postValue(Boolean value) {
        super.postValue((null == value) ? false : value);
    }

    @Override
    protected void setValue(Boolean value) {
        super.setValue((null == value) ? false : value);
    }

}
