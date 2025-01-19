package org.betonquest.betonquest.web.downloader;

import java.io.Serial;

/**
 * Exception that is thrown if a qualified error occurs while running the downloader.
 * Causes could be GitHub API not responding, invalid repository names or already existing packages.
 */
public class DownloadFailedException extends Exception {
    @Serial
    private static final long serialVersionUID = 149829848798498784L;

    /**
     * {@link Exception#Exception(String, Throwable)}
     *
     * @param message the exceptions message.
     */
    public DownloadFailedException(final String message) {
        super(message);
    }

    /**
     * {@link Exception#Exception(String, Throwable)}
     *
     * @param message the exceptions message.
     * @param cause   the Throwable that caused this exception.
     */
    public DownloadFailedException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
