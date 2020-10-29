package Erwine.Leonard.T.wguscheduler356334.util;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import Erwine.Leonard.T.wguscheduler356334.MainActivity;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.subjects.CompletableSubject;

public class WguSchedulerViewModel extends AndroidViewModel {
    private static final String LOG_TAG = MainActivity.getLogTag(WguSchedulerViewModel.class);
    private final CompletableSubject completableSubject;
    private final HashSet<Subscription<?>> subscribed;
    private final Completable completable;

    public static Disposable subscribeCleared(@NonNull WguSchedulerViewModel source, @NonNull Action onComplete) {
        return source.subscribe(onComplete);
    }

    public static void subscribeCleared(@NonNull WguSchedulerViewModel source, @NonNull WguSchedulerViewModel owner, @NonNull Action onComplete) {
        source.subscribe(owner, onComplete);
    }

    public static void subscribeCleared(@NonNull WguSchedulerViewModel source, @NonNull LifecycleOwner owner, @NonNull Action onComplete) {
        source.subscribe(owner, onComplete);
    }

    public static void unsubscribeCleared(@NonNull WguSchedulerViewModel source, @NonNull Action onComplete) {
        source.unsubscribe(onComplete);
    }

    public static void unsubscribeCleared(@NonNull WguSchedulerViewModel source, @NonNull WguSchedulerViewModel owner) {
        source.unsubscribeByOwner(owner);
    }

    public static void unsubscribeCleared(@NonNull WguSchedulerViewModel source, @NonNull LifecycleOwner owner) {
        source.unsubscribeByOwner(owner);
    }

    {
        Log.d(LOG_TAG, "Constructing " + getClass().getName());
    }

    protected WguSchedulerViewModel(@NonNull Application application) {
        super(application);
        completableSubject = CompletableSubject.create();
        completable = completableSubject.observeOn(AndroidSchedulers.mainThread());
        subscribed = new HashSet<>();
    }

    public static boolean isCleared(WguSchedulerViewModel owner) {
        return owner.completableSubject.hasComplete() || owner.completableSubject.hasThrowable();
    }

    @Override
    protected void onCleared() {
        if (!completableSubject.hasComplete() || completableSubject.hasThrowable()) {
            Log.d(LOG_TAG, getClass().getName() + " cleared");
            completableSubject.onComplete();
        }
        super.onCleared();
    }

    @Override
    protected void finalize() throws Throwable {
        if (!completableSubject.hasComplete() || completableSubject.hasThrowable()) {
            try {
                subscribed.forEach(t -> {
                    try {
                        t.disposable.dispose();
                    } finally {
                        t.onDisposed();
                    }
                });
            } finally {
                subscribed.clear();
            }
            Log.d(LOG_TAG, getClass().getName() + " finalized");
        }
        super.finalize();
    }

    private Disposable subscribe(@NonNull Action onComplete) {
        DisposableProxy disposable = new DisposableProxy(onComplete);
        subscribed.add(disposable.proxy);
        return disposable;
    }

    private synchronized void subscribe(@NonNull LifecycleOwner owner, @NonNull Action onComplete) {
        LifecycleOwnerSubscription item = new LifecycleOwnerSubscription(onComplete, owner);
        subscribed.add(item);
        if (!item.attachTargetListener(owner)) {
            subscribed.remove(item);
        }
    }

    private synchronized void subscribe(@NonNull WguSchedulerViewModel owner, @NonNull Action onComplete) {
        ViewModelSubscription item = new ViewModelSubscription(onComplete, owner);
        subscribed.add(item);
        if (!item.attachTargetListener(owner)) {
            subscribed.remove(item);
        }
    }

    private synchronized void unsubscribe(Action onComplete) {
        List<Subscription<?>> list = new ArrayList<>();
        for (Subscription<?> t : subscribed) {
            if (t.isDisposed() || t.function == onComplete) {
                list.add(t);
            }
        }
        for (Subscription<?> t : list) {
            t.dispose();
        }
    }

