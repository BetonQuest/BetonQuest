package org.betonquest.betonquest.exceptions;

/**
 * Exception thrown when the object couldn't be found.
 */
public class ObjectNotFoundException extends Exception {

    private static final long serialVersionUID = -6335789753445719198L;

    /**
     * {@link Exception#Exception(String)}
     */
    public ObjectNotFoundException(final String message) {
        super(message);
    }

    /**
     * {@link Exception#Exception(String, Throwable)}
     */
    public ObjectNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * {@link Exception#Exception(Throwable)}
     */
    public ObjectNotFoundException(final Throwable cause) {
        super(cause);
    }
}
