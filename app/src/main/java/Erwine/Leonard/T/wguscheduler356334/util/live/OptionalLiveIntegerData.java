package Erwine.Leonard.T.wguscheduler356334.util.live;

import androidx.annotation.NonNull;

import java.util.Optional;

public class OptionalLiveIntegerData extends OptionalLiveData<Integer> {

    public OptionalLiveIntegerData(Optional<Integer> initialValue) {
        super(initialValue);
    }

    public OptionalLiveIntegerData(int initialValue) {
        super(initialValue);
    }

    public OptionalLiveIntegerData() {
        super();
    }

    @Override
    protected boolean areNotEqual(@NonNull Integer a, @NonNull Integer b) {
        return a != b;
    }

}