    private synchronized void unsubscribeByOwner(Object owner) {
        List<Subscription<?>> list = new ArrayList<>();
        for (Subscription<?> t : subscribed) {
            if (t.isDisposed() || t.owner.get() == owner) {
                list.add(t);
            }
        }
        for (Subscription<?> t : list) {
            t.dispose();
        }
    }

    private synchronized void unsubscribe(@NonNull Subscription<?> target) {
        target.disposable.dispose();
        subscribed.remove(target);
        target.onDisposed();
    }

    private abstract class Subscription<T> implements Disposable {
        private final Disposable disposable;
        private final Object function;
        private final WeakReference<T> owner;

        Subscription(@NonNull Action onComplete, @NonNull T owner) {
            this.function = onComplete;
            this.owner = new WeakReference<>(owner);
            this.disposable = completable.subscribe(() -> {
                try {
                    onComplete.run();
                } finally {
                    this.owner.clear();
                    dispose();
                }
            });
        }

        T getOwner() {
            T o = owner.get();
            if (null == o) {
                dispose();
            }
            return o;
        }

        @Override
        public synchronized void dispose() {
            if (!disposable.isDisposed()) {
                unsubscribe(this);
            }
        }

        @Override
        public synchronized boolean isDisposed() {
            if (!disposable.isDisposed()) {
                if (null != owner.get()) {
                    return false;
                }
                unsubscribe(this);
            }
            return true;
        }

        public abstract void onDisposed();
    }

    private class DisposableSubscription extends Subscription<DisposableProxy> {

        private final DisposableProxy owner;

        DisposableSubscription(@NonNull Action onComplete, @NonNull DisposableProxy owner) {
            super(onComplete, owner);
            this.owner = owner;
        }

        @Override
        public void onDisposed() {
            owner.disposable.dispose();
        }

    }

    private class DisposableProxy implements Disposable {
        private final Disposable disposable;
        private final DisposableSubscription proxy;

        private DisposableProxy(@NonNull Action onComplete) {
            disposable = completable.subscribe(onComplete);
            proxy = new DisposableSubscription(onComplete, this);
        }

        @Override
        public synchronized void dispose() {
            proxy.dispose();
        }

        @Override
        public boolean isDisposed() {
            return disposable.isDisposed();
        }
    }

    private class ViewModelSubscription extends Subscription<WguSchedulerViewModel> {

        @Nullable
        private Disposable disposable;

        ViewModelSubscription(@NonNull Action onComplete, @NonNull WguSchedulerViewModel owner) {
            super(onComplete, owner);
        }

        @Override
        public synchronized void onDisposed() {
            if (null != disposable && !disposable.isDisposed()) {
                disposable.dispose();
            }
        }

        synchronized boolean attachTargetListener(@NonNull WguSchedulerViewModel owner) {
            if (owner.completableSubject.hasComplete()) {
                return false;
            }
            disposable = owner.completableSubject.subscribe(this::dispose);
            return true;
        }
    }

    private class LifecycleOwnerSubscription extends Subscription<LifecycleOwner> implements LifecycleEventObserver {

        @Nullable
        private Lifecycle lifecycle;

        private LifecycleOwnerSubscription(@NonNull Action onComplete, @NonNull LifecycleOwner target) {
            super(onComplete, target);
        }

        @Override
        public void onDisposed() {
            if (null != lifecycle) {
                lifecycle.removeObserver(this);
            }
        }

        synchronized boolean attachTargetListener(@NonNull LifecycleOwner owner) {
            Lifecycle l = owner.getLifecycle();
            if (null == l || l.getCurrentState() == Lifecycle.State.DESTROYED) {
                return false;
            }
            lifecycle = l;
            lifecycle.addObserver(this);
            return true;
        }

        @Override
        public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
            if (event == Lifecycle.Event.ON_DESTROY) {
                dispose();
            }
        }
    }
}
