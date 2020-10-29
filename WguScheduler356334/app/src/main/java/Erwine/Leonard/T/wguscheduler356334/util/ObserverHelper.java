package Erwine.Leonard.T.wguscheduler356334.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;

import java.lang.ref.WeakReference;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

import static androidx.lifecycle.Lifecycle.State.DESTROYED;

public class ObserverHelper {

    public static <T> void observe(@NonNull LiveData<T> source, @NonNull WguSchedulerViewModel owner, @NonNull androidx.lifecycle.Observer<T> observer) {
        ViewModelDependentLiveDataObserverProxy<T> proxy = new ViewModelDependentLiveDataObserverProxy<>(owner, observer);
        proxy.attach(source);
    }

    public static class ViewModelDependentLiveDataObserverProxy<T> implements androidx.lifecycle.Observer<T>, Disposable {
        private final Disposable disposable;
        @Nullable
        private WguSchedulerViewModel owner;
        @Nullable
        private androidx.lifecycle.Observer<T> observer;
        @Nullable
        private LiveData<T> source;

        public ViewModelDependentLiveDataObserverProxy(@NonNull WguSchedulerViewModel owner, @NonNull androidx.lifecycle.Observer<T> observer) {
            this.owner = owner;
            this.observer = observer;
            disposable = WguSchedulerViewModel.subscribeCleared(owner, this::onOwnerCleared);
            if (!disposable.isDisposed() && WguSchedulerViewModel.isCleared(owner)) {
                disposable.dispose();
            }
        }

        @Nullable
        public WguSchedulerViewModel getOwner() {
            return owner;
        }

        private synchronized void onOwnerCleared() {
            if (null != source) {
                source.removeObserver(this);
            }
            if (!disposable.isDisposed()) {
                disposable.dispose();
            }
        }

        @Override
        public void onChanged(T t) {
            if (null != observer) {
                observer.onChanged(t);
            }
        }

        public synchronized void attach(LiveData<T> source) {
            if (null != this.source) {
                throw new IllegalStateException();
            }
            if (!disposable.isDisposed()) {
                (this.source = source).observeForever(this);
            }
        }

        public synchronized void detach() {
            if (null != source) {
                source.removeObserver(this);
                source = null;
            }
        }

        @Override
        public void dispose() {
            try {
                if (!disposable.isDisposed()) {
                    disposable.dispose();
                }
                detach();
            } finally {
                owner = null;
                source = null;
                observer = null;
            }
        }

        @Override
        public boolean isDisposed() {
            return disposable.isDisposed();
        }
    }

    public static <T> void observeOnce(@NonNull LiveData<T> source, @NonNull LifecycleOwner owner, @NonNull androidx.lifecycle.Observer<T> observer, boolean acceptNull) {
        if (acceptNull) {
            source.observe(owner, new androidx.lifecycle.Observer<T>() {
                @Override
                public void onChanged(T t) {
                    observer.onChanged(t);
                    source.removeObserver(this);
                }
            });
        } else {
            source.observe(owner, new androidx.lifecycle.Observer<T>() {
                @Override
                public void onChanged(T t) {
                    if (null != t) {
                        observer.onChanged(t);
                        source.removeObserver(this);
                    }
                }
            });
        }
    }

    public static <T> void observeOnce(@NonNull LiveData<T> source, @NonNull WguSchedulerViewModel owner, @NonNull androidx.lifecycle.Observer<T> observer, boolean acceptNull) {
        ViewModelDependentLiveDataObserverProxy<T> proxy;
        if (acceptNull) {
            proxy = new ViewModelDependentLiveDataObserverProxy<T>(owner, observer) {
                @Override
                public void onChanged(T t) {
                    super.onChanged(t);
                    dispose();
                }
            };
        } else {
            proxy = new ViewModelDependentLiveDataObserverProxy<T>(owner, observer) {
                @Override
                public void onChanged(T t) {
                    if (null != t) {
                        super.onChanged(t);
                        dispose();
                    }
                }
            };
        }
        proxy.attach(source);
    }

