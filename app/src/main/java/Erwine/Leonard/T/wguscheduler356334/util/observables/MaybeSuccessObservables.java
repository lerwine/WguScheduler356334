package Erwine.Leonard.T.wguscheduler356334.util.observables;

import androidx.annotation.NonNull;

public abstract class MaybeSuccessObservables<T> extends MaybeFlattenedObservables<T, Boolean> {

    @NonNull
    @Override
    public Boolean mapError(@NonNull Throwable throwable) {
        return false;
    }

    @NonNull
    @Override
    public Boolean mapComplete() {
        return false;
    }
}
