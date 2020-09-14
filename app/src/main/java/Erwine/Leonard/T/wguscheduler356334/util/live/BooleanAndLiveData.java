package Erwine.Leonard.T.wguscheduler356334.util.live;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Transformations;

import java.util.Objects;

public class BooleanAndLiveData extends LiveData<Boolean> {
    private final MediatorLiveData<Boolean> mediatorLiveData;
    private Node firstTrue;
    private Node lastTrue;
    private Node firstFalse;
    private Node lastFalse;

    public BooleanAndLiveData() {
        mediatorLiveData = new MediatorLiveData<>();
    }

    private synchronized void onNodeChanged(Node node) {
        if (node.lastValue) {
            if (null == node.next) {
                if (null == (lastFalse = node.previous)) {
                    firstFalse = null;
                    if (null == (node.previous = lastTrue)) {
                        firstTrue = node;
                    } else {
                        node.previous.next = node;
                    }
                    lastTrue = node;
                    postValue(true);
                    return;
                }
                lastFalse.next = null;
            } else {
                if (null == (node.next.previous = node.previous)) {
                    firstFalse = node.next;
                } else {
                    node.previous.next = node.next;
                }
                node.next = null;
            }
            if (null == (node.previous = lastTrue)) {
                firstTrue = node;
            } else {
                node.previous.next = node;
            }
            lastTrue = node;
        } else {
            if (null == node.next) {
                if (null == (lastTrue = node.previous)) {
                    firstTrue = null;
                } else {
                    lastTrue.next = null;
                }
            } else {
                if (null == (node.next.previous = node.previous)) {
                    firstTrue = node.next;
                } else {
                    node.next.previous = node.previous;
                }
                node.next = null;
            }
            if (null == (node.previous = lastFalse)) {
                firstFalse = lastFalse = node;
                postValue(false);
            } else {
                lastFalse = node.previous.next = node;
            }
        }
    }

    private boolean isSourceAdded(LiveData<?> source) {
        if (null == source) {
            return false;
        }
        for (Node node = firstTrue; null != node; node = node.next) {
            if (node.getSource() == source) {
                return true;
            }
        }
        for (Node node = firstFalse; null != node; node = node.next) {
            if (node.getSource() == source) {
                return true;
            }
        }
        return false;
    }

    public synchronized boolean addSource(LiveData<Boolean> source) {
        if (null == source || isSourceAdded(source)) {
            return false;
        }
        addNode(new Node(source));
        return true;
    }

    public synchronized <T> boolean addSource(LiveData<T> source, Function<T, Boolean> mapFunction) {
        if (null == source || isSourceAdded(source)) {
            return false;
        }
        addNode(new Transformer<>(source, Objects.requireNonNull(mapFunction)));
        return true;
    }

    private void addNode(Node node) {
        Boolean b = node.getBooleanSource().getValue();
        node.lastValue = null != b && b;
        mediatorLiveData.addSource(node.getBooleanSource(), node::onValueChanged);
        if (node.lastValue) {
            if (null == (node.previous = lastTrue)) {
                firstTrue = node;
                if (null == firstFalse) {
                    lastTrue = node;
                    postValue(true);
                    return;
                }
            } else {
                node.previous.next = node;
            }
            lastTrue = node;
        } else {
            if (null == (node.previous = lastFalse)) {
                firstFalse = lastFalse = node;
                postValue(false);
                return;
            }
            lastFalse = node.previous.next = node;
        }
    }

    private synchronized Node removeNodeSource(LiveData<?> source) {
        for (Node node = firstTrue; null != node; node = node.next) {
            if (node.getSource() == source) {
                mediatorLiveData.removeSource(node.getBooleanSource());
                return node;
            }
        }
        for (Node node = firstFalse; null != node; node = node.next) {
            if (node.getSource() == source) {
                mediatorLiveData.removeSource(node.getBooleanSource());
                return node;
            }
        }
        return null;
    }

    public boolean removeSource(LiveData<?> source) {
        if (null == source) {
            return false;
        }
        Node node = removeNodeSource(source);
        if (null == node) {
            return false;
        }
        removeNode(node);
        return true;
    }

    private synchronized void removeNode(Node node) {
        if (null == node.next) {
            if (null == node.previous) {
                if (null == firstTrue) {
                    firstFalse = lastFalse = null;
                    postValue(false);
                } else if (null == firstFalse || firstTrue == node) {
                    firstTrue = lastTrue = null;
                } else {
                    firstFalse = lastFalse = null;
                    postValue(true);
                }
            } else if (lastTrue == node) {
                node.previous = (lastTrue = node.previous).next = null;
            } else {
                node.previous = (lastFalse = node.previous).next = null;
            }
        } else {
            if (null == (node.next.previous = node.previous)) {
                if (firstTrue == node) {
                    (firstTrue = node.next).previous = null;
                } else {
                    (firstFalse = node.next).previous = null;
                }
            } else {
                node.previous.next = node.next;
                node.previous = null;
            }
            node.next = null;
        }
    }

    class Node {
        private LiveData<Boolean> source;
        private Node previous;
        private Node next;
        private boolean lastValue;

        Node(LiveData<Boolean> source) {
            this.source = source;
        }

        final LiveData<Boolean> getBooleanSource() {
            return source;
        }

        LiveData<?> getSource() {
            return source;
        }

        private synchronized void onValueChanged(Boolean value) {
            boolean b = null != value && value;
            if (b != lastValue) {
                lastValue = b;
                onNodeChanged(this);
            }
        }
    }

    private class Transformer<T> extends Node {
        private final LiveData<T> source;

        private Transformer(LiveData<T> source, Function<T, Boolean> mapFunction) {
            super(Transformations.map(source, mapFunction));
            this.source = source;
        }

        @Override
        LiveData<?> getSource() {
            return source;
        }
    }
}
