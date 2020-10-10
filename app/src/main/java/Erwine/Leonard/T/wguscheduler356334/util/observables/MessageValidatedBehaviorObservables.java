package Erwine.Leonard.T.wguscheduler356334.util.observables;

import androidx.annotation.NonNull;
import androidx.core.util.Consumer;

import java.util.Optional;

import Erwine.Leonard.T.wguscheduler356334.util.validation.ResourceMessageFactory;
import io.reactivex.Maybe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.subjects.MaybeSubject;

public abstract class MessageValidatedBehaviorObservables<T, R> extends ValidatedBehaviorObservables<T, R> {
    private final MaybeSubject<ResourceMessageFactory> errorSubject;
    private final MaybeSubject<ResourceMessageFactory> warningSubject;
    private final Maybe<ResourceMessageFactory> errorMaybe;
    private final Maybe<ResourceMessageFactory> warningMaybe;
    private boolean emitted;

    public MessageValidatedBehaviorObservables() {
        errorSubject = MaybeSubject.create();
        errorMaybe = errorSubject.observeOn(AndroidSchedulers.mainThread());
        warningSubject = MaybeSubject.create();
        warningMaybe = warningSubject.observeOn(AndroidSchedulers.mainThread());
    }

    public Maybe<ResourceMessageFactory> getErrorMaybe() {
        return errorMaybe;
    }

    public Maybe<ResourceMessageFactory> getWarningMaybe() {
        return warningMaybe;
    }

    @NonNull
    protected abstract Optional<R> mapNext(@NonNull T t, Consumer<ResourceMessageFactory> onMessage);

    @NonNull
    @Override
    public synchronized Optional<R> mapNext(@NonNull T t) {
        emitted = false;
        Optional<R> result = mapNext(t, this::acceptMessage);
        if (!emitted) {
            errorSubject.onComplete();
            warningSubject.onComplete();
        }
        return result;
    }

    private void acceptMessage(ResourceMessageFactory resourceMessageFactory) {
        emitted = true;
        if (null == resourceMessageFactory) {
            errorSubject.onComplete();
        } else {
            if (resourceMessageFactory.isWarning()) {
                errorSubject.onComplete();
                warningSubject.onSuccess(resourceMessageFactory);
                return;
            }
            errorSubject.onSuccess(resourceMessageFactory);
        }
        warningSubject.onComplete();
    }
}
