package org.betonquest.betonquest.util.scheduler;

import java.io.Serial;

/**
 * An exception created during the execution of an async task.
 */
public class AsyncTaskException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -4501059063243851677L;

    /**
     * Create a new exception for an async task.
     *
     * @param exception The original exception
     */
    public AsyncTaskException(final Exception exception) {
        super(exception);
    }
}
