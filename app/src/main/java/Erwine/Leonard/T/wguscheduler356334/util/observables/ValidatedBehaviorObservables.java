package Erwine.Leonard.T.wguscheduler356334.util.observables;

import androidx.annotation.NonNull;

import java.util.Optional;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.MaybeSubject;

public abstract class ValidatedBehaviorObservables<T, R> implements Disposable {
    private final BehaviorSubject<T> sourceSubject;
    private final MaybeSubject<R> validatedSubject;
    private final Disposable disposable;
    private final Observable<T> sourceObservable;
    private final Maybe<R> validatedResult;

    ValidatedBehaviorObservables() {
        sourceSubject = BehaviorSubject.create();
        validatedSubject = MaybeSubject.create();
        disposable = sourceSubject.subscribe(
                t -> {
                    Optional<R> mapped = mapNext(t);
                    if (mapped.isPresent()) {
                        validatedSubject.onSuccess(mapped.get());
                    } else {
                        validatedSubject.onComplete();
                    }
                },
                validatedSubject::onError,
                validatedSubject::onComplete
        );
        sourceObservable = sourceSubject.observeOn(AndroidSchedulers.mainThread());
        validatedResult = validatedSubject.observeOn(AndroidSchedulers.mainThread());
    }

    public final Observable<T> getSourceObservable() {
        return sourceObservable;
    }

    public Maybe<R> getValidatedResult() {
        return validatedResult;
    }

    @NonNull
    public abstract Optional<R> mapNext(@NonNull T t);

    public void onNext(@NonNull T t) {
        sourceSubject.onNext(t);
    }

    public void onError(@NonNull Throwable throwable) {
        sourceSubject.onError(throwable);
    }

    public void dispose() {
        disposable.dispose();
    }

    @Override
    public boolean isDisposed() {
        return disposable.isDisposed();
    }
}
