package org.betonquest.betonquest.api.quest;

import java.io.PrintStream;
import java.io.Serial;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This exception is thrown when a list of exceptions occurred.
 */
public class QuestListException extends QuestException {
    /**
     * Serial version UID.
     */
    @Serial
    private static final long serialVersionUID = 6137642583413485787L;

    /**
     * The exceptions that occurred and where added to this exception.
     */
    private final Map<String, QuestException> exceptions;

    /**
     * Creates an exception where a list of sub exceptions can be added.
     * The Message will be printed and all added exceptions get printed in a new line with their subject as a key.
     *
     * @param message the exception message
     */
    public QuestListException(final String message) {
        super(message);
        exceptions = new LinkedHashMap<>();
    }

    /**
     * Adds an exception to the list of exceptions.
     *
     * @param subject   the subject of the exception used for readability
     * @param exception the exception to add
     */
    public void addException(final String subject, final QuestException exception) {
        exceptions.put(subject, exception);
    }

    @Override
    public String getMessage() {
        final StringBuilder message = new StringBuilder(super.getMessage());
        for (final Map.Entry<String, QuestException> entry : exceptions.entrySet()) {
            message.append("\n    ").append(entry.getKey()).append(": ").append(entry.getValue().getMessage());
        }
        return message.toString();
    }

    @Override
    public void printStackTrace(final PrintStream stream) {
        super.printStackTrace(stream);
        for (final Map.Entry<String, QuestException> entry : exceptions.entrySet()) {
            stream.println("    " + entry.getKey() + ": ");
            entry.getValue().printStackTrace(stream);
        }
    }

    /**
     * Throws this exception if there are any exceptions in the list.
     *
     * @throws QuestException if there are any exceptions in the list
     */
    public void throwIfNotEmpty() throws QuestException {
        if (!exceptions.isEmpty()) {
            throw this;
        }
    }
}
