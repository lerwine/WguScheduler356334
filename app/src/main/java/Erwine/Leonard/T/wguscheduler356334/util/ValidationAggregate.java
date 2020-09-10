package Erwine.Leonard.T.wguscheduler356334.util;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import java.security.cert.PKIXRevocationChecker;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class ValidationAggregate<T, U extends ValidationAggregate.Element> extends AbstractSet<U> {
    private final BiFunction<Builder, T, U> nodeFactory;
    private final Builder indeterminateBuilder;
    private final Builder validBuilder;
    private final Builder invalidBuilder;
    private final ValidationStateLiveData validationStateLiveData;
    private final BooleanLiveData isValidLiveData;
    private final BooleanLiveData isInvalidLiveData;
    private final BooleanLiveData isIndeterminateLiveData;
    private ValidationState currentState;
    private int size;
    private U firstSetItem;
    private U lastSetItem;
    private Object mutationKey;

    public static <T> ValidationAggregate<T, Node<T>> create() {
        return new NodeAggregate<>();
    }

    public ValidationAggregate(BiFunction<Builder, T, U> nodeFactory) {
        this.nodeFactory = Objects.requireNonNull(nodeFactory);
        size = 0;
        mutationKey = new Object();
        indeterminateBuilder = new IndeterminateBuilder();
        validBuilder = new ValidBuilder();
        invalidBuilder = new InvalidBuilder();
        validationStateLiveData = new ValidationStateLiveData();
        isValidLiveData = new BooleanLiveData(false);
        isInvalidLiveData = new BooleanLiveData(false);
        isIndeterminateLiveData = new BooleanLiveData(true);
    }

    public boolean removeLast() {
        return remove(lastSetItem);
    }

    public LiveData<Boolean> getIsIndeterminateLiveData() {
        return isIndeterminateLiveData;
    }

    public LiveData<Boolean> getIsValidLiveData() {
        return isValidLiveData;
    }

    public LiveData<Boolean> getIsInvalidLiveData() {
        return isInvalidLiveData;
    }

    public LiveData<ValidationState> getValidationStateLiveData() {
        return validationStateLiveData;
    }

    @Override
    public boolean isEmpty() {
        return null == firstSetItem;
    }

    @Override
    public boolean add(U u) {
        return null != u && addSafe(u);
    }

    @SuppressWarnings("unchecked")
    private synchronized boolean addSafe(@NonNull Element element) {
        if (element.builder.getSet() != this || null != element.group) {
            return false;
        }
        mutationKey = new Object();
        Builder builder = (Builder) element.builder;
        element.append(true);
        size++;
        lastSetItem = builder.group.lastGroupItem = (U) element;
        if (null == builder.group.firstGroupItem) {
            builder.group.firstGroupItem = lastSetItem;
            if (null == firstSetItem) {
                firstSetItem = lastSetItem;
            }
            builder.onFirstAdded(Optional.empty());
        }
        return true;
    }

    @Override
    public boolean remove(Object o) {
        return null != o && o instanceof ValidationAggregate.Element && removeNode((Element) o);
    }

    private synchronized Object checkRemove(U node, Object key) {
        if (key != mutationKey) {
            throw new ConcurrentModificationException();
        }
        if (null == node) {
            throw new IllegalStateException();
        }
        removeNode(node);
        return mutationKey;
    }

    @SuppressWarnings("unchecked")
    private synchronized boolean removeNode(@NonNull Element element) {
        Group group;
        if (element.getSet() != this || null == (group = (Group) element.group)) {
            return false;
        }

        mutationKey = new Object();
        Builder builder = (Builder) element.builder;

        Element outerElement = element.unlinkGroup();
        if (null == outerElement) {
            if (group.firstGroupItem == element) {
                group.firstGroupItem = group.lastGroupItem = null;
                element.builder.onLastRemoved(Optional.empty());
            }
        } else {
            if (null == outerElement.nextGroupItem) {
                group.lastGroupItem = (U) outerElement;
            }
            if (null == outerElement.previousGroupItem) {
                group.firstGroupItem = (U) outerElement;
            }
        }
        outerElement = element.unlinkSet();
        size--;
        if (null == outerElement) {
            if (firstSetItem == element) {
                firstSetItem = lastSetItem = null;
                // TODO: Set aggregate state to ValidationState.INDETERMINATE
            }
        } else {
            if (null == outerElement.nextSetItem) {
                lastSetItem = (U) outerElement;
            }
            if (null == outerElement.previousSetItem) {
                firstSetItem = (U) outerElement;
            }
        }
        return true;
    }

    @Override
    public void clear() {
        clearSafe();
    }

    private synchronized boolean clearSafe() {
        Element element = firstSetItem;
        if (null == element) {
            return false;
        }

        mutationKey = new Object();
        element.group = null;
        element.previousGroupItem = element.nextGroupItem = null;
        while (null != (element = element.nextSetItem)) {
            element.group = null;
            element.previousGroupItem = element.nextGroupItem = element.previousSetItem.nextSetItem = null;
            element.previousSetItem = null;
        }
        indeterminateBuilder.group.firstGroupItem = indeterminateBuilder.group.lastGroupItem = null;
        validBuilder.group.firstGroupItem = validBuilder.group.lastGroupItem = null;
        invalidBuilder.group.firstGroupItem = invalidBuilder.group.lastGroupItem = null;
        size = 0;
        // TODO: Set aggregate state to ValidationState.INDETERMINATE
        return true;
    }

    @SuppressWarnings("unchecked")
    private synchronized boolean setValidSafe(U node) {
        if (node.getSet() != this) {
            throw new IllegalArgumentException();
        }
        ValidationState oldState = node.getState();
        if (oldState == ValidationState.VALID) {
            return false;
        }

        Group group = (Group) node.getGroup();
        if (null == group) {
            ((Element) node).builder = validBuilder;
            return false;
        }
        Element outerElement = ((Element) node).unlinkGroup();
        if (null == outerElement) {
            if (group.firstGroupItem == node) {
                group.firstGroupItem = group.lastGroupItem = null;
                ((Element) node).builder.onLastRemoved(Optional.of(ValidationState.VALID));
            }
        } else {
            if (null == outerElement.nextGroupItem) {
                group.lastGroupItem = (U) outerElement;
            }
            if (null == outerElement.previousGroupItem) {
                group.firstGroupItem = (U) outerElement;
            }
        }

        ((Element) node).builder = validBuilder;
        ((Element) node).append(false);
        validBuilder.group.lastGroupItem = node;
        if (null == validBuilder.group.firstGroupItem) {
            validBuilder.group.firstGroupItem = node;
            validBuilder.onFirstAdded(Optional.of(oldState));
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    private synchronized boolean setInvalidSafe(U node) {
        if (node.getSet() != this) {
            throw new IllegalArgumentException();
        }
        ValidationState oldState = node.getState();
        if (oldState == ValidationState.INVALID) {
            return false;
        }

        Group group = (Group) node.getGroup();
        if (null == group) {
            ((Element) node).builder = invalidBuilder;
            return false;
        }
        Element outerElement = ((Element) node).unlinkGroup();
        if (null == outerElement) {
            if (group.firstGroupItem == node) {
                group.firstGroupItem = group.lastGroupItem = null;
                ((Element) node).builder.onLastRemoved(Optional.of(ValidationState.INVALID));
            }
        } else {
            if (null == outerElement.nextGroupItem) {
                group.lastGroupItem = (U) outerElement;
            }
            if (null == outerElement.previousGroupItem) {
                group.firstGroupItem = (U) outerElement;
            }
        }

        ((Element) node).builder = invalidBuilder;
        ((Element) node).append(false);
        invalidBuilder.group.lastGroupItem = node;
        if (null == invalidBuilder.group.firstGroupItem) {
            invalidBuilder.group.firstGroupItem = node;
            invalidBuilder.onFirstAdded(Optional.of(oldState));
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    private synchronized boolean setIndeterminateSafe(U node) {
        if (node.getSet() != this) {
            throw new IllegalArgumentException();
        }
        ValidationState oldState = node.getState();
        if (oldState == ValidationState.INDETERMINATE) {
            return false;
        }

        Group group = (Group) node.getGroup();
        if (null == group) {
            ((Element) node).builder = indeterminateBuilder;
            return false;
        }
        Element outerElement = ((Element) node).unlinkGroup();
        if (null == outerElement) {
            if (group.firstGroupItem == node) {
                group.firstGroupItem = group.lastGroupItem = null;
                ((Element) node).builder.onLastRemoved(Optional.of(ValidationState.INDETERMINATE));
            }
        } else {
            if (null == outerElement.nextGroupItem) {
                group.lastGroupItem = (U) outerElement;
            }
            if (null == outerElement.previousGroupItem) {
                group.firstGroupItem = (U) outerElement;
            }
        }

        ((Element) node).builder = indeterminateBuilder;
        ((Element) node).append(false);
        indeterminateBuilder.group.lastGroupItem = node;
        if (null == indeterminateBuilder.group.firstGroupItem) {
            indeterminateBuilder.group.firstGroupItem = node;
            indeterminateBuilder.onFirstAdded(Optional.of(oldState));
        }
        return true;
    }

    public U addIndeterminate(T source) {
        U node = nodeFactory.apply(indeterminateBuilder, source);
        mutationKey = new Object();
        ((Element) node).append(true);
        size++;
        lastSetItem = indeterminateBuilder.group.lastGroupItem = node;
        if (null == indeterminateBuilder.group.firstGroupItem) {
            indeterminateBuilder.group.firstGroupItem = lastSetItem;
            if (null == firstSetItem) {
                firstSetItem = lastSetItem;
            }
            indeterminateBuilder.onFirstAdded(Optional.empty());
        }
        return node;
    }

    public U addValid(T source) {
        U node = nodeFactory.apply(validBuilder, source);
        mutationKey = new Object();
        ((Element) node).append(true);
        size++;
        lastSetItem = validBuilder.group.lastGroupItem = node;
        if (null == validBuilder.group.firstGroupItem) {
            validBuilder.group.firstGroupItem = lastSetItem;
            if (null == firstSetItem) {
                firstSetItem = lastSetItem;
            }
            validBuilder.onFirstAdded(Optional.empty());
        }
        return node;
    }

    public U addInvalid(T source) {
        U node = nodeFactory.apply(invalidBuilder, source);
        mutationKey = new Object();
        ((Element) node).append(true);
        size++;
        lastSetItem = invalidBuilder.group.lastGroupItem = node;
        if (null == invalidBuilder.group.firstGroupItem) {
            invalidBuilder.group.firstGroupItem = lastSetItem;
            if (null == firstSetItem) {
                firstSetItem = lastSetItem;
            }
            invalidBuilder.onFirstAdded(Optional.empty());
        }
        return node;
    }

    @NonNull
    @Override
    public Iterator<U> iterator() {
        return new IteratorImpl();
    }

    @Override
    public int size() {
        return size;
    }

    public enum ValidationState {
        VALID,
        INVALID,
        INDETERMINATE
    }

    @SuppressWarnings("unchecked")
    public abstract class Builder {
        private final ValidationState targetState;
        private Group group;
        private Builder(ValidationState targetState) {
            this.targetState = targetState;
            this.group = new Group(null);
        }
        public ValidationAggregate<T, U> getSet() { return ValidationAggregate.this; }

        private synchronized void setValid(@NonNull Element element) {
            if (element.builder != this) {
                throw new IllegalArgumentException();
            }
            setValidSafe((U) element);
        }

        private synchronized  void setInvalid(@NonNull Element element) {
            if (element.builder != this) {
                throw new IllegalArgumentException();
            }
            setInvalidSafe((U) element);
        }

        private synchronized  void setIndeterminate(@NonNull Element element) {
            if (element.builder != this) {
                throw new IllegalArgumentException();
            }
            setIndeterminateSafe((U) element);
        }

        protected abstract void onFirstAdded(Optional<ValidationState> oldState);

        protected abstract void onLastRemoved(Optional<ValidationState> newState);
    }

    public final class IndeterminateBuilder extends Builder {

        private IndeterminateBuilder() {
            super(ValidationState.INDETERMINATE);
        }

        @Override
        protected void onFirstAdded(Optional<ValidationState> oldState) {

        }

        @Override
        protected void onLastRemoved(Optional<ValidationState> newState) {

        }
    }

    public final class ValidBuilder extends Builder {

        private ValidBuilder() {
            super(ValidationState.VALID);
        }

        @Override
        protected void onFirstAdded(Optional<ValidationState> oldState) {

        }

        @Override
        protected void onLastRemoved(Optional<ValidationState> newState) {

        }
    }

    public final class InvalidBuilder extends Builder {

        private InvalidBuilder() {
            super(ValidationState.INVALID);
        }

        @Override
        protected void onFirstAdded(Optional<ValidationState> oldState) {

        }

        @Override
        protected void onLastRemoved(Optional<ValidationState> newState) {

        }
    }

    public static class Node<T> extends ValidationAggregate.Element {
        private T value;

        protected Node(ValidationAggregate<T, Node<T>>.Builder builder, T initialValue) {
            super(builder);
            value = initialValue;
        }

        public T getValue() {
            return value;
        }

        public synchronized void setValue(T value) {
            T oldValue = this.value;
            if (!Objects.equals(value, oldValue)) {
                this.value = value;
                if (!onValueChanged(oldValue, value)) {
                    setAsIndeterminate();
                }
            }
        }

        protected boolean onValueChanged(T oldValue, T newValue) {
            return false;
        }

        @SuppressWarnings("unchecked")
        @Override
        public Node<T> getPreviousGroupItem() {
            return (Node<T>)super.getPreviousGroupItem();
        }

        @SuppressWarnings("unchecked")
        @Override
        public Node<T> getPreviousSetItem() {
            return (Node<T>)super.getPreviousSetItem();
        }

        @SuppressWarnings("unchecked")
        @Override
        public Node<T> getNextGroupItem() {
            return (Node<T>)super.getNextGroupItem();
        }

        @SuppressWarnings("unchecked")
        @Override
        public Node<T> getNextSetItem() {
            return (Node<T>)super.getNextSetItem();
        }

    }

    public static class NodeAggregate<T> extends ValidationAggregate<T, Node<T>> {

        public NodeAggregate() {
            super(Node::new);
        }

        public void addValuesIndeterminate(T ...value) {
            if (null != value) {
                for (T v : value) {
                    addIndeterminate(v);
                }
            }
        }

        public void addValuesValid(T ...value) {
            if (null != value) {
                for (T v : value) {
                    addValid(v);
                }
            }
        }

        public void addValuesInvalid(T ...value) {
            if (null != value) {
                for (T v : value) {
                    addInvalid(v);
                }
            }
        }

        public void addAllValues(Collection<T> values, Predicate<T> validator) {
            values.forEach(v -> {
                if (validator.test(v)) {
                    addValid(v);
                } else {
                    addInvalid(v);
                }
            });
        }

        public synchronized boolean containsValue(T value) {
            if (!isEmpty()) {
                Iterator<Node<T>> iterator = iterator();
                while (iterator.hasNext()) {
                    if (Objects.equals(iterator.next().value, value)) {
                        return true;
                    }
                }
            }
            return false;
        }

        public synchronized Optional<Node<T>> findNode(T value) {
            if (!isEmpty()) {
                Iterator<Node<T>> iterator = iterator();
                while (iterator.hasNext()) {
                    Node<T> node = iterator.next();
                    if (Objects.equals(node.value, value)) {
                        return Optional.of(node);
                    }
                }
            }
            return Optional.empty();
        }

    }

    public static class AutoValidateNode<T> extends ValidationAggregate.Element {
        private T value;

        protected AutoValidateNode(ValidationAggregate<T, AutoValidateNode<T>>.Builder builder, T initialValue) {
            super(builder);
            value = initialValue;
        }

        public T getValue() {
            return value;
        }

        public synchronized void setValue(T value) {
            T oldValue = this.value;
            if (!Objects.equals(value, oldValue)) {
                this.value = value;
                ValidationAggregate<?, ? extends Element> set = getSet();
                if (set instanceof AutoValidate) {
                    ((AutoValidate<T>) set).onNodeChanged(this);
                }
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public Node<T> getPreviousGroupItem() {
            return (Node<T>)super.getPreviousGroupItem();
        }

        @SuppressWarnings("unchecked")
        @Override
        public Node<T> getPreviousSetItem() {
            return (Node<T>)super.getPreviousSetItem();
        }

        @SuppressWarnings("unchecked")
        @Override
        public Node<T> getNextGroupItem() {
            return (Node<T>)super.getNextGroupItem();
        }

        @SuppressWarnings("unchecked")
        @Override
        public Node<T> getNextSetItem() {
            return (Node<T>)super.getNextSetItem();
        }

    }

    public static class AutoValidate<T> extends ValidationAggregate<T, AutoValidateNode<T>> {

        private final Predicate<T> validator;

        public AutoValidate(Predicate<T> validator) {
            super(AutoValidateNode::new);
            this.validator = validator;
        }

        public synchronized void synchronize(List<T> source) {
            if (source.isEmpty()) {
                clear();
            } else if (isEmpty()) {
                addAllValues(source);
            } else {
                Iterator<T> srcIterator = source.iterator();
                Iterator<AutoValidateNode<T>> tgtIterator = iterator();
                while (tgtIterator.hasNext()) {
                    if (!srcIterator.hasNext()) {
                        tgtIterator.remove();
                        while (tgtIterator.hasNext()) {
                            tgtIterator.next();
                            tgtIterator.remove();
                        }
                        return;
                    }
                    tgtIterator.next().setValue(srcIterator.next());
                }
                while (srcIterator.hasNext()) {
                    T value = srcIterator.next();
                    if (validator.test(value)) {
                        addValid(value);
                    } else {
                        addInvalid(value);
                    }
                }
            }
        }

        public synchronized void synchronize(Stream<T> source) {
            if (isEmpty()) {
                addAllValues(source);
            } else {
                Iterator<T> srcIterator = source.iterator();
                if (srcIterator.hasNext()) {
                    Iterator<AutoValidateNode<T>> tgtIterator = iterator();
                    tgtIterator.next().setValue(srcIterator.next());
                    while (tgtIterator.hasNext()) {
                        if (!srcIterator.hasNext()) {
                            tgtIterator.remove();
                            while (tgtIterator.hasNext()) {
                                tgtIterator.next();
                                tgtIterator.remove();
                            }
                            return;
                        }
                        tgtIterator.next().setValue(srcIterator.next());
                    }
                    while (srcIterator.hasNext()) {
                        T value = srcIterator.next();
                        if (validator.test(value)) {
                            addValid(value);
                        } else {
                            addInvalid(value);
                        }
                    }
                }
            }
        }

        public void addAllValues(Collection<T> values) {
            values.forEach(v -> {
                if (validator.test(v)) {
                    addValid(v);
                } else {
                    addInvalid(v);
                }
            });
        }

        public void addAllValues(Stream<T> values) {
            values.forEach(v -> {
                if (validator.test(v)) {
                    addValid(v);
                } else {
                    addInvalid(v);
                }
            });
        }

        public synchronized boolean containsValue(T value) {
            if (!isEmpty()) {
                Iterator<AutoValidateNode<T>> iterator = iterator();
                while (iterator.hasNext()) {
                    if (Objects.equals(iterator.next().value, value)) {
                        return true;
                    }
                }
            }
            return false;
        }

        public synchronized Optional<AutoValidateNode<T>> findNode(T value) {
            if (!isEmpty()) {
                Iterator<AutoValidateNode<T>> iterator = iterator();
                while (iterator.hasNext()) {
                    AutoValidateNode<T> node = iterator.next();
                    if (Objects.equals(node.value, value)) {
                        return Optional.of(node);
                    }
                }
            }
            return Optional.empty();
        }

        public void onNodeChanged(AutoValidateNode<T> node) {
            if (validator.test(node.value)) {
                node.setAsValid();
            } else {
                node.setAsInvalid();
            }
        }
    }

    public class Group {
        private U firstGroupItem;
        private U lastGroupItem;
        private Group(U initialItem) { firstGroupItem = lastGroupItem = initialItem; }
        public U getFirstGroupItem() { return firstGroupItem; }
        public U getLastGroupItem() { return lastGroupItem; }
        public ValidationAggregate<T, U> getSet() { return ValidationAggregate.this; }
    }

    public static abstract class Element {
        private Element previousSetItem;
        private Element nextSetItem;
        private Element previousGroupItem;
        private Element nextGroupItem;
        private ValidationAggregate<?, ? extends Element>.Builder builder;
        private ValidationAggregate<?, ? extends Element>.Group group;
        protected Element(ValidationAggregate<?, ? extends Element>.Builder builder) {
            this.builder = builder;
        }

        public ValidationState getState() {
            return builder.targetState;
        }

        public boolean isValid() { return builder.targetState == ValidationState.VALID; }

        public void setAsValid() { builder.setValid(this); }

        public boolean isInvalid() { return builder.targetState == ValidationState.INVALID; }

        public void setAsInvalid() { builder.setInvalid(this); }

        public boolean isIndeterminate() { return builder.targetState == ValidationState.INDETERMINATE; }

        public void setAsIndeterminate() { builder.setIndeterminate(this); }

        public Element getPreviousSetItem() {
            return previousSetItem;
        }

        public Element getNextSetItem() {
            return nextSetItem;
        }

        public Element getPreviousGroupItem() {
            return previousGroupItem;
        }

        public Element getNextGroupItem() {
            return nextGroupItem;
        }

        public ValidationAggregate<?, ? extends Element>.Group getGroup() {
            return group;
        }

        public ValidationAggregate<?, ? extends Element> getSet() { return builder.getSet(); }

        private synchronized void append(boolean appendSet) {
            if (null != (previousGroupItem = builder.group.lastGroupItem)) {
                previousGroupItem.nextGroupItem = this;
            }
            this.group = builder.group;
            if (appendSet && null != (previousSetItem = builder.getSet().lastSetItem)) {
                previousSetItem.nextSetItem = this;
            }
        }

        private synchronized Element unlinkGroup() {
            Element result;
            if (null != nextGroupItem) {
                if (null != (nextGroupItem.previousGroupItem = previousGroupItem)) {
                    previousGroupItem.nextGroupItem = nextGroupItem;
                    previousGroupItem = nextGroupItem = null;
                    return null;
                }
                result = nextGroupItem;
                nextGroupItem = null;
            } else if (null != (result = previousGroupItem)) {
                previousGroupItem.nextGroupItem = null;
            }
            group = null;
            return result;
        }

        private synchronized Element unlinkSet() {
            Element result;
            if (null != nextSetItem) {
                if (null != (nextSetItem.previousSetItem = previousSetItem)) {
                    previousSetItem.nextSetItem = nextSetItem;
                    previousSetItem = nextSetItem = null;
                    return null;
                }
                result = nextSetItem;
                nextSetItem = null;
            } else if (null != (result = previousSetItem)) {
                previousSetItem.nextSetItem = null;
            }
            return result;
        }

    }

    private class IteratorImpl implements Iterator<U> {

        private Object mutationCheck;
        private U currentNode;
        private U nextNode;

        private IteratorImpl() {
            mutationCheck = mutationKey;
            currentNode = null;
            nextNode = firstSetItem;
        }

        @Override
        public synchronized boolean hasNext() {
            if (mutationCheck != mutationKey) {
                throw new ConcurrentModificationException();
            }
            return null != nextNode;
        }

        @Override
        public synchronized U next() {
            if (mutationCheck != mutationKey) {
                throw new ConcurrentModificationException();
            }
            if (null == (currentNode = nextNode)) {
                throw new NoSuchElementException();
            }
            //noinspection unchecked
            nextNode = (U) ((Element) currentNode).nextSetItem;
            return currentNode;
        }

        @Override
        public synchronized void remove() {
            mutationCheck = checkRemove(currentNode, mutationCheck);
            currentNode = null;
        }
    }

    private static class BooleanLiveData extends LiveData<Boolean> {
        private boolean postedValue;
        private BooleanLiveData(boolean initialValue) {
            super(initialValue);
        }
        private synchronized void post(boolean value) {
            if (value != postedValue) {
                postedValue = value;
                postValue(value);
            }
        }
    }

    private class ValidationStateLiveData extends LiveData<ValidationState> {
        private ValidationState postedValue;
        public ValidationStateLiveData() {
            super(ValidationState.INVALID);
            postedValue = ValidationState.INVALID;
        }

        private synchronized void post(ValidationState value) {
            ValidationState oldValue = postedValue;
            if (oldValue != value) {
                postedValue = value;
                super.postValue(value);
                switch (value) {
                    case VALID:
                        isValidLiveData.post(true);
                        isInvalidLiveData.post(false);
                        isIndeterminateLiveData.post(false);
                        break;
                    case INVALID:
                        isInvalidLiveData.post(true);
                        isValidLiveData.post(false);
                        isIndeterminateLiveData.post(false);
                        break;
                    default:
                        isIndeterminateLiveData.post(true);
                        isValidLiveData.post(false);
                        isInvalidLiveData.post(false);
                        break;
                }
            }
        }
    }
}
