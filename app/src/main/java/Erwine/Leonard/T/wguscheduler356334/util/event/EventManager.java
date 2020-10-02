package Erwine.Leonard.T.wguscheduler356334.util.event;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.EventObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.stream.Stream;

public abstract class EventManager<T extends EventObject, U extends EventListener> implements AutoCloseable {

    private static final String LOG_TAG = EventManager.class.getName();

    private HashMap<String, HashSet<WeakReference<U>>> listenerMap = new HashMap<>();

    public synchronized boolean add(String name, U listener) {
        if (null == listenerMap) {
            throw new IllegalStateException("Event manager has been closed");
        }
        HashSet<WeakReference<U>> set;
        Objects.requireNonNull(listener);
        if (listenerMap.containsKey(Objects.requireNonNull(name))) {
            set = Objects.requireNonNull(listenerMap.get(name));
            Iterator<WeakReference<U>> iterator = Objects.requireNonNull(listenerMap.get(name)).iterator();
            while (iterator.hasNext()) {
                WeakReference<U> r = iterator.next();
                U e = r.get();
                if (null == e) {
                    iterator.remove();
                } else if (e == listener) {
                    return false;
                }
            }
        } else {
            set = new HashSet<>();
            listenerMap.put(name, set);
        }
        set.add(new WeakReference<>(listener));
        return true;
    }

    public synchronized boolean isListening(U listener) {
        if (null == listenerMap) {
            throw new IllegalStateException("Event manager has been closed");
        }
        if (null != listener) {
            for (String name : new ArrayList<>(listenerMap.keySet())) {
                Iterator<WeakReference<U>> iterator = Objects.requireNonNull(listenerMap.get(name)).iterator();
                boolean isEmpty = true;
                while (iterator.hasNext()) {
                    WeakReference<U> r = iterator.next();
                    U e = r.get();
                    if (null == e) {
                        iterator.remove();
                    } else if (e != listener) {
                        isEmpty = false;
                    } else {
                        return true;
                    }
                }
                if (isEmpty) {
                    listenerMap.remove(name);
                }
            }
        }
        return false;
    }

    public synchronized boolean isListening(String name, U listener) {
        if (null == listenerMap) {
            throw new IllegalStateException("Event manager has been closed");
        }
        if (null != listener && null != name && listenerMap.containsKey(name)) {
            Iterator<WeakReference<U>> iterator = Objects.requireNonNull(listenerMap.get(name)).iterator();
            boolean isEmpty = true;
            while (iterator.hasNext()) {
                WeakReference<U> r = iterator.next();
                U e = r.get();
                if (null == e) {
                    iterator.remove();
                } else if (e != listener) {
                    isEmpty = false;
                } else {
                    return true;
                }
            }
            if (isEmpty) {
                listenerMap.remove(name);
            }
        }
        return false;
    }

    public synchronized boolean remove(U listener) {
        if (null == listenerMap) {
            throw new IllegalStateException("Event manager has been closed");
        }
        boolean wasRemoved = false;
        if (null != listener) {
            for (String name : new ArrayList<>(listenerMap.keySet())) {
                Iterator<WeakReference<U>> iterator = Objects.requireNonNull(listenerMap.get(name)).iterator();
                boolean isEmpty = true;
                while (iterator.hasNext()) {
                    WeakReference<U> r = iterator.next();
                    U e = r.get();
                    if (null == e) {
                        iterator.remove();
                    } else {
                        isEmpty = false;
                        if (e == listener) {
                            iterator.remove();
                            wasRemoved = true;
                        }
                    }
                }
                if (isEmpty) {
                    listenerMap.remove(name);
                }
            }
        }
        return wasRemoved;
    }

    public synchronized boolean remove(String name, U listener) {
        if (null == listenerMap) {
            throw new IllegalStateException("Event manager has been closed");
        }
        if (null != listener && null != name && listenerMap.containsKey(name)) {
            Iterator<WeakReference<U>> iterator = Objects.requireNonNull(listenerMap.get(name)).iterator();
            boolean isEmpty = true;
            while (iterator.hasNext()) {
                WeakReference<U> r = iterator.next();
                U e = r.get();
                if (null == e) {
                    iterator.remove();
                } else if (e == listener) {
                    iterator.remove();
                    return true;
                } else {
                    isEmpty = false;
                }
            }
            if (isEmpty) {
                listenerMap.remove(name);
            }
        }
        return false;
    }

    public synchronized boolean removeAll(String name) {
        if (null == listenerMap) {
            throw new IllegalStateException("Event manager has been closed");
        }
        if (listenerMap.containsKey(name)) {
            listenerMap.remove(name);
            return true;
        }
        return false;
    }

    public synchronized boolean hasListeners(String name) {
        if (null == listenerMap) {
            throw new IllegalStateException("Event manager has been closed");
        }
        if (null != name && listenerMap.containsKey(name)) {
            Iterator<WeakReference<U>> iterator = Objects.requireNonNull(listenerMap.get(name)).iterator();
            boolean isEmpty = true;
            while (iterator.hasNext()) {
                WeakReference<U> r = iterator.next();
                U listener = r.get();
                if (null != listener) {
                    return true;
                }
                iterator.remove();
            }
            listenerMap.remove(name);
        }
        return false;
    }

    public synchronized Stream<U> getListeners(String name) {
        if (null == listenerMap) {
            throw new IllegalStateException("Event manager has been closed");
        }
        Stream.Builder<U> builder = Stream.builder();
        if (null != name && listenerMap.containsKey(name)) {
            Iterator<WeakReference<U>> iterator = Objects.requireNonNull(listenerMap.get(name)).iterator();
            boolean isEmpty = true;
            while (iterator.hasNext()) {
                WeakReference<U> r = iterator.next();
                U listener = r.get();
                if (null == listener) {
                    iterator.remove();
                } else {
                    isEmpty = false;
                    builder.accept(listener);
                }
            }
            if (isEmpty) {
                listenerMap.remove(name);
            }
        }
        return builder.build();
    }

    /**
     * Notifies listeners associated with the specified event name of a specified event. This method ensures that all listeners are notified, even if one of them throws
     * an exception. Any exceptions thrown will be logged and handled by the current {@link Thread#getUncaughtExceptionHandler()}.
     *
     * @param name  The name of the event.
     * @param event The subject event.
     */
    public void notifyListeners(String name, T event) {
        Objects.requireNonNull(event);
        Iterator<U> iterator = getListeners(Objects.requireNonNull(name)).iterator();
        if (!iterator.hasNext()) {
            return;
        }

        do {
            try {
                notifyListener(iterator.next(), event);
            } catch (@SuppressWarnings("UseSpecificCatch") Throwable ex) {
                Thread.UncaughtExceptionHandler eh = Thread.currentThread().getUncaughtExceptionHandler();
                if (null != eh) {
                    eh.uncaughtException(Thread.currentThread(), ex);
                }
            }
        } while (iterator.hasNext());
    }

    protected abstract void notifyListener(U listener, T event) throws Exception;

    @Override
    public synchronized void close() {
        listenerMap = null;
    }
}
