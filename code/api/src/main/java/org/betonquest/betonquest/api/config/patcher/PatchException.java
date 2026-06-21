package org.betonquest.betonquest.api.config.patcher;

import java.io.Serial;

/**
 * Exception to be thrown when a patch fails.
 *
 * @since 3.0.0
 */
public class PatchException extends Exception {

    /**
     * Serial version UID.
     */
    @Serial
    private static final long serialVersionUID = -3188700840301371500L;

    /**
     * Constructs a new PatchException.
     * Should be used whenever a patch left the happy path.
     *
     * @param message to show in the console
     * @since 3.0.0
     */
    public PatchException(final String message) {
        super(message);
    }

    /**
     * Constructs a new PatchException.
     * Should be used whenever a patch left the happy path.
     *
     * @param message to show in the console
     * @param cause   to show in the console
     * @since 3.0.0
     */
    public PatchException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
