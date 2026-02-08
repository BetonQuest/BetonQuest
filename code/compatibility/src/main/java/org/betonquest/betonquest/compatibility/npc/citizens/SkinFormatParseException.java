package org.betonquest.betonquest.compatibility.npc.citizens;

import java.io.Serial;

/**
 * An exception that is thrown when the base64 skin JSON is invalid.
 */
public class SkinFormatParseException extends Exception {

    @Serial
    private static final long serialVersionUID = -6313544890230009673L;

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param message the exception's message.
     */
    public SkinFormatParseException(final String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified detail message and cause.
     *
     * @param message the exception's message.
     * @param cause   the throwable that caused this exception.
     */
    public SkinFormatParseException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
