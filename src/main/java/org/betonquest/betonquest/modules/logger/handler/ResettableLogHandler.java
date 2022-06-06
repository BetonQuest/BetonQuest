package org.betonquest.betonquest.modules.logger.handler;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * {@link Handler} that creates the target {@link Handler}, when needed.
 */
public class ResettableLogHandler extends Handler {

    /**
     * {@link Lock} instance, when the target {@link Handler} is created
     */
    private final ReadWriteLock lock;

    /**
     * {@link Supplier} for the {@link Handler} creation.
     */
    private final Supplier<Handler> handlerFactory;

    /**
     * Weather this {@link Handler} is closed.
     */
    private boolean closed;

    /**
     * The created target {@link Handler}.
     */
    private Handler internalHandler;

    /**
     * Create a new {@link ResettableLogHandler} with a given {@link Supplier}.
     *
     * @param handlerFactory the {@link Supplier} to use for creation
     */
    public ResettableLogHandler(final Supplier<Handler> handlerFactory) {
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
            throw new IllegalStateException("ResettableHandler was closed, it can not be reset anymore.");
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
