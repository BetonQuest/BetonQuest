package org.betonquest.betonquest.api.quest;

import java.io.Serial;

/**
 * Exception thrown when the object couldn't be found.
 */
public class ObjectNotFoundException extends QuestException {
    /**
     * Serial version UID.
     */
    @Serial
    private static final long serialVersionUID = -6335789753445719198L;

    /**
     * {@link QuestException#QuestException(String)}.
     *
     * @param message the exception message.
     */
    public ObjectNotFoundException(final String message) {
        super(message);
    }

    /**
     * {@link QuestException#QuestException(String, Throwable)}.
     *
     * @param message the exception message.
     * @param cause   the Throwable that caused this exception.
     */
    public ObjectNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * {@link QuestException#QuestException(Throwable)}.
     *
     * @param cause the Throwable that caused this exception.
     */
    public ObjectNotFoundException(final Throwable cause) {
        super(cause);
    }
}
