package Erwine.Leonard.T.wguscheduler356334.util.observables;

import androidx.annotation.NonNull;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.BehaviorSubject;

public abstract class MappedBehaviorObservables<T, R> implements Disposable {
    private final BehaviorSubject<T> sourceSubject;
    private final BehaviorSubject<R> resultBehavior;
    private final Disposable disposable;
    private final Observable<T> sourceObservable;
    private final Observable<R> resultObservable;

    MappedBehaviorObservables() {
        sourceSubject = BehaviorSubject.create();
        resultBehavior = BehaviorSubject.create();
        disposable = sourceSubject.subscribe(
                t -> resultBehavior.onNext(mapNext(t)),
                e -> resultBehavior.onNext(mapError(e)),
                resultBehavior::onComplete
        );
        sourceObservable = sourceSubject.observeOn(AndroidSchedulers.mainThread());
        resultObservable = resultBehavior.observeOn(AndroidSchedulers.mainThread());
    }

    public final Observable<T> getSourceObservable() {
        return sourceObservable;
    }

    public final Observable<R> getResultObservable() {
        return resultObservable;
    }

    @NonNull
    public abstract R mapNext(@NonNull T t);

    @NonNull
    public abstract R mapError(@NonNull Throwable throwable);

    public void onNext(@NonNull T t) {
        sourceSubject.onNext(t);
    }

    public void onError(@NonNull Throwable throwable) {
        sourceSubject.onError(throwable);
    }

    public void onComplete() {
        sourceSubject.onComplete();
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
