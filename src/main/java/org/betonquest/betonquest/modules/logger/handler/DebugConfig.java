package org.betonquest.betonquest.modules.logger.handler;

import java.io.IOException;

/**
 * Facade for controlling the debugging setting of the config.
 */
public interface DebugConfig {
    /**
     * Get whether debugging is enabled.
     *
     * @return true if debugging is enabled; false otherwise
     */
    boolean isDebugging();

    /**
     * Set debugging enabled state.
     *
     * @param debugging value to set
     * @throws IOException when persisting the changed state fails
     */
    void setDebugging(boolean debugging) throws IOException;
}
