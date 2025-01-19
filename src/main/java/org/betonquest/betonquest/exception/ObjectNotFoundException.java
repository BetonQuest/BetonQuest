package org.betonquest.betonquest.exception;

import java.io.Serial;

/**
 * Exception thrown when the object couldn't be found.
 */
public class ObjectNotFoundException extends Exception {
    @Serial
    private static final long serialVersionUID = -6335789753445719198L;

    /**
     * {@link Exception#Exception(String)}
     *
     * @param message the exceptions message.
     */
    public ObjectNotFoundException(final String message) {
        super(message);
    }

    /**
     * {@link Exception#Exception(String, Throwable)}
     *
     * @param message the exceptions message.
     * @param cause   the Throwable that caused this exception.
     */
    public ObjectNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * {@link Exception#Exception(Throwable)}
     *
     * @param cause the Throwable that caused this exception.
     */
    public ObjectNotFoundException(final Throwable cause) {
        super(cause);
    }
}
