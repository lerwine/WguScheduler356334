package Erwine.Leonard.T.wguscheduler356334.util.live;

import androidx.lifecycle.LiveData;

import java.util.Objects;
import java.util.Optional;

public class OptionalLiveData<T> extends LiveData<Optional<T>> {
    private final PresentLiveData presentLiveData;
    private final ValueLiveData valueLiveData;

    public OptionalLiveData(Optional<T> initialValue) {
        super((null == initialValue) ? Optional.empty() : initialValue);
        presentLiveData = new PresentLiveData();
        valueLiveData = new ValueLiveData();
    }

    public OptionalLiveData(T initialValue) {
        this(Optional.of(initialValue));
    }

    public OptionalLiveData() {
        this(Optional.empty());
    }

    protected synchronized void postPlainValue(T value) {
        super.postValue(Optional.ofNullable(value));
    }

    protected T onOrElseGet() {
        // TODO: Implement Erwine.Leonard.T.wguscheduler356334.util.live.OptionalLiveData.onOrElseGet
        return null;
    }

    @Override
    protected void setValue(Optional<T> value) {
        Optional<T> currentValue = Objects.requireNonNull(getValue());
        if (value.map(t -> currentValue.map(u -> !Objects.equals(t, u)).orElse(true)).orElseGet(currentValue::isPresent)) {
            super.setValue(value);
            presentLiveData.set(value.isPresent());
            valueLiveData.set(value.orElseGet(OptionalLiveData.this::onOrElseGet));
        }
    }

    protected void setPlainValue(T value) {
        setValue(Optional.ofNullable(value));
    }

    public LiveData<Boolean> getPresentLiveData() {
        return presentLiveData;
    }

    public LiveData<T> getValueLiveData() {
        return valueLiveData;
    }

    public boolean isPresent() {
        //noinspection ConstantConditions
        return presentLiveData.getValue();
    }

    private class ValueLiveData extends LiveData<T> {

        public ValueLiveData() {
            super(Objects.requireNonNull(OptionalLiveData.this.getValue()).orElseGet(OptionalLiveData.this::onOrElseGet));
        }

        private void set(T value) {
            if (!Objects.equals(value, getValue())) {
                super.setValue(value);
            }
        }

        private void post(T value) {
            super.postValue(value);
        }
    }

    private class PresentLiveData extends LiveData<Boolean> {

        public PresentLiveData() {
            super(Objects.requireNonNull(OptionalLiveData.this.getValue()).isPresent());
        }

        @SuppressWarnings("ConstantConditions")
        private void set(boolean value) {
            if (value != getValue()) {
                super.setValue(value);
            }
        }

        private void post(boolean value) {
            super.postValue(value);
        }
    }
}
