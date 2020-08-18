package Erwine.Leonard.T.wguscheduler356334.util.live;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

public class PostTrackingLiveData<T> extends LiveData<T> {
    private T postedValue;

    public PostTrackingLiveData(T initialValue) {
        super(initialValue);
        postedValue = initialValue;
    }

    protected boolean areNotEqual(@NonNull T a, @NonNull T b) {
        return !a.equals(b);
    }

    @Override
    protected synchronized void postValue(T value) {
        if (setPostedValue(value)) {
            super.postValue(value);
        }
    }

    protected void onPostedValueChanged(T value) {
    }

    public T getPostedValue() {
        return postedValue;
    }

    private synchronized boolean setPostedValue(T value) {
        if ((null == postedValue) ? null != value : null == value || areNotEqual(postedValue, value)) {
            postedValue = value;
            onPostedValueChanged(postedValue);
            return true;
        }
        return false;
    }

    private synchronized boolean setValueSync(T value) {
        T oldValue = getValue();
        if ((null == oldValue) ? null != value : null == value || areNotEqual(oldValue, value)) {
            setPostedValue(value);
            return true;
        }
        return false;
    }

    @Override
    protected void setValue(T value) {
        if (setValueSync(value)) {
            super.setValue(value);
        }
    }

}
