package org.betonquest.betonquest.modules.logger.handler;

/**
 * Debug configuration that is kept in memory only and not persisted.
 */
public class MemoryDebugConfig implements DebugConfig {

    /**
     * Storage for the debugging state.
     */
    private boolean debugging;

    /**
     * Create a memory debugging config.
     *
     * @param debugging initial value for debugging
     */
    public MemoryDebugConfig(final boolean debugging) {
        this.debugging = debugging;
    }

    @Override
    public boolean isDebugging() {
        return debugging;
    }

    @Override
    public void setDebugging(final boolean debugging) {
        this.debugging = debugging;
    }
}
