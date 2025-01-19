package org.betonquest.betonquest.compatibility;

import org.betonquest.betonquest.exception.HookException;

/**
 * Integrator object performs integration with other plugins.
 */
public interface Integrator {

    /**
     * Integrate with another plugin.
     *
     * @throws HookException Is thrown, if the hooking was not successful
     */
    void hook() throws HookException;

    /**
     * After all integrations are successfully hooked,
     * this method can be called to activate cross compatibility features.
     *
     * @throws HookException When a cross compatibility failed to hook.
     */
    default void postHook() throws HookException {
        // Empty
    }

    /**
     * Reload the plugin integration.
     */
    void reload();

    /**
     * Clean up everything.
     */
    void close();

}
