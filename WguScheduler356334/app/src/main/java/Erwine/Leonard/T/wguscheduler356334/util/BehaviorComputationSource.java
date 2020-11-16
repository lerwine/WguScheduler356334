package Erwine.Leonard.T.wguscheduler356334.util;

import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;
import io.reactivex.subjects.BehaviorSubject;

public class BehaviorComputationSource<T> extends ComputationSource<T, BehaviorSubject<T>> {

    public BehaviorComputationSource(@NonNull BehaviorSubject<T> subject) {
        super(subject);
    }

    @NonNull
    public static <T> BehaviorComputationSource<T> create() {
        return new BehaviorComputationSource<>(BehaviorSubject.create());
    }

    @NonNull
    public static <T> BehaviorComputationSource<T> createDefault(@NonNull T defaultValue) {
        return new BehaviorComputationSource<>(BehaviorSubject.createDefault(defaultValue));
    }

    @Nullable
    public T getValue() {
        return subject.getValue();
    }

}
