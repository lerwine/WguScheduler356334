package Erwine.Leonard.T.wguscheduler356334.util.observables;

import androidx.annotation.NonNull;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.MaybeSubject;

public abstract class MaybeFlattenedObservables<T, R> implements Disposable {
    private final MaybeSubject<T> sourceSubject;
    private final BehaviorSubject<R> resultBehavior;
    private final Disposable disposable;
    private final Maybe<T> sourceMaybe;
    private final Observable<R> resultObservable;

    MaybeFlattenedObservables() {
        sourceSubject = MaybeSubject.create();
        resultBehavior = BehaviorSubject.create();
        disposable = sourceSubject.subscribe(
                t -> resultBehavior.onNext(mapSuccess(t)),
                e -> resultBehavior.onNext(mapError(e)),
                () -> resultBehavior.onNext(mapComplete())
        );
        sourceMaybe = sourceSubject.observeOn(AndroidSchedulers.mainThread());
        resultObservable = resultBehavior.observeOn(AndroidSchedulers.mainThread());
    }

    public final Maybe<T> getSourceMaybe() {
        return sourceMaybe;
    }

    public final Observable<R> getResultObservable() {
        return resultObservable;
    }

    @NonNull
    public abstract R mapSuccess(@NonNull T t);

    @NonNull
    public abstract R mapError(@NonNull Throwable throwable);

    @NonNull
    public abstract R mapComplete();

    public void onSuccess(@NonNull T t) {
        sourceSubject.onSuccess(t);
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
