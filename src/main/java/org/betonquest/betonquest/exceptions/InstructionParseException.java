package org.betonquest.betonquest.exceptions;

/**
 * Exception thrown when the instruction string has a wrong format.
 */
public class InstructionParseException extends Exception {

    private static final long serialVersionUID = 7487088647464022627L;

    /**
     * {@link Exception#Exception(String)}
     *
     * @param message the exceptions message.
     */
    public InstructionParseException(final String message) {
        super(message);
    }

    /**
     * {@link Exception#Exception(String, Throwable)}
     *
     * @param message the exceptions message.
     * @param cause   the Throwable that caused this exception.
     */
    public InstructionParseException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * {@link Exception#Exception(Throwable)}
     *
     * @param cause the exceptions cause.
     */
    public InstructionParseException(final Throwable cause) {
        super(cause);
    }
}
