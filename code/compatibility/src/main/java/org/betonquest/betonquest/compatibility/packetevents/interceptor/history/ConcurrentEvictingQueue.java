package org.betonquest.betonquest.compatibility.packetevents.interceptor.history;

import java.util.AbstractQueue;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * A thread-safe queue that holds at most a given number of elements.
 * When a new element is added to a full queue, the oldest elements are removed.
 * <p>
 * Iteration is weakly consistent: it never throws a {@link java.util.ConcurrentModificationException}
 * and may include elements that are added while it runs.
 * Under concurrent adds the size may briefly differ from the limit by a few elements.
 *
 * @param <E> the type of elements held in this queue
 */
public class ConcurrentEvictingQueue<E> extends AbstractQueue<E> {

    /**
     * The underlying concurrent queue.
     */
    private final Queue<E> delegate;

    /**
     * The maximum number of elements kept in this queue.
     */
    private final int maxSize;

    /**
     * Creates an empty queue that keeps at most {@code maxSize} elements.
     *
     * @param maxSize the maximum number of elements to keep
     */
    public ConcurrentEvictingQueue(final int maxSize) {
        super();
        if (maxSize < 0) {
            throw new IllegalArgumentException("maxSize must not be negative: " + maxSize);
        }
        this.delegate = new ConcurrentLinkedQueue<>();
        this.maxSize = maxSize;
    }

    /**
     * Adds the element to the end of the queue and removes the oldest elements
     * if the queue holds more than the maximum number of elements.
     *
     * @param element the element to add
     * @return always {@code true}
     */
    @Override
    public boolean offer(final E element) {
        delegate.offer(element);
        while (delegate.size() > maxSize) {
            delegate.poll();
        }
        return true;
    }

    @Override
    public E poll() {
        return delegate.poll();
    }

    @Override
    public E peek() {
        return delegate.peek();
    }

    @Override
    public Iterator<E> iterator() {
        return delegate.iterator();
    }

    @Override
    public int size() {
        return delegate.size();
    }
}
