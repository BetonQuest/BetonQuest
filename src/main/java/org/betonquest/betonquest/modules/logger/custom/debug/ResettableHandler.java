package org.betonquest.betonquest.modules.logger.custom.debug;

import java.util.logging.Handler;

/**
 * This is a {@link Handler}, that has a reset method to clean up things
 * and reset everything to start instead of closing it.
 */
public abstract class ResettableHandler extends Handler {
    /**
     * @see Handler#Handler()
     */
    public ResettableHandler() {
        super();
    }

    /**
     * Method to reset this {@link Handler}.
     * <p>
     * This should do nothing, after the {@link Handler#close()} method was called.
     */
    public abstract void reset();
}
