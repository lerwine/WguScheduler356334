package Erwine.Leonard.T.wguscheduler356334.util;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class IndexedStringList extends AbstractList<IndexedStringList.Item> {

    private final ArrayList<Item> backingList;

    public IndexedStringList(Collection<String> content) {
        this();
        if (null != content && !content.isEmpty()) {
            int index = 0;
            Iterator<String> iterator = content.iterator();
            while (iterator.hasNext()) {
                Item item = new Item(iterator.next());
                if (backingList.add(item)) {
                    item.number.updateValue(index);;
                }
            }
        }
    }

    public IndexedStringList() {
        backingList = new ArrayList<>();
    }

    @Override
    public synchronized boolean add(Item item) {
        if (null != item.number.getValue()) {
            throw new IllegalStateException();
        }
        if (backingList.add(item)) {
            item.number.updateValue(backingList.size());
            return true;
        }
        return false;
    }

    public synchronized boolean addValue(Collection<String> value) {
        boolean result = false;
        for (String v : value) {
            Item item = new Item(v);
            if (backingList.add(item)) {
                item.number.updateValue(backingList.size());
                result = true;
            }
        }
        return result;
    }

    @Override
    public synchronized void clear() {
        if (backingList.isEmpty()) {
            return;
        }
        backingList.forEach((t) -> t.number.updateValue(null));
        super.clear();
        if (!backingList.isEmpty()) {
            for (int i = 0; i < backingList.size(); i++) {
                backingList.get(i).number.updateValue(i + 1);
            }
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
            for (int i = index; i < backingList.size(); i++) {
                backingList.get(i).number.updateValue(i + 1);
            }
        }
        return result;
    }

    @Override
    public synchronized boolean remove(@Nullable Object o) {
        if (null != o && o instanceof Item) {
            Item item = (Item) o;
            if (null != item.number.getValue()) {
                int index = item.number.getValue() - 1;
                if (index < backingList.size() && backingList.get(index) == item && null != (item = backingList.remove(index))) {
                    item.number.updateValue(null);
                    for (int i = index; i < backingList.size(); i++) {
                        backingList.get(i).number.updateValue(i + 1);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public int size() {
        return backingList.size();
    }

    @Override
    public boolean isEmpty() {
        return backingList.isEmpty();
    }

    private class NumberData extends LiveData<Integer> {
        private NumberData() {
            super(null);
        }

        private void updateValue(Integer value) {
            postValue(value);
        }
    }

    private class ContentData extends MutableLiveData<String> {
        private ContentData(String content) {
            super(Values.asNonNullAndWsNormalized(content));
        }

        @Override
        public void setValue(String value) {
            super.setValue(Values.asNonNullAndWsNormalized(value));
        }
    }

    public class Item {
        private final NumberData number;
        private final ContentData content;

        public Item(String content) {
            number = new NumberData();
            this.content = new ContentData(content);
        }

        public LiveData<Integer> getNumber() {
            return number;
        }

        public MutableLiveData<String> getContent() {
            return content;
        }

    }
}
