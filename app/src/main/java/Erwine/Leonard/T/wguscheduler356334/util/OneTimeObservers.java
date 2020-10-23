package Erwine.Leonard.T.wguscheduler356334.util;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

public class OneTimeObservers {

    public static <T> void observeOnce(@NonNull LiveData<T> source, @NonNull LifecycleOwner owner, @NonNull androidx.lifecycle.Observer<T> target, boolean acceptNull) {
        LiveDataObserverProxy<T> proxy = new LiveDataObserverProxy<>(source, target, acceptNull);
        source.observe(owner, proxy);
    }

    public static <T> void observeOnce(@NonNull LiveData<T> source, @NonNull LifecycleOwner owner, @NonNull androidx.lifecycle.Observer<T> target) {
        observeOnce(source, owner, target, false);
    }

    public static <T> void observeOnce(@NonNull LiveData<T> source, @NonNull androidx.lifecycle.Observer<T> target, boolean acceptNull) {
        LiveDataObserverProxy<T> proxy = new LiveDataObserverProxy<>(source, target, acceptNull);
        source.observeForever(proxy);
    }

    public static <T> void observeOnce(@NonNull LiveData<T> source, @NonNull androidx.lifecycle.Observer<T> target) {
        observeOnce(source, target, false);
    }

    public static <T> void subscribeOnce(@NonNull Single<T> source, @NonNull Consumer<T> onNext, @NonNull Consumer<? super Throwable> onError, boolean skipFirstNull) {
        ConsumerProxy2<T> proxy = new ConsumerProxy2<>(onNext, onError, skipFirstNull);
        proxy.subscribeSingle(source);
    }

    public static <T> void subscribeOnce(@NonNull Single<T> source, @NonNull Consumer<T> onNext, @NonNull Consumer<? super Throwable> onError) {
        subscribeOnce(source, onNext, onError, false);
    }

    public static <T> void subscribeOnce(@NonNull Single<T> source, @NonNull Consumer<T> onNext, boolean skipFirstNull) {
        ConsumerProxy1<T> proxy = new ConsumerProxy1<>(onNext, skipFirstNull);
        proxy.subscribeSingle(source);
    }

    public static <T> void subscribeOnce(@NonNull Single<T> source, @NonNull SingleObserver<T> observer, boolean skipFirstNull) {
        if (skipFirstNull) {
            source.subscribe(new SingleObserverProxy<T>(observer) {
                boolean isFirst = true;

                @Override
                public void onSuccess(@NonNull T t) {
                    if (isFirst) {
                        isFirst = false;
                        if (null == t) {
                            return;
                        }
                    }
                    super.onSuccess(t);
                }
            });
        } else {
            source.subscribe(new SingleObserverProxy<>(observer));
        }
    }

    public static <T> void subscribeOnce(@NonNull Single<T> source, @NonNull Consumer<T> onNext) {
        subscribeOnce(source, onNext, false);
    }

    public static <T> void subscribeOnce(@NonNull Single<T> source, @NonNull SingleObserver<T> observer) {
        subscribeOnce(source, observer, false);
    }

    public static <T> void subscribeOnce(@NonNull Observable<T> source, @NonNull Consumer<T> onNext, @NonNull Consumer<? super Throwable> onError, @NonNull Action onComplete, boolean skipFirstNull) {
        ConsumerProxy2<T> proxy = new ConsumerProxy3<>(onNext, onError, onComplete, skipFirstNull);
        proxy.onSubscribeObservable(source);
    }

    public static <T> void subscribeOnce(@NonNull Observable<T> source, @NonNull Consumer<T> onNext, @NonNull Consumer<? super Throwable> onError, @NonNull Action onComplete) {
        subscribeOnce(source, onNext, onError, onComplete, false);
    }

    public static <T> void subscribeOnce(@NonNull Observable<T> source, @NonNull Consumer<T> onNext, @NonNull Consumer<? super Throwable> onError, boolean skipFirstNull) {
        ConsumerProxy2<T> proxy = new ConsumerProxy2<>(onNext, onError, skipFirstNull);
        proxy.onSubscribeObservable(source);
    }

    public static <T> void subscribeOnce(@NonNull Observable<T> source, @NonNull Consumer<T> onNext, @NonNull Consumer<? super Throwable> onError) {
        subscribeOnce(source, onNext, onError, false);
    }

