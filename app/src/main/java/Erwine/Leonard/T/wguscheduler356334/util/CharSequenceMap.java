package Erwine.Leonard.T.wguscheduler356334.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.PrimitiveIterator;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class CharSequenceMap<K extends CharSequenceMap.ValueWrapper, V> implements Map<CharSequence, V> {

    private final EntrySet entrySet;
    private final HashSet<EntryImpl<K, V>> backingSet;

    public static <V> Map<CharSequence, V> createCaseInsensitive() {
        return new CharSequenceMap<ValueWrapper, V>() {
            @Override
            protected ValueWrapper createKey(@NonNull CharSequence input) {
                return new ValueWrapper(input);
            }
        };
    }

    protected Iterator<EntryImpl<K, V>> getEntryIterator() {
        return backingSet.iterator();
    }

    protected void addEntry(K key, V value) {
        backingSet.add(new EntryImpl<>(createKey(key), value));
    }

    protected V removeEntry(EntryImpl<K, V> entry) {
        backingSet.remove(entry);
        return entry.value;
    }

    public static <V> Map<CharSequence, V> createUpperCase() {
        return new CharSequenceMap<UpperCaseValueWrapper, V>() {
            @Override
            protected UpperCaseValueWrapper createKey(@NonNull CharSequence input) {
                return new UpperCaseValueWrapper(input);
            }

            @Override
            protected V getImpl(@Nullable CharSequence key) {
                Iterator<EntryImpl<UpperCaseValueWrapper, V>> iterator = this.getEntryIterator();
                while (iterator.hasNext()) {
                    EntryImpl<UpperCaseValueWrapper, V> e = iterator.next();
                    if (e.key.equals(key)) {
                        return e.value;
                    }
                }
                return null;
            }

            @Nullable
            @Override
            protected V putImpl(CharSequence key, V value) {
                Iterator<EntryImpl<UpperCaseValueWrapper, V>> iterator = this.getEntryIterator();
                while (iterator.hasNext()) {
                    EntryImpl<UpperCaseValueWrapper, V> e = iterator.next();
                    if (e.key.equals(key)) {
                        return e.setValue(value);
                    }
                }
                addEntry(createKey(key), value);
                return null;
            }

            @Override
            protected V removeImpl(CharSequence key) {
                Iterator<EntryImpl<UpperCaseValueWrapper, V>> iterator = this.getEntryIterator();
                while (iterator.hasNext()) {
                    EntryImpl<UpperCaseValueWrapper, V> e = iterator.next();
                    if (e.key.equals(key)) {
                        return removeEntry(e);
                    }
                }
                return null;
            }
        };
    }

    public static <V> Map<CharSequence, V> createLowerCase() {
        return new CharSequenceMap<LowerCaseValueWrapper, V>() {
            @Override
            protected LowerCaseValueWrapper createKey(@NonNull CharSequence input) {
                return new LowerCaseValueWrapper(input);
            }

            @Override
            protected V getImpl(@Nullable CharSequence key) {
                Iterator<EntryImpl<LowerCaseValueWrapper, V>> iterator = getEntryIterator();
                while (iterator.hasNext()) {
                    EntryImpl<LowerCaseValueWrapper, V> e = iterator.next();
                    if (e.key.equals(key)) {
                        return e.value;
                    }
                }
                return null;
            }

            @Nullable
            @Override
            protected V putImpl(CharSequence key, V value) {
                Iterator<EntryImpl<LowerCaseValueWrapper, V>> iterator = getEntryIterator();
                while (iterator.hasNext()) {
                    EntryImpl<LowerCaseValueWrapper, V> e = iterator.next();
                    if (e.key.equals(key)) {
                        return e.setValue(value);
                    }
                }
                addEntry(createKey(key), value);
                return null;
            }

            @Override
            protected V removeImpl(CharSequence key) {
                Iterator<EntryImpl<LowerCaseValueWrapper, V>> iterator = getEntryIterator();
                while (iterator.hasNext()) {
                    EntryImpl<LowerCaseValueWrapper, V> e = iterator.next();
                    if (e.key.equals(key)) {
                        return removeEntry(e);
                    }
                }
                return null;
            }
        };
    }

    protected CharSequenceMap() {
        backingSet = new HashSet<>();
        entrySet = new EntrySet();
    }

    public static class UpperCaseValueWrapper extends ValueWrapper {
        private Integer hashCode;

        public UpperCaseValueWrapper(@NonNull CharSequence value) {
            super(value);
        }

        @Override
        public int hashCode() {
            if (null == hashCode) {
                hashCode = backingString.toUpperCase().hashCode();
            }
            return hashCode;
        }

        @Override
        protected boolean test(int t1, int t2) {
            if (t1 == t2) {
                return true;
            }
            if (Character.isLetter(t1) && Character.isLetter(t2)) {
                return Character.toUpperCase(t1) == Character.toUpperCase(t2);
            }
            return false;
        }

        @Override
        protected int compare(int t1, int t2) {
            int result = t1 - t2;
            if (result != 0) {
                if (Character.isLetter(t1) && Character.isLetter(t2)) {
                    int r = Character.toUpperCase(t1) - Character.toUpperCase(t2);
                    if (r != 0) {
                        return r;
                    }
                }
            }
            return result;
        }
    }

    public static class LowerCaseValueWrapper extends ValueWrapper {
        private Integer hashCode;

        public LowerCaseValueWrapper(@NonNull CharSequence value) {
            super(value);
        }

        @Override
        public int hashCode() {
            if (null == hashCode) {
                hashCode = backingString.toLowerCase().hashCode();
            }
            return hashCode;
        }

        @Override
        protected boolean test(int t1, int t2) {
            if (t1 == t2) {
                return true;
            }
            if (Character.isLetter(t1) && Character.isLetter(t2)) {
                return Character.toLowerCase(t1) == Character.toLowerCase(t2);
            }
            return false;
        }

        @Override
        protected int compare(int t1, int t2) {
            int result = t1 - t2;
            if (result != 0) {
                if (Character.isLetter(t1) && Character.isLetter(t2)) {
                    int r = Character.toLowerCase(t1) - Character.toLowerCase(t2);
                    if (r != 0) {
                        return r;
                    }
                }
            }
            return result;
        }
    }

    protected abstract K createKey(@NonNull CharSequence input);

    @Override
    public int size() {
        return backingSet.size();
    }

    @Override
    public boolean isEmpty() {
        return backingSet.isEmpty();
    }

    @Override
    public boolean containsKey(@Nullable Object key) {
        if (backingSet.isEmpty()) {
            return false;
        }
        if (key instanceof ValueWrapper) {
            return containsKeyImpl(((ValueWrapper) key).backingString);
        }
        if (null == key || key instanceof CharSequence) {
            return containsKeyImpl((CharSequence) key);
        }
        return false;
    }

    protected boolean containsKeyImpl(CharSequence key) {
        for (EntryImpl<K, V> entry : backingSet) {
            if (entry.key.equals(key)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean containsValue(@Nullable Object value) {
        if (backingSet.isEmpty()) {
            return false;
        }
        for (EntryImpl<K, V> entry : backingSet) {
            if (Objects.equals(entry.value, value)) {
                return true;
            }
        }
        return false;
    }

    @Nullable
    @Override
    public V get(@Nullable Object key) {
        if (backingSet.isEmpty()) {
            return null;
        }
        if (key instanceof ValueWrapper) {
            return getImpl(((ValueWrapper) key).backingString);
        }
        if (null == key || key instanceof CharSequence) {
            return getImpl((CharSequence) key);
        }
        return null;
    }

    protected V getImpl(@Nullable CharSequence key) {
        Iterator<EntryImpl<K, V>> iterator = backingSet.iterator();
        while (iterator.hasNext()) {
            EntryImpl<K, V> e = iterator.next();
            if (e.key.equals(key)) {
                if (e.key.backingString.contentEquals(key)) {
                    return e.value;
                }
                EntryImpl<K, V> r = e;
                while (iterator.hasNext()) {
                    e = iterator.next();
                    if (e.key.backingString.contentEquals(key)) {
                        return e.value;
                    }
                }
                return r.value;
            }
        }
        return null;
    }

    @Nullable
    @Override
    public V put(CharSequence key, V value) {
        if (backingSet.isEmpty()) {
            backingSet.add(new EntryImpl<>(createKey(key), value));
            return null;
        }
        if (key instanceof ValueWrapper) {
            return putImpl(((ValueWrapper) key).backingString, value);
        }
        return putImpl(key, value);
    }

    @Nullable
    protected V putImpl(CharSequence key, V value) {
        Iterator<EntryImpl<K, V>> iterator = backingSet.iterator();
        while (iterator.hasNext()) {
            EntryImpl<K, V> e = iterator.next();
            if (e.key.equals(key)) {
                if (e.key.backingString.contentEquals(key)) {
                    return e.setValue(value);
                }
                EntryImpl<K, V> r = e;
                while (iterator.hasNext()) {
                    e = iterator.next();
                    if (e.key.backingString.contentEquals(key)) {
                        return e.setValue(value);
                    }
                }
                return r.setValue(value);
            }
        }
        backingSet.add(new EntryImpl<>(createKey(key), value));
        return null;
    }

    @Nullable
    @Override
    public V remove(@Nullable Object key) {
        if (backingSet.isEmpty()) {
            return null;
        }
        if (key instanceof ValueWrapper) {
            return removeImpl(((ValueWrapper) key).backingString);
        }
        if (null == key || key instanceof CharSequence) {
            return removeImpl((CharSequence) key);
        }
        return null;
    }

    protected V removeImpl(CharSequence key) {
        Iterator<EntryImpl<K, V>> iterator = backingSet.iterator();
        while (iterator.hasNext()) {
            EntryImpl<K, V> e = iterator.next();
            if (e.key.equals(key)) {
                if (e.key.backingString.contentEquals(key)) {
                    backingSet.remove(e);
                    return e.value;
                }
                EntryImpl<K, V> r = e;
                while (iterator.hasNext()) {
                    e = iterator.next();
                    if (e.key.backingString.contentEquals(key)) {
                        backingSet.remove(e);
                        return e.value;
                    }
                }
                backingSet.remove(r);
                return r.value;
            }
        }
        return null;
    }

    @Override
    public void putAll(@NonNull Map<? extends CharSequence, ? extends V> m) {
        if (m.isEmpty()) {
            return;
        }
        Iterator<? extends Entry<? extends CharSequence, ? extends V>> src = m.entrySet().iterator();
        if (!src.hasNext()) {
            return;
        }
        Entry<? extends CharSequence, ? extends V> e = src.next();
        put(e.getKey(), e.getValue());
        while (src.hasNext()) {
            e = src.next();
            putImpl(e.getKey(), e.getValue());
        }
    }

    @Override
    public void clear() {
        backingSet.clear();
    }

    @NonNull
    @Override
    public Set<CharSequence> keySet() {
        return keySet;
    }

    @NonNull
    @Override
    public Collection<V> values() {
        return values;
    }

    @NonNull
    @Override
    public Set<Entry<CharSequence, V>> entrySet() {
        return entrySet;
    }

    private KeySet keySet = new KeySet();
    private Values values = new Values();

    private class KeySet implements Set<CharSequence> {

        @Override
        public int size() {
            return backingSet.size();
        }

        @Override
        public boolean isEmpty() {
            return backingSet.isEmpty();
        }

        @Override
        public boolean contains(@Nullable Object o) {
            return containsKey(o);
        }

        @NonNull
        @Override
        public Iterator<CharSequence> iterator() {
            return new IteratorImpl<CharSequence>() {
                @Override
                protected CharSequence apply(EntryImpl<K, V> e) {
                    return e.key;
                }
            };
        }

        @NonNull
        @Override
        public Object[] toArray() {
            if (backingSet.isEmpty())
                return new Object[0];
            return backingSet.stream().map(t -> t.key).toArray();
        }

        @NonNull
        @Override
        public <T> T[] toArray(@NonNull T[] a) {
            return backingSet.stream().map(t -> t.key).collect(Collectors.toList()).toArray(a);
        }

        @Override
        public boolean add(CharSequence charSequence) {
            return false;
        }

        @Override
        public boolean remove(@Nullable Object o) {
            return false;
        }

        @Override
        public boolean containsAll(@NonNull Collection<?> c) {
            if (c.isEmpty()) {
                return true;
            }
            return c.stream().allMatch(CharSequenceMap.this::containsKey);
        }

        @Override
        public boolean addAll(@NonNull Collection<? extends CharSequence> c) {
            return false;
        }

        @Override
        public boolean retainAll(@NonNull Collection<?> c) {
            return false;
        }

        @Override
        public boolean removeAll(@NonNull Collection<?> c) {
            return false;
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }
    }

    private class Values implements Collection<V> {

        @Override
        public int size() {
            return backingSet.size();
        }

        @Override
        public boolean isEmpty() {
            return backingSet.isEmpty();
        }

        @Override
        public boolean contains(@Nullable Object o) {
            return containsValue(o);
        }

        @NonNull
        @Override
        public Iterator<V> iterator() {
            return new IteratorImpl<V>() {
                @Override
                protected V apply(EntryImpl<K, V> e) {
                    return e.value;
                }
            };
        }

        @NonNull
        @Override
        public Object[] toArray() {
            if (backingSet.isEmpty())
                return new Object[0];
            return backingSet.stream().map(t -> t.value).toArray();
        }

        @NonNull
        @Override
        public <T> T[] toArray(@NonNull T[] a) {
            return backingSet.stream().map(t -> t.value).collect(Collectors.toList()).toArray(a);
        }

        @Override
        public boolean add(V v) {
            return false;
        }

        @Override
        public boolean remove(@Nullable Object o) {
            return false;
        }

        @Override
        public boolean containsAll(@NonNull Collection<?> c) {
            if (c.isEmpty()) {
                return true;
            }
            return c.stream().allMatch(CharSequenceMap.this::containsValue);
        }

        @Override
        public boolean addAll(@NonNull Collection<? extends V> c) {
            return false;
        }

        @Override
        public boolean removeAll(@NonNull Collection<?> c) {
            return false;
        }

        @Override
        public boolean retainAll(@NonNull Collection<?> c) {
            return false;
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }
    }

    public static class ValueWrapper implements CharSequence, Comparable<CharSequence> {

        protected final String backingString;

        public ValueWrapper(@NonNull CharSequence value) {
            backingString = (value instanceof String) ? (String) value : value.toString();
        }

        @Override
        public final int length() {
            return backingString.length();
        }

        @Override
        public final char charAt(int index) {
            return backingString.charAt(index);
        }

        @NonNull
        @Override
        public final CharSequence subSequence(int start, int end) {
            return backingString.subSequence(start, end);
        }

        @NonNull
        @Override
        public IntStream chars() {
            return backingString.chars();
        }

        @NonNull
        @Override
        public IntStream codePoints() {
            return backingString.codePoints();
        }

        @Override
        public int hashCode() {
            return backingString.hashCode();
        }

        @NonNull
        @Override
        public String toString() {
            return backingString;
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            return obj instanceof CharSequence && test((CharSequence) obj);
        }

        protected boolean test(@NonNull CharSequence obj) {
            if (backingString.isEmpty()) {
                return (obj instanceof String) ? ((String) obj).isEmpty() : ((obj instanceof ValueWrapper) ? ((ValueWrapper) obj).backingString.isEmpty() : obj.length() == 0);
            }
            if (obj.length() != backingString.length()) {
                return false;
            }
            return test(obj.codePoints().iterator());
        }

        protected boolean test(PrimitiveIterator.OfInt iterator) {
            for (int i = 0; i < backingString.length(); i++) {
                if (!(iterator.hasNext() && test(backingString.codePointAt(i), iterator.next()))) {
                    return false;
                }
            }
            return !iterator.hasNext();
        }

        protected boolean test(int t1, int t2) {
            if (t1 == t2) {
                return true;
            }
            if (Character.isLetter(t1) && Character.isLetter(t2)) {
                return Character.toUpperCase(t1) == Character.toUpperCase(t2) || Character.toLowerCase(t1) == Character.toLowerCase(t2);
            }
            return false;
        }

        @Override
        public int compareTo(@Nullable CharSequence o) {
            return (null == o) ? 1 : compare(o);
        }

        protected int compare(@NonNull CharSequence obj) {
            if (backingString.isEmpty()) {
                return ((obj instanceof String) ? ((String) obj).isEmpty() : ((obj instanceof ValueWrapper) ? ((ValueWrapper) obj).backingString.isEmpty() : obj.length() == 0)) ? 0 : -1;
            }
            return compare(obj.codePoints().iterator());
        }

        protected int compare(PrimitiveIterator.OfInt iterator) {
            for (int i = 0; i < backingString.length(); i++) {
                if (!iterator.hasNext()) {
                    return 1;
                }
                int result = compare(backingString.codePointAt(i), iterator.next());
                if (result != 0) {
                    return result;
                }
            }
            return (iterator.hasNext()) ? -1 : 0;
        }

        protected int compare(int t1, int t2) {
            int result = t1 - t2;
            if (result != 0) {
                if (Character.isLetter(t1) && Character.isLetter(t2)) {
                    int r = Character.toUpperCase(t1) - Character.toUpperCase(t2);
                    if (r != 0 || (r = Character.toLowerCase(t1) - Character.toLowerCase(t2)) != 0) {
                        return r;
                    }
                }
            }
            return result;
        }

    }

    private static class EntryImpl<K extends CharSequenceMap.ValueWrapper, V> implements Entry<CharSequence, V> {
        private final K key;
        private V value;

        private EntryImpl(@NonNull K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            if (Objects.equals(value, this.value)) {
                return null;
            }
            this.value = value;
            return this.value;
        }
    }

    private class EntrySet extends AbstractSet<Entry<CharSequence, V>> {
        private EntrySet() {
        }

        @NonNull
        @Override
        public Iterator<Entry<CharSequence, V>> iterator() {
            return new EntryIterator();
        }

        @Override
        public int size() {
            return backingSet.size();
        }

        @Override
        public boolean isEmpty() {
            return backingSet.isEmpty();
        }
    }

    private class EntryIterator implements Iterator<Entry<CharSequence, V>> {
        private final Iterator<EntryImpl<K, V>> backingIterator;

        private EntryIterator() {
            this.backingIterator = backingSet.iterator();
        }

        @Override
        public boolean hasNext() {
            return backingIterator.hasNext();
        }

        @Override
        public Entry<CharSequence, V> next() {
            return backingIterator.next();
        }
    }

    private abstract class IteratorImpl<R> implements Iterator<R> {
        private final Iterator<EntryImpl<K, V>> backingIterator;

        private IteratorImpl() {
            this.backingIterator = backingSet.iterator();
        }

        @Override
        public boolean hasNext() {
            return backingIterator.hasNext();
        }

        @Override
        public R next() {
            return apply(backingIterator.next());
        }

        protected abstract R apply(EntryImpl<K, V> e);
    }

}
