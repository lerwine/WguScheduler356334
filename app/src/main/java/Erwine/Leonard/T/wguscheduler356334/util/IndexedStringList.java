package Erwine.Leonard.T.wguscheduler356334.util;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class IndexedStringList extends AbstractList<IndexedStringList.Item> {

    private final ArrayList<Item> backingList;
    private final LiveBoolean anyElementNonEmpty;

    public IndexedStringList(Collection<String> content) {
        this();
        if (null != content && !content.isEmpty()) {
            int index = 0;
            Iterator<String> iterator = content.iterator();
            boolean n = false;
            while (iterator.hasNext()) {
                Item item = new Item(iterator.next());
                if (backingList.add(item)) {
                    item.number.updateValue(index);
                    if (item.nonEmpty.currentValue) {
                        n = true;
                    }
                }
            }
            if (n) {
                anyElementNonEmpty.updateValue(true);
            }
        }
    }

    public IndexedStringList() {
        backingList = new ArrayList<>();
        anyElementNonEmpty = new LiveBoolean();
    }

    public LiveData<Boolean> isAnyElementNonEmpty() {
        return anyElementNonEmpty;
    }

    @Override
    public synchronized boolean add(Item item) {
        if (null != item.number.getValue()) {
            throw new IllegalStateException();
        }
        if (backingList.add(item)) {
            item.number.updateValue(backingList.size());
            if (item.nonEmpty.currentValue) {
                anyElementNonEmpty.updateValue(true);
            }
            return true;
        }
        return false;
    }

    public synchronized boolean addValue(String... value) {
        boolean result = false;
        boolean n = false;
        for (String v : value) {
            Item item = new Item(v);
            if (backingList.add(item)) {
                item.number.updateValue(backingList.size());
                item.nonEmpty.observeForever(this::onNonEmptyChanged);
                if (item.nonEmpty.currentValue) {
                    n = true;
                }
                result = true;
            }
        }
        if (n) {
            anyElementNonEmpty.updateValue(true);
        }
        return result;
    }

    @Override
    public synchronized void clear() {
        if (backingList.isEmpty()) {
            return;
        }
        backingList.forEach((t) -> {
            t.nonEmpty.removeObserver(this::onNonEmptyChanged);
            t.number.updateValue(null);
        });
        super.clear();
        if (backingList.isEmpty()) {
            anyElementNonEmpty.updateValue(false);
        } else {
            // Property will never happen, but just in case ...
            boolean n = false;
            for (int i = 0; i < backingList.size(); i++) {
                Item v = backingList.get(i);
                if (v.nonEmpty.currentValue) {
                    n = true;
                }
                v.number.updateValue(i + 1);
            }
            anyElementNonEmpty.updateValue(n);
        }
    }

    @Override
    public Item get(int i) {
        return backingList.get(i);
    }

    @Override
    public synchronized Item remove(int index) {
        Item result = backingList.remove(index);
        if (null != result) {
            result.number.updateValue(null);
            result.nonEmpty.removeObserver(this::onNonEmptyChanged);
            if (result.nonEmpty.currentValue) {
                boolean n = false;
                for (int i = index; i < backingList.size(); i++) {
                    Item v = backingList.get(i);
                    if (v.nonEmpty.currentValue) {
                        n = true;
                    }
                    v.number.updateValue(i + 1);
                }
                anyElementNonEmpty.updateValue(n);
            } else {
                for (int i = index; i < backingList.size(); i++) {
                    backingList.get(i).number.updateValue(i + 1);
                }
            }
        }
        return result;
    }

    @Override
    public synchronized boolean remove(@Nullable Object o) {
        if (o instanceof Item) {
            Item item = (Item) o;
            if (null != item.number.getValue()) {
                int index = item.number.getValue() - 1;
                if (index < backingList.size() && backingList.get(index) == item && null != (item = backingList.remove(index))) {
                    item.number.updateValue(null);
                    item.nonEmpty.removeObserver(this::onNonEmptyChanged);
                    if (backingList.isEmpty()) {
                        anyElementNonEmpty.updateValue(false);
                    } else if (item.nonEmpty.currentValue) {
                        boolean n = false;
                        for (int i = index; i < backingList.size(); i++) {
                            Item v = backingList.get(i);
                            if (v.nonEmpty.currentValue) {
                                n = true;
                            }
                            v.number.updateValue(i + 1);
                        }
                        anyElementNonEmpty.updateValue(n);
                    } else {
                        for (int i = index; i < backingList.size(); i++) {
                            backingList.get(i).number.updateValue(i + 1);
                        }
                    }
                    return true;
                }
            }
        }
        return false;
    }

    private void onNonEmptyChanged(Boolean value) {
        if (value) {
            anyElementNonEmpty.updateValue(true);
        } else if (!backingList.stream().anyMatch((t) -> t.nonEmpty.currentValue)) {
            anyElementNonEmpty.updateValue(false);
        }
    }

    @Override
    public int size() {
        return backingList.size();
    }

    @Override
    public boolean isEmpty() {
        return backingList.isEmpty();
    }

    private static class LiveBoolean extends LiveData<Boolean> {
        private boolean currentValue = false;

        private LiveBoolean() {
            super(false);
        }

        private synchronized void updateValue(boolean value) {
            if (value != currentValue) {
                currentValue = value;
                postValue(value);
            }
        }
    }

    private static class NumberData extends LiveData<Integer> {
        private NumberData() {
            super(null);
        }

        private void updateValue(Integer value) {
            postValue(value);
        }
    }

    private static class ContentData extends MutableLiveData<String> {
        private String currentValue;

        private ContentData(String content) {
            super(Values.asNonNullAndWsNormalized(content));
            currentValue = getValue();
        }

        @Override
        public void postValue(String value) {
            super.postValue(value);
        }

        @Override
        public synchronized void setValue(String value) {
            String n = Values.asNonNullAndWsNormalized(value);
            if (!n.equals(currentValue)) {
                currentValue = n;
                super.setValue(n);
            }
        }
    }

    public static class Item {
        private final NumberData number;
        private final ContentData content;
        private final LiveBoolean nonEmpty;

        public Item(String content) {
            number = new NumberData();
            this.content = new ContentData(content);
            nonEmpty = new LiveBoolean();
            onContentChanged(content);
            this.content.observeForever(this::onContentChanged);
            //noinspection ConstantConditions
            onContentChanged(this.content.getValue());
        }

        private void onContentChanged(String s) {
            nonEmpty.updateValue(!s.isEmpty());
        }

        public LiveData<Integer> getNumber() {
            return number;
        }

        public MutableLiveData<String> getContent() {
            return content;
        }

    }
}
