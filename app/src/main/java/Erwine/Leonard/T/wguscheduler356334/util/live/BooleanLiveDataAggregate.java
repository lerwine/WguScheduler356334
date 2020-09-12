package Erwine.Leonard.T.wguscheduler356334.util.live;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import java.util.AbstractSet;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class BooleanLiveDataAggregate extends AbstractSet<LiveData<Boolean>> implements AutoCloseable {
    private final Container trueContainer;
    private final Container falseContainer;
    private final Container nullContainer;
    private final BooleanLiveData emptyLiveData;
    private AllFalseBooleanLiveData allTrueLiveData;
    private AllFalseBooleanLiveData allFalseLiveData;
    private AllFalseBooleanLiveData allNullLiveData;
    private Node first;
    private Node last;
    private int size;
    private Object mutationKey;

    protected BooleanLiveDataAggregate() {
        super();
        emptyLiveData = new BooleanLiveData(true);
        mutationKey = new Object();
        trueContainer = new Container();
        falseContainer = new Container();
        nullContainer = new Container();
        allTrueLiveData = new AllFalseBooleanLiveData(falseContainer.nonEmptyLiveData, nullContainer.nonEmptyLiveData);
        allFalseLiveData = new AllFalseBooleanLiveData(trueContainer.nonEmptyLiveData, nullContainer.nonEmptyLiveData);
        allNullLiveData = new AllFalseBooleanLiveData(trueContainer.nonEmptyLiveData, falseContainer.nonEmptyLiveData);
    }

    public LiveData<Boolean> getEmptyLiveData() {
        return emptyLiveData;
    }

    public LiveData<Boolean> getAnyTrueLiveData() {
        return trueContainer.nonEmptyLiveData;
    }

    public LiveData<Boolean> getAllTrueLiveData() {
        return allTrueLiveData;
    }

    public LiveData<Boolean> getAnyFalseLiveData() {
        return falseContainer.nonEmptyLiveData;
    }

    public LiveData<Boolean> getAllFalseLiveData() {
        return allFalseLiveData;
    }

    public LiveData<Boolean> getAnyNullLiveData() {
        return nullContainer.nonEmptyLiveData;
    }

    public LiveData<Boolean> getAllNullLiveData() {
        return allNullLiveData;
    }

    @Nullable
    private Node getNode(LiveData<?> o) {
        for (Node node = first; null != node; node = node.nextInSet) {
            if (node.liveData == o) {
                return node;
            }
        }
        return null;
    }

    @Override
    public boolean isEmpty() {
        return null == first;
    }

    @Override
    public synchronized boolean add(LiveData<Boolean> e) {
        if (null == e) {
            return false;
        }
        Node node;
        for (node = first; null != node; node = node.nextInSet) {
            if (node.liveData == e) {
                return false;
            }
        }
        node = new Node(e);
        mutationKey = new Object();
        node.addToSet();
        return true;
    }

    @Override
    public boolean remove(Object o) {
        Node node;
        if (o instanceof LiveData && null != (node = getNode((LiveData<?>) o))) {
            mutationKey = new Object();
            node.removeFromSet();
            return true;
        }
        return false;
    }

    private synchronized Object concurrentRemove(Node node, Object changeKey) {
        if (null == changeKey) {
            throw new IllegalStateException();
        }
        if (changeKey != mutationKey) {
            throw new ConcurrentModificationException();
        }
        if (null == node) {
            throw new IllegalStateException();
        }
        mutationKey = new Object();
        node.removeFromSet();
        return mutationKey;
    }

    @Override
    public synchronized void clear() {
        if (null != first) {
            mutationKey = new Object();
            try {
                first.closeToNext();
            } finally {
                first = last = trueContainer.latest = falseContainer.latest = nullContainer.latest = null;
                size = 0;
                trueContainer.nonEmptyLiveData.post(false);
                falseContainer.nonEmptyLiveData.post(false);
                nullContainer.nonEmptyLiveData.post(false);
            }
        }
    }

    @NonNull
    @Override
    public Iterator<LiveData<Boolean>> iterator() {
        return new IteratorImpl();
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void close() {
        if (null != first) {
            first.closeToNext();
            first = last = trueContainer.latest = falseContainer.latest = nullContainer.latest = null;
            size = 0;
        }
        if (null != allTrueLiveData) {
            allTrueLiveData.close();
            allFalseLiveData.close();
            allNullLiveData.close();
            allTrueLiveData = null;
        }
    }

    private class IteratorImpl implements Iterator<LiveData<Boolean>> {
        private Object changeKey;
        private Node current;
        private Node next;

        @Override
        public synchronized boolean hasNext() {
            if (null == changeKey) {
                changeKey = mutationKey;
                next = first;
            } else if (changeKey != mutationKey) {
                throw new ConcurrentModificationException();
            }
            return null != next;
        }

        @Override
        public synchronized LiveData<Boolean> next() throws NoSuchElementException, ConcurrentModificationException {
            if (null == changeKey) {
                changeKey = mutationKey;
                next = first;
            } else if (changeKey != mutationKey) {
                throw new ConcurrentModificationException();
            }
            if (null == (current = next)) {
                throw new NoSuchElementException();
            }
            next = next.nextInSet;
            return current.liveData;
        }

        @Override
        public synchronized void remove() {
            Node node = current;
            current = null;
            changeKey = concurrentRemove(node, changeKey);
        }
    }

    private class Container {
        private final BooleanLiveData nonEmptyLiveData;
        //        private Node first;
        private Node latest;

        Container() {
            nonEmptyLiveData = new BooleanLiveData(false);
        }
    }

    private class Node implements AutoCloseable {
        private final LiveData<Boolean> liveData;
        private Observer<Boolean> observer;
        private Boolean groupValue;
        private Node previousInContainer;
        private Node previousInSet;
        private Node nextInContainer;
        private Node nextInSet;
        private Container container;

        Node(LiveData<Boolean> liveData) {
            this.liveData = liveData;
            observer = this::onValueChanged;
        }

        private synchronized void onValueChanged(Boolean value) {
            if (null == value) {
                if (null != groupValue) {
                    groupValue = null;
                    containerUnlink();
                    containerLink(nullContainer);
                }
            } else if (null == groupValue || groupValue != value) {
                groupValue = value;
                containerUnlink();
                containerLink((value) ? trueContainer : falseContainer);
            }
        }

        synchronized void containerLink(Container container) {
            this.container = container;
            if (null != (previousInContainer = container.latest)) {
                container.latest = previousInContainer.nextInContainer = this;
            } else {
                container.latest = this;
                container.nonEmptyLiveData.post(true);
            }
        }

        synchronized void containerUnlink() {
            if (null == nextInContainer) {
                if (null == (container.latest = previousInContainer)) {
                    container.nonEmptyLiveData.post(false);
                } else {
                    previousInContainer = previousInContainer.nextInContainer = null;
                }
            } else {
                if (null != (nextInContainer.previousInContainer = previousInContainer)) {
                    previousInContainer.nextInContainer = nextInContainer;
                    previousInContainer = null;
                }
                nextInContainer = null;
            }
            container = null;
        }

        void removeFromSet() {
            if (null == nextInSet) {
                if (null == (last = previousInSet)) {
                    first = null;
                } else {
                    previousInSet = previousInSet.nextInSet = null;
                }
            } else {
                if (null == (nextInSet.previousInSet = previousInSet)) {
                    first = nextInSet;
                } else {
                    previousInSet.nextInSet = nextInSet;
                    previousInSet = null;
                }
                nextInSet = null;
            }
            size--;
            containerUnlink();
            liveData.removeObserver(observer);
        }

        synchronized void closeToNext() {
            try {
                if (null != observer) {
                    liveData.removeObserver(observer);
                }
            } finally {
                observer = null;
                if (null != nextInSet) {
                    nextInSet.closeToNext();
                }
            }
        }

        synchronized void addToSet() {
            if (null == (previousInSet = last)) {
                first = this;
            } else {
                previousInSet.nextInSet = this;
            }
            last = this;
            size++;
            groupValue = liveData.getValue();
            containerLink((null == groupValue) ? nullContainer : ((groupValue) ? trueContainer : falseContainer));
            liveData.observeForever(observer);
        }

        @Override
        public void close() {
            if (null != observer) {
                liveData.removeObserver(observer);
                observer = null;
            }
        }
    }

    private static class BooleanLiveData extends LiveData<Boolean> {
        BooleanLiveData(boolean initialValue) {
            super(initialValue);
        }

        private void post(boolean value) {
            postValue(value);
        }
    }

    private class AllFalseBooleanLiveData extends LiveData<Boolean> implements AutoCloseable {
        private final LiveData<Boolean> firstLiveData;
        private boolean currentFirst;
        private final LiveData<Boolean> secondLiveData;
        private boolean currentSecond;
        private boolean currentEmpty;
        private Observer<Boolean> firstChanged;
        private Observer<Boolean> secondChanged;
        private Observer<Boolean> emptyChanged;
        private boolean postedValue;

        @SuppressWarnings("ConstantConditions")
        AllFalseBooleanLiveData(@NonNull LiveData<Boolean> firstLiveData, @NonNull LiveData<Boolean> secondLiveData) {
            super(!(firstLiveData.getValue() || secondLiveData.getValue() || emptyLiveData.getValue()));
            postedValue = getValue();
            currentFirst = (this.firstLiveData = firstLiveData).getValue();
            currentSecond = (this.secondLiveData = secondLiveData).getValue();
            currentEmpty = emptyLiveData.getValue();
            firstChanged = t -> {
                currentFirst = t;
                onChange();
            };
            secondChanged = t -> {
                currentSecond = t;
                onChange();
            };
            emptyChanged = t -> {
                currentEmpty = t;
                onChange();
            };
            firstLiveData.observeForever(firstChanged);
            secondLiveData.observeForever(secondChanged);
            emptyLiveData.observeForever(emptyChanged);
        }

        private synchronized void onChange() {
            boolean v = !(currentFirst || currentSecond || currentEmpty);
            if (v != postedValue) {
                postedValue = v;
                postValue(v);
            }
        }

        @Override
        public void close() {
            if (null != firstChanged) {
                firstLiveData.removeObserver(firstChanged);
                secondLiveData.removeObserver(secondChanged);
                emptyLiveData.removeObserver(emptyChanged);
                firstChanged = null;
            }
        }
    }

}
