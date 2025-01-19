package org.betonquest.betonquest.logger.handler;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * Resettable {@link Handler} that can be reset by calling {@link #reset()}.
 * Resetting will close the previous handler and replace it with a newly
 * created {@link Handler}.
 */
public class ResettableHandler extends Handler {

    /**
     * Lock to prevent invalid states caused by multithreading.
     * <p>
     * Those states include but are not limited to:
     * <ul>
     *     <li>Publishing while reset</li>
     *     <li>Closing while reset</li>
     *     <li>Publishing while closing</li>
     * </ul>
     */
    private final ReadWriteLock lock;

    /**
     * Factory that creates the internal {@link Handler} every time it is reset.
     */
    private final Supplier<Handler> handlerFactory;

    /**
     * Marker that the {@link Handler} is closed to prevent resets that would
     * unclose the internal {@link Handler}.
     */
    private boolean closed;

    /**
     * The current internal {@link Handler}.
     */
    private Handler internalHandler;

    /**
     * Create a new {@link ResettableHandler} that will create the internal
     * {@link Handler} by calling the given {@link Supplier}.
     *
     * @param handlerFactory the {@link Supplier} to use for creation
     */
    public ResettableHandler(final Supplier<Handler> handlerFactory) {
        super();
        this.lock = new ReentrantReadWriteLock();
        this.handlerFactory = handlerFactory;
        this.internalHandler = handlerFactory.get();
    }

    @Override
    public void publish(final LogRecord record) {
        lock.readLock().lock();
        try {
            internalHandler.publish(record);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void flush() {
        lock.readLock().lock();
        try {
            internalHandler.flush();
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Reset the underlying {@link Handler} by closing the old instance and then obtaining a new instance from the
     * provided supplier.
     *
     * @throws IllegalStateException if this handler was closed
     */
    public void reset() {
        requireNotClosed();
        lock.writeLock().lock();
        try {
            requireNotClosed();
            internalHandler.close();
            internalHandler = handlerFactory.get();
        } finally {
            lock.writeLock().unlock();
        }
    }

    private void requireNotClosed() {
        if (closed) {
            throw new IllegalStateException("Cannot publish log record: ResettableHandler was closed, it can not be reset anymore.");
        }
    }

    @Override
    public void close() {
        if (closed) {
            return;
        }
        lock.writeLock().lock();
        try {
            closed = true;
            internalHandler.close();
        } finally {
            lock.writeLock().unlock();
        }
    }
}
