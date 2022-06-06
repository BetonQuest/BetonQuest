package org.betonquest.betonquest.modules.logger.handler;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * {@link Handler} that creates the target {@link Handler}, when needed.
 */
public class LazyLogHandler extends Handler {

    /**
     * {@link Lock} instance, when the target {@link Handler} is created
     */
    private final Lock publishLock;

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
    private Handler target;

    /**
     * Create a new {@link LazyLogHandler} with a given {@link Supplier}.
     *
     * @param handlerFactory the {@link Supplier} to use for creation
     */
    public LazyLogHandler(final Supplier<Handler> handlerFactory) {
        super();
        this.publishLock = new ReentrantLock();
        this.handlerFactory = handlerFactory;
    }

    @Override
    public void publish(final LogRecord record) {
        if (closed) {
            return;
        }
        if (target == null) {
            publishLock.lock();
            try {
                if (target == null) {
                    target = handlerFactory.get();
                }
            } finally {
                publishLock.unlock();
            }
        }
        if (target != null) {
            target.publish(record);
        }
    }

    @Override
    public void flush() {
        if (target != null) {
            target.flush();
        }
    }

    @Override
    public void close() {
        if (target != null) {
            target.close();
            closed = true;
        }
    }
}
