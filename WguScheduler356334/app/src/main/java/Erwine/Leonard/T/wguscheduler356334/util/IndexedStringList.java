package Erwine.Leonard.T.wguscheduler356334.util;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.lang.ref.WeakReference;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;

public final class IndexedStringList extends AbstractList<IndexedStringList.Item> {

    private static final Function<String, String> SINGLE_LINE_NORMALIZER = StringHelper.getNormalizer(StringNormalizationOption.SINGLE_LINE);

    private final Observer<Item> contentChangeObserver;

    private final ArrayList<Item> backingList;
    private final Observer<Item> emptyStateChangeObserver;
    private final LiveBoolean anyElementNonEmpty;
    private final ArrayList<WeakReference<Observer<IndexedStringList>>> changeObservers = new ArrayList<>();

    public IndexedStringList(Collection<String> content) {
        this();
        if (null != content && !content.isEmpty()) {
            int index = 0;
            Iterator<String> iterator = content.iterator();
            boolean n = false;
            while (iterator.hasNext()) {
                Item item = new Item(iterator.next());
                if (backingList.add(item)) {
                    item.observeContentChange(contentChangeObserver);
                    item.observeEmptyChange(emptyStateChangeObserver);
                    item.lineNumber.onNext(++index);
                    if (!item.empty.currentValue) {
                        n = true;
                    }
                }
            }
            if (n) {
                anyElementNonEmpty.set(true);
            }
        }
    }

    public IndexedStringList() {
        backingList = new ArrayList<>();
        anyElementNonEmpty = new LiveBoolean(false);
        contentChangeObserver = item -> raiseListChanged();
        emptyStateChangeObserver = item -> {
            if (item.empty.currentValue) {
                if (backingList.stream().allMatch(t -> t.empty.currentValue)) {
                    anyElementNonEmpty.set(false);
                }
            } else {
                anyElementNonEmpty.set(true);
            }
        };
    }

    public static IndexedStringList of(String lines) {
        IndexedStringList list = new IndexedStringList();
        list.setText(lines);
        return list;
    }

    private static <T> void notifyAllObservers(T item, Iterator<Observer<T>> iterator) {
        if (iterator.hasNext()) {
            try {
                iterator.next().onChanged(item);
            } finally {
                notifyAllObservers(item, iterator);
            }
        }
    }

    private static <T> boolean addObserver(Observer<T> observer, ArrayList<WeakReference<Observer<T>>> target) {
        if (null == observer) {
            return false;
        }
        Iterator<WeakReference<Observer<T>>> iterator = target.iterator();
        while (iterator.hasNext()) {
            Observer<T> o = iterator.next().get();
            if (null == o)
                iterator.remove();
            else if (o == observer) {
                return false;
            }
        }
        target.add(new WeakReference<>(observer));
        return true;
    }

    private static <T> boolean removeObserver(Observer<T> observer, ArrayList<WeakReference<Observer<T>>> target) {
        if (null != observer) {
            Iterator<WeakReference<Observer<T>>> iterator = target.iterator();
            while (iterator.hasNext()) {
                Observer<T> o = iterator.next().get();
                if (null == o)
                    iterator.remove();
                else if (o == observer) {
                    iterator.remove();
                    return true;
                }
            }
        }
        return false;
    }

    private static <T> ArrayList<Observer<T>> getObserverObjects(ArrayList<WeakReference<Observer<T>>> target) {
        ArrayList<Observer<T>> result = new ArrayList<>();
        Iterator<WeakReference<Observer<T>>> iterator = target.iterator();
        while (iterator.hasNext()) {
            Observer<T> o = iterator.next().get();
            if (null == o) {
                iterator.remove();
            } else {
                result.add(o);
            }
        }
        return result;
    }

    private void raiseListChanged() {
        if (!changeObservers.isEmpty()) {
            ArrayList<Observer<IndexedStringList>> observers = getObserverObjects(changeObservers);
            notifyAllObservers(this, observers.iterator());
        }
    }

    public synchronized boolean observeListChange(Observer<IndexedStringList> observer) {
        return addObserver(observer, changeObservers);
    }

