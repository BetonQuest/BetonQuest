package org.betonquest.betonquest.config.patcher.migration;

import java.io.Serial;

/**
 * This exception is thrown when there is a problem with versions.
 */
public class VersionMissmatchException extends Exception {

    @Serial
    private static final long serialVersionUID = 1113625566182978578L;

    /**
     * Create a new Exception with a message.
     *
     * @param message the exception message
     */
    public VersionMissmatchException(final String message) {
        super(message);
    }
}