    public static <T> void subscribeOnce(@NonNull Observable<T> source, @NonNull Consumer<T> onNext, boolean skipFirstNull) {
        ConsumerProxy1<T> proxy = new ConsumerProxy1<>(onNext, skipFirstNull);
        proxy.onSubscribeObservable(source);
    }

    public static <T> void subscribeOnce(@NonNull Observable<T> source, @NonNull Observer<T> observer, boolean skipFirstNull) {
        if (skipFirstNull) {
            source.subscribe(new ObserverProxy<T>(observer) {
                boolean isFirst = true;

                @Override
                public void onNext(@NonNull T t) {
                    if (isFirst) {
                        isFirst = false;
                        if (null == t) {
                            return;
                        }
                    }
                    super.onNext(t);
                }
            });
        } else {
            source.subscribe(new ObserverProxy<>(observer));
        }
    }

    public static <T> void subscribeOnce(@NonNull Observable<T> source, @NonNull Consumer<T> onNext) {
        subscribeOnce(source, onNext, false);
    }

    public static <T> void subscribeOnce(@NonNull Observable<T> source, @NonNull Observer<T> observer) {
        subscribeOnce(source, observer, false);
    }

    public static void subscribeOnce(@NonNull Completable source, @NonNull Action onComplete, @NonNull Consumer<? super Throwable> onError) {
        ActionProxy2 proxy = new ActionProxy2(onComplete, onError);
        proxy.subscribeCompletable(source);
    }

    public static void subscribeOnce(@NonNull Completable source, @NonNull Action onComplete) {
        ActionProxy1 proxy = new ActionProxy1(onComplete);
        proxy.subscribeCompletable(source);
    }

    public static void subscribeOnce(@NonNull Completable source, @NonNull CompletableObserver observer) {
        source.subscribe(new CompletableObserver() {
            private Disposable disposable;

            @Override
            public void onSubscribe(@NonNull Disposable d) {
                disposable = d;
                observer.onSubscribe(d);
            }

            @Override
            public void onComplete() {
                observer.onComplete();
                disposable.dispose();
            }

            @Override
            public void onError(@NonNull Throwable e) {
                observer.onError(e);
                disposable.dispose();
            }
        });
    }

    private OneTimeObservers() {
    }

    private static abstract class ActionProxy implements Disposable {

        private final Action onComplete;
        private Disposable disposable;

        ActionProxy(@NonNull Action onComplete) {
            this.onComplete = onComplete;
        }

        void subscribeCompletable(@NonNull Completable target) {
            disposable = onSubscribeCompletable(target);
        }

        protected abstract Disposable onSubscribeCompletable(Completable target);

        void acceptComplete() throws Exception {
            dispose();
            onComplete.run();
        }

        @Override
        public void dispose() {
            if (null != disposable) {
                disposable.dispose();
            }
        }

        @Override
        public boolean isDisposed() {
            return null != disposable && disposable.isDisposed();
        }
    }

    private static class ActionProxy1 extends ActionProxy {

        ActionProxy1(@NonNull Action onComplete) {
            super(onComplete);
        }

        @Override
        protected Disposable onSubscribeCompletable(@NonNull Completable target) {
            return target.subscribe(this::acceptComplete);
        }
    }

    private static class ActionProxy2 extends ActionProxy1 {

        private final Consumer<? super Throwable> onError;

        ActionProxy2(@NonNull Action onComplete, @NonNull Consumer<? super Throwable> onError) {
            super(onComplete);
            this.onError = onError;
        }

        void acceptError(Throwable t) throws Exception {
            dispose();
            onError.accept(t);
        }

        @Override
        protected Disposable onSubscribeCompletable(@NonNull Completable target) {
            return target.subscribe(this::acceptComplete, this::acceptError);
        }
    }

    private static abstract class ConsumerProxy<T> implements Disposable {

        @NonNull
        private final Consumer<T> onNext;
        private final boolean skipFirstNull;
        private Disposable disposable;

        ConsumerProxy(@NonNull Consumer<T> onNext, boolean skipFirstNull) {
            this.onNext = onNext;
            this.skipFirstNull = skipFirstNull;
        }

        void subscribeSingle(Single<T> source) {
            disposable = onSubscribeSingle(source);
        }

        void subscribeObservable(Observable<T> source) {
            disposable = onSubscribeObservable(source);
        }

        protected abstract Disposable onSubscribeSingle(Single<T> source);

        protected abstract Disposable onSubscribeObservable(Observable<T> source);

