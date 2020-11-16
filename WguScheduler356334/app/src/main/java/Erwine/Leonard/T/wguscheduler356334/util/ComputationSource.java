package Erwine.Leonard.T.wguscheduler356334.util;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.Subject;

public class ComputationSource<T, U extends Subject<T>> implements ObservableSource<T>, Observer<T> {

    protected final U subject;
    private final Observable<T> observable;

    public ComputationSource(@NonNull U subject) {
        this.subject = subject;
        observable = subject.observeOn(Schedulers.computation());
    }

    @NonNull
    public Observable<T> getObservable() {
        return observable;
    }

    @Override
    public void subscribe(@NonNull Observer<? super T> observer) {
        observable.subscribe(observer);
    }

    @Override
    public void onSubscribe(@NonNull Disposable d) {
        subject.onSubscribe(d);
    }

    @Override
    public void onNext(@NonNull T t) {
        subject.onNext(t);
    }

    @Override
    public void onError(@NonNull Throwable e) {
        subject.onError(e);
    }

    @Override
    public void onComplete() {
        subject.onComplete();
    }

}