    public static <T> void observeOnce(@NonNull LiveData<T> source, @NonNull WguSchedulerViewModel owner, @NonNull androidx.lifecycle.Observer<T> observer) {
        observeOnce(source, owner, observer, false);
    }

    public static <T> void observeOnce(@NonNull LiveData<T> source, @NonNull LifecycleOwner owner, @NonNull androidx.lifecycle.Observer<T> target) {
        observeOnce(source, owner, target, false);
    }

    public static <T> void subscribeOnce(@NonNull Single<T> source, @NonNull LifecycleOwner owner, @NonNull Consumer<T> onNext, @NonNull Consumer<? super Throwable> onError) {
        LifecycleSingleObserverProxy<T> singleObserverProxy = new LifecycleSingleObserverProxy<>(new SingleObserver<T>() {

            @Override
            public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
            }

            @Override
            public void onSuccess(@io.reactivex.annotations.NonNull T t) {
                try {
                    onNext.accept(t);
                } catch (Exception e) {
                    Thread.UncaughtExceptionHandler eh = Thread.getDefaultUncaughtExceptionHandler();
                    if (null != eh) {
                        eh.uncaughtException(Thread.currentThread(), e);
                    }
                }
            }

            @Override
            public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                try {
                    onError.accept(e);
                } catch (Exception ex) {
                    Thread.UncaughtExceptionHandler eh = Thread.getDefaultUncaughtExceptionHandler();
                    if (null != eh) {
                        eh.uncaughtException(Thread.currentThread(), ex);
                    }
                }
            }
        }, owner);
        source.subscribe(singleObserverProxy);
    }

    // TODO: Make this obsolete
    public static <T> Disposable subscribeOnce(@NonNull Single<T> source, @NonNull Consumer<T> onNext, @NonNull Consumer<? super Throwable> onError) {
        ConsumerProxy2<T> proxy = new ConsumerProxy2<>(onNext, onError);
        proxy.subscribeSingle(source);
        return ((ConsumerProxy<T>) proxy).disposable;
    }

    public static <T> void subscribeOnce(@NonNull Single<T> source, @NonNull LifecycleOwner owner, @NonNull SingleObserver<T> observer) {
        source.subscribe(new LifecycleSingleObserverProxy<>(observer, owner));
    }

    @NonNull
    private static <T> SingleObserver<T> toSingleObserver(@NonNull Consumer<T> onSuccess, @Nullable Consumer<Throwable> onError) {
        if (null == onError) {
            return new SingleObserver<T>() {
                @Override
                public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
                }

                @Override
                public void onSuccess(@io.reactivex.annotations.NonNull T t) {
                    try {
                        onSuccess.accept(t);
                    } catch (Exception ex) {
                        Thread.UncaughtExceptionHandler eh = Thread.getDefaultUncaughtExceptionHandler();
                        if (null != eh) {
                            eh.uncaughtException(Thread.currentThread(), ex);
                        }
                    }
                }

                @Override
                public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                }
            };
        }
        return new SingleObserver<T>() {
            @Override
            public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
            }

            @Override
            public void onSuccess(@io.reactivex.annotations.NonNull T t) {
                try {
                    onSuccess.accept(t);
                } catch (Exception ex) {
                    Thread.UncaughtExceptionHandler eh = Thread.getDefaultUncaughtExceptionHandler();
                    if (null != eh) {
                        eh.uncaughtException(Thread.currentThread(), ex);
                    }
                }
            }

            @Override
            public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                try {
                    onError.accept(e);
                } catch (Exception ex) {
                    Thread.UncaughtExceptionHandler eh = Thread.getDefaultUncaughtExceptionHandler();
                    if (null != eh) {
                        eh.uncaughtException(Thread.currentThread(), ex);
                    }
                }
            }
        };
    }

    private static CompletableObserver toCompletableObserver(@NonNull Action onComplete, @Nullable Consumer<? super Throwable> onError) {
        return new CompletableObserver() {
            @Override
            public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
            }

            @Override
            public void onComplete() {
                try {
                    onComplete.run();
                } catch (Exception ex) {
                    Thread.UncaughtExceptionHandler eh = Thread.getDefaultUncaughtExceptionHandler();
                    if (null != eh) {
                        eh.uncaughtException(Thread.currentThread(), ex);
                    }
                }
            }

            @Override
            public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                try {
                    onError.accept(e);
                } catch (Exception ex) {
                    Thread.UncaughtExceptionHandler eh = Thread.getDefaultUncaughtExceptionHandler();
                    if (null != eh) {
                        eh.uncaughtException(Thread.currentThread(), ex);
                    }
                }
            }
        };
    }

    public static <T> void subscribeOnce(@NonNull Single<T> source, LifecycleOwner owner, @NonNull Consumer<T> onNext) {
        source.subscribe(new LifecycleSingleObserverProxy<>(toSingleObserver(onNext, null), owner));
    }

    public static void subscribeOnce(@NonNull Completable source, LifecycleOwner owner, @NonNull Action onComplete, @NonNull Consumer<? super Throwable> onError) {
        source.subscribe(new LifecycleCompletableObserverProxy(toCompletableObserver(onComplete, onError), owner));
    }

    public static void subscribeOnce(@NonNull Completable source, LifecycleOwner owner, @NonNull CompletableObserver observer) {
        source.subscribe(new LifecycleCompletableObserverProxy(observer, owner));
    }

    private ObserverHelper() {
    }

    private static abstract class ConsumerProxy<T> implements Disposable {

        @NonNull
        private final Consumer<T> onNext;
        private Disposable disposable;

        ConsumerProxy(@NonNull Consumer<T> onNext) {
            this.onNext = onNext;
        }

        void subscribeSingle(Single<T> source) {
            disposable = onSubscribeSingle(source);
        }

        void subscribeObservable(Observable<T> source) {
            disposable = onSubscribeObservable(source);
        }

        protected abstract Disposable onSubscribeSingle(Single<T> source);

        protected abstract Disposable onSubscribeObservable(Observable<T> source);

        protected void acceptNext(@NonNull T t) throws Exception {
            dispose();
            onNext.accept(t);
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
        ConsumerProxy1(@NonNull Consumer<T> onNext) {
            super(onNext);
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

        ConsumerProxy2(@NonNull Consumer<T> onNext, @NonNull Consumer<? super Throwable> onError) {
            super(onNext);
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

    private static class LifecycleSingleObserverProxy<T> extends SingleObserverProxy<T> implements LifecycleEventObserver {

        private final WeakReference<LifecycleOwner> ownerReference;

        LifecycleSingleObserverProxy(@NonNull SingleObserver<T> backingObserver, @NonNull LifecycleOwner owner) {
            super(backingObserver);
            this.ownerReference = new WeakReference<>(owner);
        }

        @Override
        public void dispose() {
            LifecycleOwner owner = ownerReference.get();
            dispose((null == owner) ? null : owner.getLifecycle());
        }

        private void dispose(Lifecycle lifecycle) {
            ownerReference.clear();
            super.dispose();
            if (null != lifecycle) {
                lifecycle.removeObserver(this);
            }
        }

        public synchronized void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
            if (isDisposed()) {
                return;
            }
            LifecycleOwner owner = ownerReference.get();
            Lifecycle lifecycle = source.getLifecycle();
            if (null != owner) {
                if (source != owner) {
                    lifecycle.removeObserver(this);
                    return;
                }
                if (lifecycle.getCurrentState() != DESTROYED) {
                    return;
                }
            }
            dispose(lifecycle);
        }

        @Override
        public synchronized void onSubscribe(@NonNull Disposable d) {
            super.onSubscribe(d);
            if (d.isDisposed()) {
                return;
            }
            LifecycleOwner owner = ownerReference.get();
            Lifecycle lifecycle;
            if (null != owner && (lifecycle = owner.getLifecycle()).getCurrentState() != DESTROYED) {
                lifecycle.addObserver(this);
            } else {
                dispose(null);
            }
        }
    }

    private static class SingleObserverProxy<T> implements SingleObserver<T>, Disposable {

        private final SingleObserver<T> backingObserver;
        private Disposable disposable;

        SingleObserverProxy(SingleObserver<T> backingObserver) {
            this.backingObserver = backingObserver;
        }

        @Override
        public void onSubscribe(@NonNull Disposable d) {
            disposable = d;
            backingObserver.onSubscribe(d);
        }

        @Override
        public void onSuccess(@NonNull T t) {
            if (!disposable.isDisposed()) {
                backingObserver.onSuccess(t);
                disposable.dispose();
            }
        }

        @Override
        public void onError(@NonNull Throwable e) {
            if (!disposable.isDisposed()) {
                backingObserver.onError(e);
                disposable.dispose();
            }
        }

        @Override
        public void dispose() {
            if (!disposable.isDisposed()) {
                disposable.dispose();
            }
        }

        @Override
        public boolean isDisposed() {
            return disposable.isDisposed();
        }
    }

    private static class LifecycleCompletableObserverProxy extends CompletableObserverProxy implements LifecycleEventObserver {

        private final WeakReference<LifecycleOwner> ownerReference;

        LifecycleCompletableObserverProxy(@NonNull CompletableObserver backingObserver, @NonNull LifecycleOwner owner) {
            super(backingObserver);
            this.ownerReference = new WeakReference<>(owner);
        }

        @Override
        public void dispose() {
            LifecycleOwner owner = ownerReference.get();
            dispose((null == owner) ? null : owner.getLifecycle());
        }

        private void dispose(Lifecycle lifecycle) {
            ownerReference.clear();
            super.dispose();
            if (null != lifecycle) {
                lifecycle.removeObserver(this);
            }
        }

        public synchronized void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
            if (isDisposed()) {
                return;
            }
            LifecycleOwner owner = ownerReference.get();
            Lifecycle lifecycle = source.getLifecycle();
            if (null != owner) {
                if (source != owner) {
                    lifecycle.removeObserver(this);
                    return;
                }
                if (lifecycle.getCurrentState() != DESTROYED) {
                    return;
                }
            }
            dispose(lifecycle);
        }

        @Override
        public synchronized void onSubscribe(@NonNull Disposable d) {
            super.onSubscribe(d);
            if (d.isDisposed()) {
                return;
            }
            LifecycleOwner owner = ownerReference.get();
            Lifecycle lifecycle;
            if (null != owner && (lifecycle = owner.getLifecycle()).getCurrentState() != DESTROYED) {
                lifecycle.addObserver(this);
            } else {
                dispose(null);
            }
        }
    }

    private static class CompletableObserverProxy implements CompletableObserver, Disposable {

        private final CompletableObserver backingObserver;
        private Disposable disposable;

        CompletableObserverProxy(CompletableObserver backingObserver) {
            this.backingObserver = backingObserver;
        }

        @Override
        public void onSubscribe(@NonNull Disposable d) {
            disposable = d;
            backingObserver.onSubscribe(d);
        }

        @Override
        public void onComplete() {
            if (!disposable.isDisposed()) {
                backingObserver.onComplete();
                disposable.dispose();
            }
        }

        @Override
        public void onError(@NonNull Throwable e) {
            if (!disposable.isDisposed()) {
                backingObserver.onError(e);
                disposable.dispose();
            }
        }

        @Override
        public void dispose() {
            if (!disposable.isDisposed()) {
                disposable.dispose();
            }
        }

        @Override
        public boolean isDisposed() {
            return disposable.isDisposed();
        }
    }

}
