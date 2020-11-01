package Erwine.Leonard.T.wguscheduler356334.util;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import java.util.Optional;
import java.util.function.Supplier;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

public interface SubscribingLiveDataWrapper<T> extends Disposable {

    LiveData<T> getLiveData();

    @NonNull
    static <T> SubscribingLiveDataWrapper<T> of(@NonNull Observable<T> observable) {
        return new StraightObserver<>(observable, new LiveDataImpl<>());
    }

    @NonNull
    static <T> SubscribingLiveDataWrapper<T> of(T initialValue, @NonNull Observable<T> observable) {
        return new StraightObserver<>(observable, new LiveDataImpl<>(initialValue));
    }

    @NonNull
    static <T> SubscribingLiveDataWrapper<T> ofOptional(@NonNull Observable<Optional<T>> observable) {
        return new OptionalObserver<>(observable, new LiveDataImpl<>());
    }

    @NonNull
    static <T> SubscribingLiveDataWrapper<T> ofOptional(T initialValue, @NonNull Observable<Optional<T>> observable) {
        return new OptionalObserver<>(observable, new LiveDataImpl<>(initialValue));
    }

    @NonNull
    static <T> SubscribingLiveDataWrapper<T> ofOptional(@NonNull Observable<Optional<T>> observable, T orElse) {
        return new OptionalObserver<>(observable, new LiveDataImpl<>(), orElse);
    }

    @NonNull
    static <T> SubscribingLiveDataWrapper<T> ofOptional(T initialValue, @NonNull Observable<Optional<T>> observable, T orElse) {
        return new OptionalObserver<>(observable, new LiveDataImpl<>(initialValue), orElse);
    }

    @NonNull
    static <T> SubscribingLiveDataWrapper<T> ofOptional(T initialValue, @NonNull Observable<Optional<T>> observable, Supplier<? extends T> orElse) {
        return new OptionalObserver<>(observable, new LiveDataImpl<>(initialValue), orElse);
    }

    @NonNull
    static <T> SubscribingLiveDataWrapper<T> ofOptional(@NonNull Observable<Optional<T>> observable, Supplier<? extends T> orElse) {
        return new OptionalObserver<>(observable, new LiveDataImpl<>(), orElse);
    }

    class StraightObserver<T> implements SubscribingLiveDataWrapper<T> {

        @NonNull
        private final LiveDataImpl<T> liveData;
        private final Disposable disposable;

        private StraightObserver(@NonNull Observable<T> observable, @NonNull LiveDataImpl<T> liveData) {
            this.liveData = liveData;
            disposable = observable.subscribe(liveData::postValue);
        }

        @Override
        public LiveData<T> getLiveData() {
            return liveData;
        }

        @Override
        public void dispose() {
            disposable.dispose();
        }

        @Override
        public boolean isDisposed() {
            return disposable.isDisposed();
        }

    }

    class OptionalObserver<T> implements SubscribingLiveDataWrapper<T> {

        @NonNull
        private final LiveDataImpl<T> liveData;
        private final Disposable disposable;

        private OptionalObserver(@NonNull Observable<Optional<T>> observable, @NonNull LiveDataImpl<T> liveData) {
            this.liveData = liveData;
            disposable = observable.subscribe(o -> liveData.postValue(o.orElse(null)));
        }

        private OptionalObserver(@NonNull Observable<Optional<T>> observable, @NonNull LiveDataImpl<T> liveData, T orElse) {
            this.liveData = liveData;
            disposable = observable.subscribe(o -> liveData.postValue(o.orElse(orElse)));
        }

        private OptionalObserver(@NonNull Observable<Optional<T>> observable, @NonNull LiveDataImpl<T> liveData, Supplier<? extends T> orElse) {
            this.liveData = liveData;
            disposable = observable.subscribe(o -> liveData.postValue(o.orElseGet(orElse)));
        }

        @Override
        public LiveData<T> getLiveData() {
            return liveData;
        }

        @Override
        public void dispose() {
            disposable.dispose();
        }

        @Override
        public boolean isDisposed() {
            return disposable.isDisposed();
        }

    }

    class LiveDataImpl<T> extends LiveData<T> {

        private LiveDataImpl() {
            super();
        }

        private LiveDataImpl(T initialValue) {
            super(initialValue);
        }

        @Override
        protected void postValue(T value) {
            super.postValue(value);
        }
    }
}
