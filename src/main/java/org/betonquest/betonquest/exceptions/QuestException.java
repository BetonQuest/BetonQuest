package org.betonquest.betonquest.exceptions;

import org.jetbrains.annotations.Nullable;

import java.io.Serial;

/**
 * This exception is thrown when something goes wrong with a quest context.
 */
public class QuestException extends Exception {
    /**
     * Serial version UID.
     */
    @Serial
    private static final long serialVersionUID = 7487088647464022627L;

    /**
     * {@link Exception#Exception(String)}.
     *
     * @param message the exception message.
     */
    public QuestException(final String message) {
        super(message);
    }

    /**
     * {@link Exception#Exception(String, Throwable)}.
     *
     * @param message the exception message.
     * @param cause   the Throwable that caused this exception.
     */
    public QuestException(@Nullable final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * {@link Exception#Exception(Throwable)}.
     *
     * @param cause the exceptions cause.
     */
    public QuestException(final Throwable cause) {
        super(cause);
    }
}
