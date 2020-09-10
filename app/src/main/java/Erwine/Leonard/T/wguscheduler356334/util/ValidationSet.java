package Erwine.Leonard.T.wguscheduler356334.util;

import java.util.function.BiFunction;
import java.util.function.Consumer;

public class ValidationSet<T, U extends ValidationSet.Node<T, U>> {

    private Builder<T, U> root;

    public ValidationSet(BiFunction<Builder<T, U>, T, U> nodeFactory) {
        root = createEmpty(nodeFactory, null, t -> root = t);
    }

    private Builder<T, U> createEmpty(BiFunction<Builder<T, U>, T, U> nodeFactory, U node, Consumer<Builder<T, U>> onReplace) {
        return new Builder<T, U>(ValidationState.INDETERMINATE, nodeFactory, new Owner<>(node)) {

            @Override
            protected void onAddValid(U node) {
                onReplace.accept(createValid(nodeFactory, node, t -> root = t));
            }

            @Override
            protected void onAddInvalid(U node) {
                onReplace.accept(createInvalid(nodeFactory, node, t -> root = t));
            }

            @Override
            protected void onAddIndeterminate(U node) {
                onReplace.accept(createIndeterminate(nodeFactory, node, t -> root = t));
            }

            @Override
            protected boolean setValid(U node) {
                throw new IllegalStateException();
            }

            @Override
            protected boolean setInvalid(U node) {
                throw new IllegalStateException();
            }

            @Override
            protected boolean setIndeterminate(U node) {
                throw new IllegalStateException();
            }

            @Override
            protected boolean remove(U node) {
                throw new IllegalStateException();
            }
        };
    }

    private Builder<T, U> createIndeterminate(BiFunction<Builder<T, U>, T, U> nodeFactory, U node, Consumer<Builder<T, U>> onReplace) {
        return new Builder<T, U>(ValidationState.INDETERMINATE, nodeFactory, new Owner<>(node)) {

            @Override
            protected void onAddValid(U node) {

            }

            @Override
            protected void onAddInvalid(U node) {

            }

            @Override
            protected void onAddIndeterminate(U node) {

            }

            @Override
            protected boolean setValid(U node) {
                return false;
            }

            @Override
            protected boolean setInvalid(U node) {
                return false;
            }

            @Override
            protected boolean setIndeterminate(U node) {
                return false;
            }

            @Override
            protected boolean remove(U node) {
                return false;
            }
        };
    }

    private Builder<T, U> createInvalid(BiFunction<Builder<T, U>, T, U> nodeFactory, U node, Consumer<Builder<T, U>> onReplace) {
        return new Builder<T, U>(ValidationState.INVALID, nodeFactory, new Owner<>(node)) {

            @Override
            protected void onAddValid(U node) {

            }

            @Override
            protected void onAddInvalid(U node) {

            }

            @Override
            protected void onAddIndeterminate(U node) {

            }

            @Override
            protected boolean setValid(U node) {
                return false;
            }

            @Override
            protected boolean setInvalid(U node) {
                return false;
            }

            @Override
            protected boolean setIndeterminate(U node) {
                return false;
            }

            @Override
            protected boolean remove(U node) {
                return false;
            }
        };
    }

    private Builder<T, U> createValid(BiFunction<Builder<T, U>, T, U> nodeFactory, U node, Consumer<Builder<T, U>> onReplace) {
        return new Builder<T, U>(ValidationState.VALID, nodeFactory, new Owner<>(node)) {

            @Override
            protected void onAddValid(U node) {

            }

            @Override
            protected void onAddInvalid(U node) {

            }

            @Override
            protected void onAddIndeterminate(U node) {

            }

            @Override
            protected boolean setValid(U node) {
                return false;
            }

            @Override
            protected boolean setInvalid(U node) {
                return false;
            }

            @Override
            protected boolean setIndeterminate(U node) {
                return false;
            }

            @Override
            protected boolean remove(U node) {
                return false;
            }
        };
    }

    public U add(T value) {
        return root.addIndeterminate(value);
    }

    public U addValid(T value) {
        return root.addValid(value);
    }

    public U addInvalid(T value) {
        return root.addInvalid(value);
    }

    public enum ValidationState {
        VALID,
        INVALID,
        INDETERMINATE
    }

    public static abstract class Builder<T, U extends Node<T, U>> {
        private final ValidationState state;
        private final BiFunction<Builder<T, U>, T, U> nodeFactory;
        private Owner<T, U> owner;

        protected Builder(ValidationState state, BiFunction<Builder<T, U>, T, U> nodeFactory, Owner<T, U> owner) {
            this.state = state;
            this.nodeFactory = nodeFactory;
            this.owner = owner;
        }

        protected final U addValid(T value) {
            U node = nodeFactory.apply(this, value);
            onAddValid(node);
            return node;
        }

        protected final U addInvalid(T value) {
            U node = nodeFactory.apply(this, value);
            onAddInvalid(node);
            return node;
        }

        protected final U addIndeterminate(T value) {
            U node = nodeFactory.apply(this, value);
            onAddIndeterminate(node);
            return node;
        }

        protected abstract void onAddValid(U node);

        protected abstract void onAddInvalid(U node);

        protected abstract void onAddIndeterminate(U node);

        protected abstract boolean setValid(U node);

        protected abstract boolean setInvalid(U node);

        protected abstract boolean setIndeterminate(U node);

        protected abstract boolean remove(U node);
    }

    public static class Owner<T, U extends Node<T, U>> {
        private U first;
        private U last;

        private Owner(U value) {
            first = last = value;
        }
    }

    @SuppressWarnings("unchecked")
    public static class Node<T, U extends Node<T, U>> {
        private U previous;
        private U next;
        private Builder<T, U> builder;
        private Owner<T, U> owner;

        private void setOwner(Owner<T, U> owner) {
            if (null == owner) {
                if (null != this.owner) {
                    if (null == next) {
                        if (null == (this.owner.last = previous)) {
                            this.owner.first = null;
                        } else {
                            ((Node<T, U>) previous).next = null;
                            previous = null;
                        }
                    } else {
                        if (null == (((Node<T, U>) next).previous = previous)) {
                            this.owner.first = next;
                        } else {
                            ((Node<T, U>) previous).next = next;
                            previous = null;
                        }
                        next = null;
                    }
                }
            } else {
                if (null != this.owner) {
                    if (this.owner == owner) {
                        return;
                    }
                    setOwner(null);
                }
                if (null == (previous = owner.last)) {
                    owner.first = (U) this;
                } else {
                    ((Node<T, U>) previous).next = (U) this;
                }
            }
            this.owner = owner;
        }

        protected Node(Builder<T, U> builder) {
            this.builder = builder;
        }

        public U getPrevious() {
            return previous;
        }

        public U getNext() {
            return next;
        }

        public Owner<T, U> getOwner() {
            return owner;
        }

        public ValidationState getState() {
            return builder.state;
        }

        public boolean isValid() {
            return builder.state == ValidationState.VALID;
        }

        public boolean setValid() {
            return builder.setValid((U) this);
        }

        public boolean isInvalid() {
            return builder.state == ValidationState.INVALID;
        }

        public boolean setInvalid() {
            return builder.setInvalid((U) this);
        }

        public boolean isIndeterminate() {
            return builder.state == ValidationState.INDETERMINATE;
        }

        public boolean setIndeterminate() {
            return builder.setIndeterminate((U) this);
        }
    }
}
