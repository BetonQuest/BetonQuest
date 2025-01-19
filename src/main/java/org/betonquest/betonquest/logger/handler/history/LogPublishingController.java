package org.betonquest.betonquest.logger.handler.history;

import java.io.IOException;

/**
 * This interface provides methods to manage settings related to debug settings.
 */
public interface LogPublishingController {

    /**
     * Check whether logging is enabled.
     *
     * @return True, if debugging is enabled
     */
    boolean isLogging();

    /**
     * Starts debugging.
     *
     * @throws IOException If there was an issue start debugging
     */
    void startLogging() throws IOException;

    /**
     * Stops debugging.
     *
     * @throws IOException If there was an issue stop debugging
     */
    void stopLogging() throws IOException;
}
