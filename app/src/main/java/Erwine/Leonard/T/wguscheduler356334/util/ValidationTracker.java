package Erwine.Leonard.T.wguscheduler356334.util;

import android.speech.SpeechRecognizer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.room.Ignore;

import java.util.ArrayList;
import java.util.Collection;

public class ValidationTracker<T extends ValidationTracker.ValidationTrackable> extends ArrayList<T> {
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
            liveValid = new LiveBoolean(c.stream().reduce(Boolean.FALSE, (b, t) -> {
                checkAttached(t);
                return !((ValidationTrackable) t).valid;
            }, (a, b) -> a || b));
            liveEmpty = new LiveBoolean(null == firstValid && null == firstInvalid);
        }
    }

    private synchronized boolean checkAttached(ValidationTrackable node) {
        return null != node && node.checkAttached(this);
    }

    private synchronized void onValidationChanged(ValidationTrackable node) {
        if (node.valid) {
            if (null == node.next) {
                if (null == (lastInvalid = node.previous)) {
                    firstInvalid = null;
                    liveValid.post(true);
                } else {
                    lastInvalid.next = null;
                }
            } else {
                if (null == (node.next.previous = node.previous)) {
                    firstInvalid = node.next;
                } else {
                    node.previous.next = node;
                }
                node.next = null;
            }
            if (null == (node.previous = lastValid)) {
                firstValid = node;
            } else {
                lastValid = lastValid.next = node;
            }
        } else {
            if (null == node.next) {
                if (null == (lastValid = node.previous)) {
                    firstValid = null;
                } else {
                    lastValid.next = null;
                }
            } else {
                if (null == (node.next.previous = node.previous)) {
                    firstValid = node.next;
                } else {
                    node.previous.next = node;
                }
                node.next = null;
            }
            if (null == (node.previous = lastInvalid)) {
                firstInvalid = node;
                liveValid.post(false);
            } else {
                lastInvalid = lastInvalid.next = node;
            }
        }
    }

    @Override
    public T set(int index, T element) {
        return super.set(index, element);
    }

    @Override
    public boolean add(T t) {
        return super.add(t);
    }

    @Override
    public void add(int index, T element) {
        super.add(index, element);
    }

    @Override
    public T remove(int index) {
        return super.remove(index);
    }

    @Override
    public boolean remove(@Nullable Object o) {
        return super.remove(o);
    }

    @Override
    public void clear() {
        super.clear();
    }

    @Override
    public boolean addAll(@NonNull Collection<? extends T> c) {
        return super.addAll(c);
    }

    @Override
    public boolean addAll(int index, @NonNull Collection<? extends T> c) {
        return super.addAll(index, c);
    }

    @Override
    protected void removeRange(int fromIndex, int toIndex) {
        super.removeRange(fromIndex, toIndex);
    }

    @Override
    public boolean removeAll(@NonNull Collection<?> c) {
        return super.removeAll(c);
    }

    @Override
    public boolean retainAll(@NonNull Collection<?> c) {
        return super.retainAll(c);
    }

    public static boolean isValid(ValidationTrackable source) {
        return source.valid;
    }

    public static void setValid(ValidationTrackable source, boolean valid) {
        source.setValid(valid);
    }

    private class LiveBoolean extends LiveData<Boolean> {
        private LiveBoolean(boolean initialValue) {
            super(initialValue);
        }

        public void post(boolean b) {

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
        private synchronized boolean checkAttached(ValidationTracker<? extends ValidationTrackable> tracker) {
            if (null != this.tracker) {
                if (tracker != this.tracker) {
                    throw new IllegalArgumentException();
                }
                return false;
            }
            this.tracker = tracker;
            if (valid) {
                if (null == (previous = tracker.lastValid)) {
                    tracker.lastValid = tracker.firstValid = this;
                    return null == tracker.lastInvalid;
                }
                tracker.lastValid = previous.next = this;
            } else {
                if (null == (previous = tracker.lastInvalid)) {
                    tracker.firstInvalid = tracker.lastInvalid = this;
                    return true;
                }
                tracker.lastInvalid = previous.next = this;
            }
            return false;
        }
    }

}
