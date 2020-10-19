package Erwine.Leonard.T.wguscheduler356334.util.observables;

import androidx.annotation.NonNull;

import java.util.Objects;

import Erwine.Leonard.T.wguscheduler356334.util.Workers;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.BehaviorSubject;

/**
 * Maps an {@link Observable}, invalidating only if the mapped result changes.
 *
 * @param <T> The source object type.
 * @param <R> The mapped object type.
 */
public abstract class MappedObservableSource<T, R> implements ObservableSource<R>, Disposable {
    private final BehaviorSubject<R> resultBehavior;
    private final Disposable disposable;
    private final Observable<T> sourceObservable;

    protected MappedObservableSource(@NonNull Observable<T> sourceObservable) {
        this.sourceObservable = sourceObservable;
        resultBehavior = BehaviorSubject.create();
        disposable = sourceObservable.subscribeOn(Workers.getScheduler()).subscribe(
                t -> {
                    R r = mapNext(t);
                    if (!Objects.equals(r, resultBehavior.getValue())) {
                        onNext(r);
                    }
                },
                this::onError,
                this::onComplete
        );
    }

    protected void onNext(@NonNull R next) {
        resultBehavior.onNext(next);
    }

    protected void onError(Throwable throwable) {
        resultBehavior.onError(throwable);
    }

    protected void onComplete() {
        resultBehavior.onComplete();
    }

    public final Observable<T> getSourceObservable() {
        return sourceObservable;
    }

    @NonNull
    protected abstract R mapNext(@NonNull T t);

    @Override
    public void dispose() {
        disposable.dispose();
    }

    @Override
    public boolean isDisposed() {
        return disposable.isDisposed();
    }

    @Override
    public void subscribe(@NonNull Observer<? super R> observer) {
        resultBehavior.subscribe(observer);
    }
}
