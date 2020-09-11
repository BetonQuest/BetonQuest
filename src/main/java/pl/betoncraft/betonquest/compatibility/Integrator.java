package pl.betoncraft.betonquest.compatibility;


/**
 * Integrator object performs integration with other plugins.
 */
public interface Integrator {

    /**
     * Integrate with another plugin.
     */
    void hook() throws Exception;

    /**
     * Reload the plugin integration.
     */
    void reload();

    /**
     * Clean up everything.
     */
    void close();

}
