package org.betonquest.betonquest.modules.logger.handler;

import org.betonquest.betonquest.modules.logger.handler.history.LogPublishingController;

/**
 * Debug configuration that is kept in memory only and not persisted.
 */
public class MemoryDebugConfig implements LogPublishingController {

    /**
     * Storage for the debugging state.
     */
    private boolean logging;

    /**
     * Create a memory logging config.
     *
     * @param logging initial value for logging
     */
    public MemoryDebugConfig(final boolean logging) {
        this.logging = logging;
    }

    @Override
    public boolean isLogging() {
        return logging;
    }

    @Override
    public void startLogging() {
        logging = true;
    }

    @Override
    public void stopLogging() {
        logging = false;
    }
}
