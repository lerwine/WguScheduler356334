package Erwine.Leonard.T.wguscheduler356334;

import androidx.lifecycle.Observer;

import java.util.Collection;
import java.util.LinkedList;

public class ObserverHelper<T> extends LinkedList<T> implements Observer<T> {

    @Override
    public synchronized void onChanged(T t) {
        addLast(t);
    }

    @Override
    public synchronized T removeFirst() {
        return super.removeFirst();
    }

    @Override
    public synchronized T removeLast() {
        return super.removeLast();
    }

    @Override
    public synchronized void addFirst(T t) {
        super.addFirst(t);
    }

    @Override
    public synchronized void addLast(T t) {
        super.addLast(t);
    }

    @Override
    public synchronized boolean add(T t) {
        return super.add(t);
    }

    @Override
    public synchronized boolean remove(Object o) {
        return super.remove(o);
    }

    @Override
    public synchronized boolean addAll(int index, Collection<? extends T> c) {
        return super.addAll(index, c);
    }

    @Override
    public synchronized void clear() {
        super.clear();
    }

    @Override
    public synchronized T set(int index, T element) {
        return super.set(index, element);
    }

    @Override
    public synchronized void add(int index, T element) {
        super.add(index, element);
    }

    @Override
    public synchronized T remove(int index) {
        return super.remove(index);
    }

    @Override
    public synchronized T remove() {
        return super.remove();
    }

}
