package Erwine.Leonard.T.wguscheduler356334.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.AbstractList;
import java.util.Collection;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class IndexedStringItem {
    private static final Function<String, String> NORMALIZER = StringHelper.getNormalizer(StringNormalizationOption.SINGLE_LINE);
    private final LineNumber lineNumber;
    private final LiveString value;
    private Integer postedLineNumber = null;
    private String postedValue;

    public IndexedStringItem(String initialValue) {
        lineNumber = new LineNumber();
        value = new LiveString(initialValue);
    }

    public static List ofList(String lines) {
        List result = new List();
        result.setString(lines);
        return result;
    }

    @NonNull
    public LiveData<Integer> getLineNumber() {
        return lineNumber;
    }

    @NonNull
    public MutableLiveData<String> getValue() {
        return value;
    }

    public static class List extends AbstractList<IndexedStringItem> {
        private final StringValueCalculated stringValueCalculated;
        private final LineCount lineCount;
        private Object mutationKey = new Object();
        private int size = 0;
        private Node firstNode = null;
        private Node lastNode = null;
        private boolean postedStringValueCalculated = true;
        private String string = "";

        protected List() {
            super();
            lineCount = new LineCount();
            stringValueCalculated = new StringValueCalculated();
        }

        @NonNull
        public LiveData<Integer> getLineCount() {
            return lineCount;
        }

        @NonNull
        public LiveData<Boolean> isStringValueCalculated() {
            return stringValueCalculated;
        }

        @Nullable
        private synchronized Node getNode(int index) {
            if (index < 0)
                throw new IndexOutOfBoundsException("index(" + index + ") < 0");
            if (index > size)
                throw new IndexOutOfBoundsException("index(" + index + ") > size(" + size + ")");
            if (index == size)
                return null;
            Node result;
            if (index > (size >> 1)) {
                result = lastNode;
                for (int i = index + 1; i < size; i++)
                    result = result.previous;
            } else {
                result = firstNode;
                for (int i = 0; i < index; i++)
                    result = result.next;
            }
            return result;
        }

        @Nullable
        private synchronized Node getNode(IndexedStringItem element) {
            if (null != element) {
                for (Node node = firstNode; null != node; node = node.next) {
                    if (node.item.equals(element))
                        return node;
                }
            }
            return null;
        }

        private synchronized Node getInstanceNode(IndexedStringItem element) {
            if (null != element) {
                for (Node node = firstNode; null != node; node = node.next) {
                    if (node.item == element)
                        return node;
                }
            }
            return null;
        }

        private synchronized int getIndexof(@NonNull IndexedStringItem element) {
            int index = -1;
            for (Node node = firstNode; null != node; node = node.next) {
                ++index;
                if (node.item.equals(element))
                    return index;
            }
            return -1;
        }

        private synchronized int getLastIndexof(@NonNull IndexedStringItem element) {
            int index = size;
            for (Node node = lastNode; null != node; node = node.previous) {
                --index;
                if (node.item.equals(element))
                    return index;
            }
            return -1;
        }

        private synchronized Node clearAllNodes() {
            Node result = firstNode;
            firstNode = lastNode = null;
            lineCount.postValue(0);
            if (null != result)
                clearNumber(result);
            return result;
        }

        private synchronized void addNode(@NonNull IndexedStringItem element) {
            if (null == lastNode) {
                firstNode = lastNode = new Node(element);
                lineCount.postValue(1);
            } else {
                lastNode = lastNode.linkNext(element);
                lineCount.postValue(size + 1);
            }
            renumber(lastNode);
            mutationKey = new Object();
        }

        @Nullable
        private synchronized Node addNodes(@NonNull Iterator<? extends IndexedStringItem> iterator) {
            if (!iterator.hasNext()) {
                return null;
            }
            IndexedStringItem item = iterator.next();
            while (null == item || null != getInstanceNode(item)) {
                if (!iterator.hasNext())
                    return null;
            }
            Node result = new Node(item);
            int n;
            if (null == lastNode) {
                firstNode = lastNode = new Node(item);
                n = 1;
            } else {
                lastNode = lastNode.linkNext(item);
                n = size + 1;
            }
            Node renumberFrom = lastNode;
            Node previous = result;
            while (iterator.hasNext()) {
                item = iterator.next();
                if (null != item && null == getInstanceNode(item)) {
                    previous = previous.linkNext(item);
                    lastNode = lastNode.linkNext(item);
                    n++;
                }
            }
            lineCount.postValue(n);
            renumber(renumberFrom);
            mutationKey = new Object();
            return result;
        }

        private synchronized boolean tryAddNode(IndexedStringItem element) {
            if (null == element || null != getInstanceNode(element))
                return false;
            addNode(element);
            return true;
        }

        private synchronized boolean tryInsertNode(int index, IndexedStringItem element) {
            if (null == element || null != getInstanceNode(element))
                return false;
            Node node = getNode(index);
            if (null == node) {
                addNode(element);
            } else {
                node = node.linkPrevious(element);
                if (null == node.previous)
                    firstNode = node;
                size++;
                renumber(node);
                mutationKey = new Object();
            }
            return true;
        }

        private synchronized IndexedStringItem trySetNode(int index, IndexedStringItem element) {
            if (null == element) {
                return null;
            }
            Node node = getNode(index);
            if (null == node) {
                tryAddNode(element);
                mutationKey = new Object();
                return element;
            }
            if (node.item == element || null != getInstanceNode(element)) {
                return null;
            }
            mutationKey = new Object();
            return node.replace(element);
        }

        private synchronized IndexedStringItem removeNode(IndexedStringItem element) {
            Node node = getNode(element);
            if (null == node) {
                return null;
            }
            Node next = node.next;
            IndexedStringItem result = node.remove();
            lineCount.postValue(size);
            if (null != next)
                renumber(next);
            clearNumber(node);
            mutationKey = new Object();
            return result;
        }

        private synchronized IndexedStringItem removeNode(int index) {
            Node node = getNode(index);
            if (null == node) {
                throw new IndexOutOfBoundsException("index(" + index + ") == size(" + size + ")");
            }
            Node next = node.next;
            IndexedStringItem result = node.remove();
            if (null != next)
                renumber(next);
            clearNumber(node);
            mutationKey = new Object();
            return result;
        }

        private synchronized Node removeNodes(@NonNull Iterator<?> iterator) {
            if (!iterator.hasNext()) {
                return null;
            }
            Object o = iterator.next();
            Node node;
            while (null == o || !(o instanceof IndexedStringItem && null != (node = getNode((IndexedStringItem) o)))) {
                if (!iterator.hasNext())
                    return null;
            }
            Node result = new Node(node.remove());
            Node previous = result;
            while (size > 0 && iterator.hasNext()) {
                o = iterator.next();
                if (o instanceof IndexedStringItem && null != (node = getNode((IndexedStringItem) o))) {
                    previous = previous.linkNext(node.remove());
                }
            }
            lineCount.postValue(size);
            if (null != firstNode)
                renumber(firstNode);
            clearNumber(result);
            mutationKey = new Object();
            return result;
        }

        private synchronized Node retainNodes(Collection<?> c) {
            if (null == c || null == firstNode) {
                return null;
            }
            if (c.isEmpty()) {
                return clearAllNodes();
            }

            Node node = firstNode;
            while (c.contains(node.item)) {
                if (null == (node = node.next))
                    return null;
            }
            Node n = node.next;
            Node result = new Node(node.remove());
            Node next = result;
            while (null != n) {
                if (c.contains(n.item))
                    n = n.next;
                else {
                    n = (node = n).next;
                    next = next.linkNext(node.remove());
                }
            }
            lineCount.postValue(size);
            if (null != firstNode)
                renumber(firstNode);
            clearNumber(result);
            mutationKey = new Object();
            return result;
        }

        private synchronized Node removeNodes(int fromIndex, int toIndex) {
            if (fromIndex < 0)
                throw new IndexOutOfBoundsException("fromIndex(" + fromIndex + ") < 0");
            int count = toIndex - fromIndex;
            if (count < 0)
                throw new IndexOutOfBoundsException("fromIndex(" + fromIndex + ") > toIndex(" + toIndex + ")");
            if (toIndex > size)
                throw new IndexOutOfBoundsException("toIndex(" + toIndex + ") > size(" + size + ")");
            if (count == 0) {
                return null;
            }
            Node node;
            if (fromIndex == 0) {
                if (toIndex == size)
                    return clearAllNodes();
                node = firstNode;
            } else
                node = getNode(fromIndex);
            @SuppressWarnings("ConstantConditions") Node n = node.next;
            Node result = new Node(node.remove());
            if (count > 1) {
                Node next = result;
                for (int i = 1; i < count; i++) {
                    n = (node = n).next;
                    next = next.linkNext(node.remove());
                }
            }
            lineCount.postValue(size);
            if (null != n)
                renumber(n);
            clearNumber(result);
            mutationKey = new Object();
            return result;
        }

        private synchronized Object removeWithConcurrency(int index, Object concurrency) {
            if (concurrency != mutationKey)
                throw new ConcurrentModificationException();
            removeNode(index);
            stringValueCalculated.postValue(false);
            return mutationKey;
        }

        private void renumber(@NonNull Node node) {
            try {
                node.item.lineNumber.postValue((null == node.previous) ? 1 : node.previous.item.postedLineNumber + 1);
            } finally {
                if (null != (node = node.next))
                    renumber(node);
            }
        }

        private void clearNumber(@NonNull Node node) {
            try {
                node.item.lineNumber.postValue(null);
            } finally {
                if (null != (node = node.next))
                    clearNumber(node);
            }
        }

        @Override
        public boolean add(IndexedStringItem element) {
            if (tryAddNode(element)) {
                stringValueCalculated.postValue(false);
                return true;
            }
            return false;
        }

        @Override
        public IndexedStringItem set(int index, IndexedStringItem element) {
            IndexedStringItem result = trySetNode(index, element);
            if (null != result) {
                stringValueCalculated.postValue(false);
                if (result == element)
                    return null;
            }
            return result;
        }

        @Override
        public void add(int index, IndexedStringItem element) {
            if (tryInsertNode(index, element)) {
                stringValueCalculated.postValue(false);
            }
        }

        @NonNull
        @Override
        public IndexedStringItem remove(int index) {
            IndexedStringItem removedItem = removeNode(index);
            stringValueCalculated.postValue(false);
            return removedItem;
        }

        @Override
        public int indexOf(Object o) {
            if (o instanceof IndexedStringItem)
                return getIndexof((IndexedStringItem) o);
            return -1;
        }

        @Override
        public int lastIndexOf(Object o) {
            if (o instanceof IndexedStringItem)
                return getLastIndexof((IndexedStringItem) o);
            return -1;
        }

        @Override
        public void clear() {
            if (null != clearAllNodes())
                stringValueCalculated.postValue(false);
        }

        @Override
        public boolean addAll(int index, Collection<? extends IndexedStringItem> c) {
            throw new UnsupportedOperationException();
        }

        @NonNull
        @Override
        public Iterator<IndexedStringItem> iterator() {
            return listIterator();
        }

        @NonNull
        @Override
        public ListIterator<IndexedStringItem> listIterator() {
            return listIterator(0);
        }

        @NonNull
        @Override
        public ListIterator<IndexedStringItem> listIterator(int index) {
            return new IteratorImpl(index);
        }

        @Override
        protected void removeRange(int fromIndex, int toIndex) {
            Node result = removeNodes(fromIndex, toIndex);
            if (null != result) {
                stringValueCalculated.postValue(false);
            }
        }

        @Override
        public boolean isEmpty() {
            return size == 0;
        }

        @Override
        public boolean contains(Object o) {
            return o instanceof IndexedStringItem && null != getNode((IndexedStringItem) o);
        }

        @Override
        public boolean remove(Object o) {
            if (o instanceof IndexedStringItem) {
                IndexedStringItem item = removeNode((IndexedStringItem) o);
                if (null != item) {
                    stringValueCalculated.postValue(false);
                    return true;
                }
            }
            return false;
        }

        @Override
        public synchronized boolean containsAll(@SuppressWarnings("NullableProblems") Collection<?> c) {
            if (null == c || c.isEmpty()) {
                return false;
            }
            for (Object o : c) {
                if (!contains(o))
                    return false;
            }
            return true;
        }

        @Override
        public boolean addAll(@SuppressWarnings("NullableProblems") Collection<? extends IndexedStringItem> c) {
            if (null != c) {
                Node result = addNodes(c.iterator());
                if (null != result) {
                    stringValueCalculated.postValue(false);
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean removeAll(@SuppressWarnings("NullableProblems") Collection<?> c) {
            if (null != c) {
                Node result = removeNodes(c.iterator());
                if (null != result) {
                    stringValueCalculated.postValue(false);
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean retainAll(@SuppressWarnings("NullableProblems") Collection<?> c) {
            Node result = retainNodes(c);
            if (null != result) {
                stringValueCalculated.postValue(false);
                return true;
            }
            return false;
        }

        @NonNull
        @Override
        public synchronized String toString() {
            if (postedStringValueCalculated)
                return string;
            Node node = firstNode;
            if (null == node)
                string = "";
            else if (null == node.next)
                string = NORMALIZER.apply(node.item.toString());
            else {
                StringBuilder sb = new StringBuilder(NORMALIZER.apply(node.item.postedValue));
                sb.append("\n").append(NORMALIZER.apply((node = node.next).item.postedValue));
                while (null != (node = node.next)) {
                    sb.append("\n").append(NORMALIZER.apply(node.item.postedValue));
                }
                string = sb.toString();
            }

            stringValueCalculated.postValue(true);
            return string;
        }

        @Override
        public void replaceAll(@NonNull UnaryOperator<IndexedStringItem> operator) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void sort(@Nullable Comparator<? super IndexedStringItem> c) {
            throw new UnsupportedOperationException("List is sorted by default");
        }

        @NonNull
        @Override
        public IndexedStringItem get(int index) {
            Node node = getNode(index);
            if (null == node) {
                throw new IndexOutOfBoundsException("index(" + index + ") == size(" + size + ")");
            }
            return node.item;
        }

        @Override
        public int size() {
            return size;
        }

        public void setString(String lines) {
            StringLineIterator iterator = StringLineIterator.create(lines, true, true);
            clear();
            while (iterator.hasNext()) {
                String line = iterator.next();
                if (!line.isEmpty()) {
                    add(new IndexedStringItem(line));
                }
            }
        }

        private class LineCount extends LiveData<Integer> {
            private LineCount() {
                super(size);
            }

            @Override
            protected void postValue(Integer value) {
                size = (null == value) ? 0 : value;
                super.postValue(size);
            }
        }

        private class StringValueCalculated extends LiveData<Boolean> {
            private StringValueCalculated() {
                super(postedStringValueCalculated);
            }

            @Override
            protected void postValue(Boolean value) {
                postedStringValueCalculated = null != value && value;
                super.postValue(postedStringValueCalculated);
            }
        }

        private class IteratorImpl implements ListIterator<IndexedStringItem> {
            private final int startIndex;
            private Object concurrency;
            private int endIndex;
            private int currentIndex;
            private Node currentNode = null;
            private boolean canRemove = false;

            private IteratorImpl(int index) {
                concurrency = mutationKey;
                endIndex = size;
                if (index < 0)
                    throw new IndexOutOfBoundsException("index(" + index + ") < 0");
                if (index > endIndex)
                    throw new IndexOutOfBoundsException("index(" + index + ") > size(" + endIndex + ")");
                startIndex = index;
                currentIndex = index - 1;
            }

            @Override
            public boolean hasNext() {
                return currentIndex < endIndex;
            }

            @Override
            public synchronized IndexedStringItem next() {
                if (currentIndex == endIndex || ++currentIndex == endIndex) {
                    if (concurrency != mutationKey)
                        throw new ConcurrentModificationException();
                    throw new NoSuchElementException();
                }
                if (null == currentNode) {
                    try {
                        currentNode = getNode(currentIndex);
                    } catch (IndexOutOfBoundsException ex) {
                        throw new ConcurrentModificationException(ex);
                    }
                } else
                    currentNode = currentNode.next;
                if (concurrency != mutationKey)
                    throw new ConcurrentModificationException();
                canRemove = true;
                //noinspection ConstantConditions
                return currentNode.item;
            }

            @Override
            public boolean hasPrevious() {
                return currentIndex > startIndex;
            }

            @Override
            public synchronized IndexedStringItem previous() {
                if (currentIndex <= startIndex) {
                    if (concurrency != mutationKey)
                        throw new ConcurrentModificationException();
                    throw new NoSuchElementException();
                }
                if (null == currentNode) {
                    try {
                        currentNode = getNode(--currentIndex);
                    } catch (IndexOutOfBoundsException ex) {
                        throw new ConcurrentModificationException(ex);
                    }
                } else {
                    --currentIndex;
                    currentNode = currentNode.next;
                }
                if (concurrency != mutationKey)
                    throw new ConcurrentModificationException();
                canRemove = true;
                //noinspection ConstantConditions
                return currentNode.item;
            }

            @Override
            public int nextIndex() {
                return (currentIndex < endIndex) ? currentIndex + 1 : endIndex;
            }

            @Override
            public int previousIndex() {
                return (currentIndex > startIndex) ? currentIndex - 1 : -1;
            }

            @Override
            public synchronized void remove() {
                if (!canRemove) {
                    throw new IllegalStateException();
                }
                concurrency = removeWithConcurrency(currentIndex, concurrency);
                canRemove = false;
                endIndex--;
                currentNode = null;
            }

            @Override
            public void set(IndexedStringItem indexedStringItem) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void add(IndexedStringItem indexedStringItem) {
                throw new UnsupportedOperationException();
            }
        }

        private class Node {
            private final IndexedStringItem item;
            private Node previous = null;
            private Node next = null;

            private Node(IndexedStringItem item) {
                this.item = item;
            }

            private void swap(Node other) {
                Integer i = item.postedLineNumber;
                Integer v = other.item.postedLineNumber;
                item.lineNumber.postValue(v);
                other.item.lineNumber.postValue(i);
                Node n = next;
                if (null == (next = other.next)) {
                    lastNode = this;
                    n.previous = other;
                } else {
                    next.previous = this;
                    if (null == (other.next = n))
                        lastNode = other;
                    else
                        n.previous = other;
                }
                n = previous;
                if (null == (previous = other.previous)) {
                    firstNode = this;
                    n.next = other;
                } else {
                    previous.next = this;
                    if (null == (other.previous = n))
                        firstNode = other;
                    else
                        n.next = other;
                }
            }

            private boolean moveUp() {
                if (null == previous) {
                    return false;
                }
                Node p = previous;
                Integer i = item.postedLineNumber;
                Integer v = p.item.postedLineNumber;
                item.lineNumber.postValue(v);
                p.item.lineNumber.postValue(i);
                if (null != (p.next = next))
                    next.previous = p;
                next = p;
                if (null != (previous = p.previous)) {
                    previous.next = this;
                } else {
                    firstNode = this;
                }
                p.previous = this;
                return true;
            }

            private boolean moveDown() {
                if (null == next) {
                    return false;
                }
                Node n = next;
                Integer i = item.postedLineNumber;
                Integer v = n.item.postedLineNumber;
                item.lineNumber.postValue(v);
                n.item.lineNumber.postValue(i);
                if (null != (n.previous = previous))
                    previous.next = n;
                previous = n;
                if (null != (next = n.next)) {
                    next.previous = this;
                } else {
                    lastNode = this;
                }
                n.next = this;
                return true;
            }

            private Node linkNext(IndexedStringItem item) {
                Node result = new Node(item);
                if (null != (result.next = next)) {
                    next.previous = result;
                }
                (next = result).previous = this;
                return result;
            }

            private Node linkPrevious(IndexedStringItem item) {
                Node result = new Node(item);
                if (null != (result.previous = previous)) {
                    previous.next = result;
                }
                (previous = result).next = this;
                return result;
            }

            private IndexedStringItem replace(IndexedStringItem item) {
                Node node = new Node(item);
                if (null == (node.next = next))
                    lastNode = node;
                else {
                    next.previous = node;
                    next = null;
                }
                if (null == (node.previous = previous))
                    firstNode = node;
                else {
                    previous.next = node;
                    previous = null;
                }
                item.lineNumber.postValue(this.item.postedLineNumber);
                this.item.lineNumber.postValue(null);
                return this.item;
            }

            private IndexedStringItem remove() {
                if (null == next) {
                    if (null == previous) {
                        if (this != firstNode)
                            throw new IllegalStateException();
                        firstNode = lastNode = null;
                        size = 0;
                        return item;
                    }
                    previous = (lastNode = previous).next = null;
                } else {
                    if (null == (next.previous = previous)) {
                        firstNode = next;
                    } else {
                        previous.next = next;
                        previous = null;
                    }
                    next = null;
                }
                size--;
                return item;
            }
        }

    }

    private class LineNumber extends LiveData<Integer> {
        @Override
        protected void postValue(Integer value) {
            postedLineNumber = value;
            super.postValue(value);
        }
    }

    private class LiveString extends MutableLiveData<String> {
        private boolean applied = true;

        private LiveString(String initialValue) {
            super((null == initialValue) ? "" : initialValue);
            postedValue = (null == initialValue) ? "" : initialValue;
        }

        @Override
        public synchronized void postValue(String value) {
            postedValue = (null == value) ? "" : value;
            applied = false;
            super.postValue(value);
        }

        @Override
        public synchronized void setValue(String value) {
            if (null == value) {
                super.setValue("");
                if (applied)
                    postedValue = "";
            } else {
                super.setValue(value);
                if (applied)
                    postedValue = value;
                else if (value.equals(postedValue))
                    applied = true;
            }
        }
    }
}
