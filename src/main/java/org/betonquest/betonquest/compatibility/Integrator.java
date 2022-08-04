package org.betonquest.betonquest.compatibility;


import org.betonquest.betonquest.exceptions.HookException;

/**
 * Integrator object performs integration with other plugins.
 */
public interface Integrator {

    /**
     * Integrate with another plugin.
     *
     * @param pluginName The name of the plugin that is being hooked to
     * @throws HookException Is thrown, if the hooking was not successful
     */
    void hook(String pluginName) throws HookException;

    /**
     * Reload the plugin integration.
     */
    void reload();

    /**
     * Clean up everything.
     */
    void close();

}
