package org.betonquest.betonquest.modules.logger.handler;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
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
     * The logger to use for logging initialization errors.
     */
    private final BetonQuestLogger log;

    /**
     * Factory that lazily creates the internal {@link Handler}.
     */
    private final LazyHandlerSupplier handlerFactory;

    /**
     * Marker that the internal {@link Handler} is closed for the case
     * that it wasn't initialized.
     */
    private boolean closed;

    /**
     * Marker that the internal {@link Handler} initialization failed.
     */
    private boolean failedInitialization;

    /**
     * Lazily created internal {@link Handler}.
     */
    @Nullable
    private Handler internalHandler;

    /**
     * Create a new {@link LazyHandler} that will create the actual
     * {@link Handler} on demand by calling the given {@link Supplier}.
     *
     * @param log            the {@link BetonQuestLogger} to use for logging initialization errors
     * @param handlerFactory the {@link Supplier} to use for creation
     */
    public LazyHandler(final BetonQuestLogger log, final LazyHandlerSupplier handlerFactory) {
        super();
        this.log = log;
        this.lock = new ReentrantLock();
        this.handlerFactory = handlerFactory;
    }

    @Override
    @SuppressWarnings("NullAway")
    public void publish(final LogRecord record) {
        if (failedInitialization) {
            return;
        }
        requireNotClosed();
        if (internalHandler == null && !initializeInternalHandler()) {
            return;
        }
        internalHandler.publish(record);
    }

    private boolean initializeInternalHandler() {
        lock.lock();
        try {
            requireNotClosed();
            if (internalHandler == null) {
                internalHandler = handlerFactory.get();
            }
        } catch (final IOException e) {
            failedInitialization = true;
            log.error("Could not initialize internal handler: " + e.getMessage() + "\n", e);
            return false;
        } finally {
            lock.unlock();
        }
        return true;
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

    /**
     * Functional interface to lazily create a {@link Handler}.
     */
    @FunctionalInterface
    public interface LazyHandlerSupplier {

        /**
         * Gets a result.
         *
         * @return a result
         * @throws IOException if an I/O error occurs
         */
        Handler get() throws IOException;
    }
}