        protected void acceptNext(T t) throws Exception {
            if (!skipFirstNull || null != t) {
                dispose();
                onNext.accept(t);
            }
        }

        @Override
        public void dispose() {
            if (null != disposable) {
                disposable.dispose();
            }
        }

        @Override
        public boolean isDisposed() {
            return null != disposable && disposable.isDisposed();
        }
    }

    private static class ConsumerProxy1<T> extends ConsumerProxy<T> {
        ConsumerProxy1(@NonNull Consumer<T> onNext, boolean skipFirstNull) {
            super(onNext, skipFirstNull);
        }

        @Override
        protected Disposable onSubscribeSingle(Single<T> source) {
            return source.subscribe(this::acceptNext);
        }

        @Override
        protected Disposable onSubscribeObservable(Observable<T> source) {
            return source.subscribe(this::acceptNext);
        }
    }

    private static class ConsumerProxy2<T> extends ConsumerProxy1<T> {
        @NonNull
        private final Consumer<? super Throwable> onError;

        ConsumerProxy2(@NonNull Consumer<T> onNext, @NonNull Consumer<? super Throwable> onError, boolean skipFirstNull) {
            super(onNext, skipFirstNull);
            this.onError = onError;
        }

        void acceptError(Throwable t) throws Exception {
            dispose();
            onError.accept(t);
        }

        @Override
        protected Disposable onSubscribeSingle(Single<T> source) {
            return source.subscribe(this::acceptNext, this::acceptError);
        }

        @Override
        protected Disposable onSubscribeObservable(Observable<T> source) {
            return source.subscribe(this::acceptNext, this::acceptError);
        }
    }

    private static class ConsumerProxy3<T> extends ConsumerProxy2<T> {

        @NonNull
        private final Action onComplete;

        ConsumerProxy3(@NonNull Consumer<T> onNext, @NonNull Consumer<? super Throwable> onError, @NonNull Action onComplete, boolean skipFirstNull) {
            super(onNext, onError, skipFirstNull);
            this.onComplete = onComplete;
        }

        @Override
        protected Disposable onSubscribeSingle(Single<T> source) {
            throw new UnsupportedOperationException();
        }

        @Override
        protected Disposable onSubscribeObservable(Observable<T> source) {
            return source.subscribe(this::acceptNext, this::acceptError, this::acceptComplete);
        }

        void acceptComplete() throws Exception {
            dispose();
            onComplete.run();
        }
    }

    private static class LiveDataObserverProxy<T> implements androidx.lifecycle.Observer<T> {

        private final LiveData<T> source;
        private final androidx.lifecycle.Observer<T> target;
        private final boolean acceptNull;

        LiveDataObserverProxy(@NonNull LiveData<T> source, @NonNull androidx.lifecycle.Observer<T> target, boolean acceptNull) {
            this.source = source;
            this.target = target;
            this.acceptNull = acceptNull;
        }

        @Override
        public void onChanged(T t) {
            if (acceptNull || null != t) {
                source.removeObserver(LiveDataObserverProxy.this);
                target.onChanged(t);
            }
        }
    }

    private static class ObserverProxy<T> implements Observer<T>, Disposable {

        private final Observer<T> backingObserver;
        private Disposable disposable;

        ObserverProxy(@NonNull Observer<T> backingObserver) {
            this.backingObserver = backingObserver;
        }

        @Override
        public final void onSubscribe(@NonNull Disposable d) {
            disposable = d;
            backingObserver.onSubscribe(d);
        }

        @Override
        public void onNext(@NonNull T t) {
            backingObserver.onNext(t);
            disposable.dispose();
        }

        @Override
        public void onError(@NonNull Throwable e) {
            backingObserver.onError(e);
            disposable.dispose();
        }

        @Override
        public void onComplete() {
            backingObserver.onComplete();
            disposable.dispose();
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

    private static class SingleObserverProxy<T> implements SingleObserver<T>, Disposable {

        private final SingleObserver<T> backingObserver;
        private Disposable disposable;

        SingleObserverProxy(SingleObserver<T> backingObserver) {
            this.backingObserver = backingObserver;
        }

        @Override
        public final void onSubscribe(@NonNull Disposable d) {
            disposable = d;
            backingObserver.onSubscribe(d);
        }

        @Override
        public void onSuccess(@NonNull T t) {
            backingObserver.onSuccess(t);
            disposable.dispose();
        }

        @Override
        public void onError(@NonNull Throwable e) {
            backingObserver.onError(e);
            disposable.dispose();
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

}
