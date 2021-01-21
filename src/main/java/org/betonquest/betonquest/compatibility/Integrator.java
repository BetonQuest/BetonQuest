package org.betonquest.betonquest.compatibility;


import org.betonquest.betonquest.exceptions.HookException;

/**
 * Integrator object performs integration with other plugins.
 */
public interface Integrator {

    /**
     * Integrate with another plugin.
     */
    void hook() throws HookException;

    /**
     * Reload the plugin integration.
     */
    void reload();

    /**
     * Clean up everything.
     */
    void close();

}