    public synchronized boolean removeListChangeObserver(Observer<IndexedStringList> observer) {
        return removeObserver(observer, changeObservers);
    }

    public boolean isAnyElementNonEmpty() {
        return anyElementNonEmpty.currentValue;
    }

    public LiveData<Boolean> anyElementNonEmpty() {
        return anyElementNonEmpty;
    }

    public synchronized String getText() {
        Iterator<String> iterator = backingList.stream().map(t -> t.normalizedValue.postedValue).filter(t -> !t.isEmpty()).iterator();
        if (iterator.hasNext()) {
            String text = iterator.next();
            if (iterator.hasNext()) {
                StringBuilder sb = new StringBuilder(text);
                do {
                    sb.append("\n").append(iterator.next());
                } while (iterator.hasNext());
                return sb.toString();
            }
            return text;
        }
        return "";
    }

    /**
     * Re-populate current {@code IndexedStringList} with lines parsed from the specified string.
     *
     * @param text The text containing lines separated by newline sequences.
     * @return {@code true} if the current {@code IndexedStringList} was changed; otherwise {@code false}.
     */
    public synchronized boolean setText(String text) {
        Optional<Stream<String>> lines = CollectionHelper.ifAnyElementDiffers(
                backingList.iterator(),
                StringLineIterator.getLines(text).iterator(), SINGLE_LINE_NORMALIZER,
                t -> !t.isEmpty(),
                (a, b) -> a.normalizedValue.postedValue.equals(b)
        );
        return lines.map(t -> {
            Iterator<String> iterator = t.iterator();
            if (iterator.hasNext()) {
                if (!backingList.isEmpty())
                    clearImpl();
                int index = 0;
                do {
                    Item item = new Item(iterator.next());
                    item.observeContentChange(contentChangeObserver);
                    item.observeEmptyChange(emptyStateChangeObserver);
                    item.lineNumber.onNext(++index);
                    backingList.add(item);
                } while (iterator.hasNext());
            } else {
                if (!backingList.isEmpty()) {
                    if (backingList.size() == 1 && backingList.get(0).normalizedValue.postedValue.isEmpty())
                        return false;
                    clearImpl();
                }
                Item item = new Item("");
                item.observeContentChange(contentChangeObserver);
                item.observeEmptyChange(emptyStateChangeObserver);
                item.lineNumber.onNext(1);
                backingList.add(item);
            }
            raiseListChanged();
            return true;
        }).orElse(false);
    }

    @Override
    public synchronized boolean add(Item item) {
        if (null != item.lineNumber.getValue()) {
            throw new IllegalStateException();
        }
        if (backingList.add(item)) {
            item.lineNumber.onNext(backingList.size());
            item.observeContentChange(contentChangeObserver);
            item.observeEmptyChange(emptyStateChangeObserver);
            if (!item.empty.currentValue) {
                anyElementNonEmpty.set(true);
            }
            return true;
        }
        return false;
    }

    public synchronized boolean addValue(String... value) {
        boolean result = false;
        boolean n = false;
        int index = backingList.size();
        for (String v : value) {
            Item item = new Item(v);
            if (backingList.add(item)) {
                item.lineNumber.onNext(++index);
                item.observeContentChange(contentChangeObserver);
                item.observeEmptyChange(emptyStateChangeObserver);
                if (!item.empty.currentValue) {
                    n = true;
                }
                result = true;
            }
        }
        if (n) {
            anyElementNonEmpty.set(true);
        }
        if (result) {
            raiseListChanged();
        }
        return result;
    }

