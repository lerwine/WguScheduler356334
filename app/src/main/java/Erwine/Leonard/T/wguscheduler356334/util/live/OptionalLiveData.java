package Erwine.Leonard.T.wguscheduler356334.util.live;

import java.util.Optional;

public class OptionalLiveData<T> extends PostTrackingLiveData<T> {
    private final LiveBooleanData presentLiveData;
    private Optional<T> optionalValue;

    public OptionalLiveData(Optional<T> initialValue) {
        super(initialValue.orElse(null));
        optionalValue = normalize(initialValue);
        presentLiveData = new LiveBooleanData(optionalValue.isPresent());
    }

    public OptionalLiveData(T initialValue) {
        this(Optional.of(initialValue));
    }

    public OptionalLiveData() {
        this(Optional.empty());
    }

    private static <T> Optional<T> normalize(Optional<T> value) {
        return (null == value) ? Optional.empty() : value.flatMap(i -> (null == i) ? Optional.empty() : value);
    }

    private static <T> Optional<T> asOptional(T value) {
        return (null == value) ? Optional.empty() : Optional.of(value);
    }

    protected synchronized void postValue(Optional<T> value) {
        optionalValue = normalize(value);
        presentLiveData.postValue(optionalValue.isPresent());
        super.postValue(optionalValue.orElse(null));
    }

    public Optional<T> getOptionalValue() {
        return optionalValue;
    }

    @Override
    protected void onPostedValueChanged(T value) {
        optionalValue = asOptional(value);
        presentLiveData.postValue(optionalValue.isPresent());
    }

    public LiveBooleanData getPresentLiveData() {
        return presentLiveData;
    }

    public boolean isPresent() {
        return presentLiveData.getValue();
    }

    public boolean isPostedPresent() {
        return presentLiveData.getPostedValue();
    }

}
