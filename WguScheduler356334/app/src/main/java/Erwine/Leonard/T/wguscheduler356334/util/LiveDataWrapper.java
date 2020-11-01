package Erwine.Leonard.T.wguscheduler356334.util;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

public class LiveDataWrapper<T> {
    @NonNull
    private final LiveDataImpl<T> liveData;

    private LiveDataWrapper(@NonNull LiveDataImpl<T> liveData) {
        this.liveData = liveData;
    }

    public LiveDataWrapper(T initialValue) {
        this(new LiveDataImpl<>(initialValue));
    }

    public LiveDataWrapper() {
        this(new LiveDataImpl<>());
    }

    @NonNull
    public LiveData<T> getLiveData() {
        return liveData;
    }

    public void postValue(T value) {
        liveData.postValue(value);
    }

    private static class LiveDataImpl<T> extends LiveData<T> {

        LiveDataImpl() {
            super();
        }

        LiveDataImpl(T initialValue) {
            super(initialValue);
        }

        @Override
        protected void postValue(T value) {
            super.postValue(value);
        }
    }
}
