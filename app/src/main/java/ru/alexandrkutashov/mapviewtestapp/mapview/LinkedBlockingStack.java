package ru.alexandrkutashov.mapviewtestapp.mapview;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

/**
 * LIFO BlockingQueue для использования в ExecutorService.
 *
 * @author Alexandr Kutashov
 *         on 01.04.2018
 */

public class LinkedBlockingStack<T> implements BlockingQueue<T> {
    private final LinkedBlockingDeque<T> stack = new LinkedBlockingDeque<T>();

    @Override
    public T remove() {
        return stack.remove();
    }

    @Override
    public T poll() {
        return stack.poll();
    }

    @Override
    public T element() {
        return stack.element();
    }

    @Override
    public T peek() {
        return stack.peek();
    }

    @Override
    public int size() {
        return stack.size();
    }

    @Override
    public boolean isEmpty() {
        return stack.isEmpty();
    }

    @Override
    public Iterator<T> iterator() {
        return stack.iterator();
    }

    @Override
    public Object[] toArray() {
        return stack.toArray();
    }

    @Override
    public <S> S[] toArray(final S[] a) {
        return stack.toArray(a);
    }

    @Override
    public boolean containsAll(final Collection<?> c) {
        return stack.containsAll(c);
    }

    @Override
    public boolean addAll(final Collection<? extends T> c) {
        return stack.addAll(c);
    }

    @Override
    public boolean removeAll(final Collection<?> c) {
        return stack.removeAll(c);
    }

    @Override
    public boolean retainAll(final Collection<?> c) {
        return stack.removeAll(c);
    }

    @Override
    public void clear() {
        stack.clear();
    }

    @Override
    public boolean add(final T e) {
        return stack.offerFirst(e); //Used offerFirst instead of add.
    }

    @Override
    public boolean offer(final T e) {
        return stack.offerFirst(e); //Used offerFirst instead of offer.
    }

    @Override
    public void put(final T e) throws InterruptedException {
        stack.put(e);
    }

    @Override
    public boolean offer(final T e, final long timeout, final TimeUnit unit)
            throws InterruptedException {
        return stack.offerLast(e, timeout, unit);
    }

    @Override
    public T take() throws InterruptedException {
        return stack.take();
    }

    @Override
    public T poll(final long timeout, final TimeUnit unit)
            throws InterruptedException {
        return stack.poll();
    }

    @Override
    public int remainingCapacity() {
        return stack.remainingCapacity();
    }

    @Override
    public boolean remove(final Object o) {
        return stack.remove(o);
    }

    @Override
    public boolean contains(final Object o) {
        return stack.contains(o);
    }

    @Override
    public int drainTo(final Collection<? super T> c) {
        return stack.drainTo(c);
    }

    @Override
    public int drainTo(final Collection<? super T> c, final int maxElements) {
        return stack.drainTo(c, maxElements);
    }
}