    @Override
    public Item set(int index, Item element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(int index, Item element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Item get(int i) {
        return backingList.get(i);
    }

    @Override
    public boolean equals(@Nullable Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return backingList.hashCode();
    }

    @Override
    public int size() {
        return backingList.size();
    }

    @Override
    public boolean isEmpty() {
        return backingList.isEmpty();
    }

    private void clearImpl() {
        backingList.forEach((t) -> {
            t.removeContentChangeObserver(contentChangeObserver);
            t.removeEmptyChangeObserver(emptyStateChangeObserver);
            t.lineNumber.onNext(-1);
        });
        super.clear();
    }

    @Override
    public synchronized void clear() {
        if (backingList.isEmpty()) {
            return;
        }
        clearImpl();
        if (backingList.isEmpty()) {
            anyElementNonEmpty.set(false);
        } else {
            // Property will never happen, but just in case ...
            boolean n = false;
            for (int i = 0; i < backingList.size(); i++) {
                Item v = backingList.get(i);
                if (!v.empty.currentValue) {
                    n = true;
                }
                v.observeContentChange(contentChangeObserver);
                v.observeEmptyChange(emptyStateChangeObserver);
                v.lineNumber.onNext(i + 1);
            }
            anyElementNonEmpty.set(n);
        }
        raiseListChanged();
    }

    @Override
    public synchronized Item remove(int index) {
        Item result = backingList.remove(index);
        if (null != result) {
            result.lineNumber.onNext(-1);
            result.removeContentChangeObserver(contentChangeObserver);
            result.removeEmptyChangeObserver(emptyStateChangeObserver);
            if (result.empty.currentValue) {
                for (int i = index; i < backingList.size(); i++) {
                    backingList.get(i).lineNumber.onNext(i + 1);
                }
            } else {
                boolean n = false;
                for (int i = index; i < backingList.size(); i++) {
                    Item v = backingList.get(i);
                    if (!v.empty.currentValue) {
                        n = true;
                    }
                    v.lineNumber.onNext(i + 1);
                }
                anyElementNonEmpty.set(n);
            }
            raiseListChanged();
        }
        return result;
    }

    @Override
    public synchronized boolean remove(@Nullable Object o) {
        if (o instanceof Item) {
            Item item = (Item) o;
            if (item.lineNumber.getValue() >= 0) {
                @SuppressWarnings("ConstantConditions") int index = item.lineNumber.getValue() - 1;
                if (index < backingList.size() && backingList.get(index) == item && null != (item = backingList.remove(index))) {
                    item.lineNumber.onNext(-1);
                    item.removeContentChangeObserver(contentChangeObserver);
                    item.removeEmptyChangeObserver(emptyStateChangeObserver);
                    if (backingList.isEmpty()) {
                        anyElementNonEmpty.set(false);
                    } else if (item.empty.currentValue) {
                        for (int i = index; i < backingList.size(); i++) {
                            backingList.get(i).lineNumber.onNext(i + 1);
                        }
                    } else {
                        boolean n = false;
                        for (int i = index; i < backingList.size(); i++) {
                            Item v = backingList.get(i);
                            if (!v.empty.currentValue) {
                                n = true;
                            }
                            v.lineNumber.onNext(i + 1);
                        }
                        anyElementNonEmpty.set(n);
                    }
                    raiseListChanged();
                    return true;
                }
            }
        }
        return false;
    }

    private static class LiveBoolean extends LiveData<Boolean> {
        private boolean currentValue = false;
        LiveBoolean(boolean initialValue) {
            super(initialValue);
        }

        private synchronized void set(boolean value) {
            postValue(value);
        }

        private synchronized void setCurrentValue(boolean currentValue) {
            this.currentValue = currentValue;
        }

        @Override
        protected void setValue(Boolean value) {
            setCurrentValue(value);
            super.setValue(value);
        }

        @Override
        protected synchronized void postValue(Boolean value) {
            if (value != currentValue) {
                this.currentValue = value;
                super.postValue(value);
            }
        }
    }

//    private static class NumberData extends OptionalLiveData<Integer> {
//        private Integer currentValue;
//
//        NumberData() {
//            super(Optional.empty());
//        }
//
//        private void set(Integer value) {
//            postValue(Optional.ofNullable(value));
//        }
//
//    }

    private static class StringData extends LiveData<String> {
        private String postedValue;

        StringData(String initialValue) {
            super(SINGLE_LINE_NORMALIZER.apply(initialValue));
            postedValue = getValue();
        }

        private void set(String value) {
            postedValue = SINGLE_LINE_NORMALIZER.apply(value);
            postValue(postedValue);
        }
    }

    public static class Item {
        private final BehaviorSubject<Integer> lineNumber;
        private final MutableLiveData<String> rawValue;
        private final StringData normalizedValue;
        private final LiveBoolean empty;
        private final ArrayList<WeakReference<Observer<Item>>> contentChangeObservers = new ArrayList<>();
        private final ArrayList<WeakReference<Observer<Item>>> numberChangeObservers = new ArrayList<>();
        private final ArrayList<WeakReference<Observer<Item>>> emptyChangeObservers = new ArrayList<>();

        public Item(String content) {
            lineNumber = BehaviorSubject.create();
            normalizedValue = new StringData(content);
            empty = new LiveBoolean(normalizedValue.postedValue.isEmpty());
            rawValue = new MutableLiveData<String>(content) {
                private String postedValue;

                private synchronized boolean checkSetValue(String value) {
                    if (null == value) {
                        if (null == postedValue) {
                            return false;
                        }
                        normalizedValue.set("");
                    } else if (null == postedValue || !postedValue.equals(value)) {
                        normalizedValue.set(value);
                    }
                    return true;
                }

                @Override
                public void setValue(String value) {
                    if (checkSetValue(value)) {
                        super.setValue(value);
                    }
                }

                @Override
                public synchronized void postValue(String value) {
                    if (null == value) {
                        if (null == postedValue) {
                            return;
                        }
                        normalizedValue.set("");
                    } else {
                        if (null != postedValue && postedValue.equals(value)) {
                            return;
                        }
                        normalizedValue.set(SINGLE_LINE_NORMALIZER.apply(value));
                    }
                    postedValue = value;
                    super.postValue(value);
                }
            };
            lineNumber.subscribe(t -> raiseLineNumberChanged());
            normalizedValue.observeForever(t -> raiseContentChanged());
            empty.observeForever(t -> raiseEmptyChanged());
        }

        public Integer getLineNumber() {
            return lineNumber.getValue();
        }

        public Observable<Integer> lineNumber() {
            return lineNumber;
        }

        public String getRawValue() {
            return rawValue.getValue();
        }

        public MutableLiveData<String> rawValue() {
            return rawValue;
        }

        public String getNormalizedValue() {
            return normalizedValue.getValue();
        }

        public LiveData<String> normalizedValue() {
            return normalizedValue;
        }

        public boolean isEmpty() {
            Boolean b = empty.getValue();
            return null != b && b;
        }

        public LiveBoolean empty() {
            return empty;
        }

        private void raiseContentChanged() {
            if (!contentChangeObservers.isEmpty()) {
                ArrayList<Observer<Item>> observers = getObserverObjects(contentChangeObservers);
                notifyAllObservers(this, observers.iterator());
            }
        }

        public synchronized boolean observeContentChange(Observer<Item> observer) {
            return addObserver(observer, contentChangeObservers);
        }

        public synchronized boolean removeContentChangeObserver(Observer<Item> observer) {
            return removeObserver(observer, contentChangeObservers);
        }

        private void raiseLineNumberChanged() {
            if (!numberChangeObservers.isEmpty()) {
                ArrayList<Observer<Item>> observers = getObserverObjects(numberChangeObservers);
                notifyAllObservers(this, observers.iterator());
            }
        }

        public synchronized boolean observeLineNumberChange(Observer<Item> observer) {
            return addObserver(observer, numberChangeObservers);
        }

        public synchronized boolean removeLineNumberChangeObserver(Observer<Item> observer) {
            return removeObserver(observer, numberChangeObservers);
        }

        private void raiseEmptyChanged() {
            if (!emptyChangeObservers.isEmpty()) {
                ArrayList<Observer<Item>> observers = getObserverObjects(emptyChangeObservers);
                notifyAllObservers(this, observers.iterator());
            }
        }

        public synchronized boolean observeEmptyChange(Observer<Item> observer) {
            return addObserver(observer, emptyChangeObservers);
        }

        public synchronized boolean removeEmptyChangeObserver(Observer<Item> observer) {
            return removeObserver(observer, emptyChangeObservers);
        }

    }
}
