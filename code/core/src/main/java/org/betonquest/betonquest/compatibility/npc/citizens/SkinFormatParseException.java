package org.betonquest.betonquest.compatibility.npc.citizens;

import java.io.Serial;

/**
 * An exception that is thrown when the base64 skin JSON is invalid.
 */
public class SkinFormatParseException extends Exception {

    @Serial
    private static final long serialVersionUID = -6313544890230009673L;

    /**
     * {@link Exception#Exception(String)}
     *
     * @param message the exception's message.
     */
    public SkinFormatParseException(final String message) {
        super(message);
    }

    /**
     * {@link Exception#Exception(String, Throwable)}
     *
     * @param message the exception's message.
     * @param cause   the throwable that caused this exception.
     */
    public SkinFormatParseException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * {@link Exception#Exception(Throwable)}
     *
     * @param cause the throwable that caused this exception.
     */
    public SkinFormatParseException(final Throwable cause) {
        super(cause);
    }
}
