package org.betonquest.betonquest.compatibility.protocollib.wrappers;

import java.io.Serial;

/**
 * An unchecked exception that is thrown when a packet cannot be processed.
 */
public class UncheckedPacketException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 60789910395201791L;

    /**
     * Constructs a new unchecked packet exception.
     */
    public UncheckedPacketException() {
        super();
    }

    /**
     * Constructs a new unchecked packet exception with the specified detail message.
     *
     * @param message the detail message
     */
    public UncheckedPacketException(final String message) {
        super(message);
    }

    /**
     * Constructs a new unchecked packet exception with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause   the cause of the exception
     */
    public UncheckedPacketException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new unchecked packet exception with the specified cause.
     *
     * @param cause the cause of the exception
     */
    public UncheckedPacketException(final Throwable cause) {
        super(cause);
    }
}
