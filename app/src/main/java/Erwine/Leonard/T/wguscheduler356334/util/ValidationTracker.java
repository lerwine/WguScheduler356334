package Erwine.Leonard.T.wguscheduler356334.util;

import android.media.MediaDrm;
import android.speech.SpeechRecognizer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.room.Ignore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public class ValidationTracker<T extends ValidationTracker.ValidationTrackable> extends ArrayList<T> {

    public static boolean isValid(ValidationTrackable source) {
        return source.valid;
    }

    public static void setValid(ValidationTrackable source, boolean valid) {
        source.setValid(valid);
    }

    private final LiveBoolean liveValid;
    private final LiveBoolean liveEmpty;
    private ValidationTrackable firstValid;
    private ValidationTrackable firstInvalid;
    private ValidationTrackable lastValid;
    private ValidationTrackable lastInvalid;

    public ValidationTracker(int initialCapacity) {
        super(initialCapacity);
        liveValid = new LiveBoolean(false);
        liveEmpty = new LiveBoolean(true);
    }

    public ValidationTracker() {
        super();
        liveValid = new LiveBoolean(false);
        liveEmpty = new LiveBoolean(true);
    }

    public ValidationTracker(@NonNull Collection<? extends T> c) {
        super(c);
        if (c.isEmpty()) {
            liveValid = new LiveBoolean(false);
            liveEmpty = new LiveBoolean(true);
        } else {
            c.forEach(node -> {
                if (null != ((ValidationTrackable) node).tracker) {
                    throw new IllegalStateException();
                }
                link(node, false);
            });
            liveValid = new LiveBoolean(null != firstValid && null == firstInvalid);
            liveEmpty = new LiveBoolean(null == firstValid && null == firstInvalid);
        }
    }

    private synchronized void onValidationChanged(ValidationTrackable node) {
        if (unlink(node, false)) {
            if (node.valid) {
                link(node, false);
                liveValid.post(true);
            } else {
                link(node, false);
            }
        } else if (link(node, false) && !node.valid) {
            liveValid.post(false);
        }
    }

    private ValidationTrackable assertOrphaned(ValidationTrackable element) {
        if (null != element.tracker) {
            throw new IllegalStateException();
        }
        return  element;
    }
    private synchronized boolean unlink(ValidationTrackable element, boolean isSizeChange) {
        if (null == element.tracker || element.tracker != this) {
            throw new IllegalStateException();
        }
        element.tracker = null;
        if (null == element.next) {
            if (element.valid) {
                if (null == (lastValid = element.previous)) {
                    firstValid = null;
                    if (isSizeChange && null == firstInvalid) {
                        liveEmpty.post(true);
                        liveValid.post(false);
                    }
                    return true;
                }
            } else if (null == (lastInvalid = element.previous)) {
                firstInvalid = null;
                if (isSizeChange) {
                    if (null != firstValid) {
                        liveValid.post(true);
                    } else {
                        liveEmpty.post(true);
                    }
                }
                return true;
            }
            element.previous = element.previous.next = null;
        } else {
            if (null == (element.next.previous = element.previous)) {
                if (element.valid) {
                    firstValid = element.next;
                } else {
                    firstInvalid = element.next;
                }
            } else {
                element.previous.next = element.next;
                element.previous = null;
            }
            element.next = null;
        }
        return false;
    }
    private synchronized boolean link(ValidationTrackable element, boolean isSizeChange) {
        if (null != element.tracker || element.tracker != this) {
            throw new IllegalStateException();
        }
        if (element.valid) {
            element.tracker = this;
            if (null == (element.previous = lastValid)) {
                firstValid = lastValid = element;
                if (isSizeChange && null == firstInvalid) {
                    liveValid.post(true);
                    liveEmpty.post(false);
                }
                return true;
            }
            lastValid = element;
        } else {
            if (null == (element.previous = lastInvalid)) {
                firstInvalid = lastInvalid = element;
                if (isSizeChange) {
                    if (null != firstValid) {
                        liveValid.post(false);
                    } else {
                        liveEmpty.post(false);
                    }
                }
                return true;
            }
            lastInvalid = element;
        }
        element.previous.next = element;
        return false;
    }
    private synchronized void replace(ValidationTrackable target, ValidationTrackable newElement) {
        if (null == target.tracker || target.tracker != this) {
            throw new IllegalStateException();
        }
        if (null != newElement.tracker) {
            if (newElement != target) {
                throw new IllegalStateException();
            }
            return;
        }
        newElement.tracker = target.tracker;
        target.tracker = null;
        if (target.valid == newElement.valid) {
            if (null == (newElement.previous = target.previous)) {
                if (newElement.valid) {
                    firstValid = newElement;
                } else {
                    firstInvalid = newElement;
                }
            } else {
                newElement.previous.next = newElement;
                target.previous = null;
            }
            if (null == (newElement.next = target.next)) {
                if (newElement.valid) {
                    lastValid = newElement;
                } else {
                    lastInvalid = newElement;
                }
            } else {
                newElement.next.previous = newElement;
                target.next = null;
            }
        } else if (newElement.valid) {
            if (unlink(target, false)) {
                link(newElement, false);
                liveValid.post(true);
            } else {
                link(newElement, false);
            }
        } else if (link(newElement, false)) {
            unlink(target, false);
            liveValid.post(false);
        } else {
            unlink(target, false);
        }
    }
    @Override
    public synchronized T set(int index, T element) {
        assertOrphaned(element);
        T replaced = super.set(index, element);
        replace(replaced, element);
        return replaced;
    }

    @Override
    public synchronized boolean add(T t) {
        assertOrphaned(t);
        if (super.add(t)) {
            link(t, true);
            return true;
        }
        return false;
    }

    @Override
    public synchronized void add(int index, T element) {
        assertOrphaned(element);
        super.add(index, element);
        link(element, true);
    }

    @Override
    public synchronized T remove(int index) {
        T result = super.remove(index);
        unlink(result, true);
        return result;
    }

    @Override
    public synchronized boolean remove(@Nullable Object o) {
        if (super.remove(o)) {
            unlink((ValidationTrackable) o, true);
            return true;
        }
        return false;
    }

    @Override
    public synchronized void clear() {
        if (isEmpty()) {
            return;
        }
        List<T> removed = stream().collect(Collectors.toList());
        super.clear();
        removed.forEach(t -> {
            ValidationTrackable node = (ValidationTrackable) t;
            node.tracker = null;
            node.next = node.previous = null;
        });
    }

    private void ensureLinked() {
        forEach(t -> {
            if (null == ((ValidationTrackable) t).tracker) {
                link(t, true);
            }
        });
    }
    @Override
    public synchronized boolean addAll(@NonNull Collection<? extends T> c) {
        if (super.addAll(c)) {
            ensureLinked();
            return true;
        }
        return false;
    }

    @Override
    public synchronized boolean addAll(int index, @NonNull Collection<? extends T> c) {
        if (super.addAll(index, c)) {
            ensureLinked();
            return true;
        }
        return false;
    }

    private void checkRemove(List<T> items) {
        items.forEach(t -> {
            if (!contains(t)) {
                unlink(t, true);
            }
        });
    }
    @Override
    public synchronized void removeRange(int fromIndex, int toIndex) {
        if (!isEmpty()) {
            List<T> items = stream().collect(Collectors.toList());
            super.removeRange(fromIndex, toIndex);
            checkRemove(items);
        }
    }

    @Override
    public synchronized boolean removeAll(@NonNull Collection<?> c) {
        if (!isEmpty()) {
            List<T> items = stream().collect(Collectors.toList());
            if (super.removeAll(c)) {
                checkRemove(items);
                return true;
            }
        }
        return false;
    }

    @Override
    public synchronized boolean retainAll(@NonNull Collection<?> c) {
        if (!isEmpty()) {
            List<T> items = stream().collect(Collectors.toList());
            if (super.retainAll(c)) {
                checkRemove(items);
                return true;
            }
        }
        return false;
    }

    @Override
    public synchronized void replaceAll(@NonNull UnaryOperator<T> operator) {
        List<T> items = stream().collect(Collectors.toList());
        super.replaceAll(operator);
        checkRemove(items);
        ensureLinked();
    }

    private class LiveBoolean extends LiveData<Boolean> {
        private boolean postedValue;
        private LiveBoolean(boolean initialValue) {
            super(initialValue);
            postedValue = initialValue;
        }

        public synchronized void post(boolean b) {
            if (b != postedValue) {
                postedValue = b;
                postValue(b);
            }
        }
    }

    public static class ValidationTrackable {
        @Ignore
        private ValidationTracker<? extends ValidationTrackable> tracker;
        @Ignore
        private ValidationTrackable previous;
        @Ignore
        private ValidationTrackable next;
        @Ignore
        private boolean valid;

        public ValidationTrackable(boolean initialValue) {
            valid = initialValue;
        }

        private synchronized void setValid(boolean value) {
            if (valid != value) {
                valid = value;
                if (null != tracker) {
                    tracker.onValidationChanged(this);
                }
            }
        }
    }

}
