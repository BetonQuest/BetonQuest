package org.betonquest.betonquest.modules.logger.handler;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * Lazy {@link Handler} that creates the target {@link Handler} on demand.
 * Only {@link #publish(LogRecord)} will trigger initialization, all other
 * methods are silently ignored when the internal {@link Handler} was not
 * yet initialized.
 */
public class LazyHandler extends Handler {

    /**
     * {@link Lock} to ensure mutual exclusion of initialization and
     * closing of the internal {@link Handler}.
     */
    private final Lock lock;

    /**
     * Factory that lazily creates the internal {@link Handler}.
     */
    private final Supplier<Handler> handlerFactory;

    /**
     * Marker that the internal {@link Handler} is closed for the case
     * that it wasn't initialized.
     */
    private boolean closed;

    /**
     * Lazily created internal {@link Handler}.
     */
    private Handler internalHandler;

    /**
     * Create a new {@link LazyHandler} that will create the actual
     * {@link Handler} on demand by calling the given {@link Supplier}.
     *
     * @param handlerFactory the {@link Supplier} to use for creation
     */
    public LazyHandler(final Supplier<Handler> handlerFactory) {
        super();
        this.lock = new ReentrantLock();
        this.handlerFactory = handlerFactory;
    }

    @Override
    public void publish(final LogRecord record) {
        requireNotClosed();
        if (internalHandler == null) {
            initializeInternalHandler();
        }
        internalHandler.publish(record);
    }

    private void initializeInternalHandler() {
        lock.lock();
        try {
            requireNotClosed();
            if (internalHandler == null) {
                internalHandler = handlerFactory.get();
            }
        } finally {
            lock.unlock();
        }
    }

    private void requireNotClosed() {
        if (closed) {
            throw new IllegalStateException(
                    "Cannot publish log record: LazyLogHandler was closed and had not been initialized before closing."
            );
        }
    }

    @Override
    public void flush() {
        if (internalHandler != null) {
            internalHandler.flush();
        }
    }

    @Override
    public void close() {
        if (closed) {
            return;
        }
        closed = true;
        lock.lock();
        try {
            if (internalHandler != null) {
                internalHandler.close();
            }
        } finally {
            lock.unlock();
        }
    }
}
