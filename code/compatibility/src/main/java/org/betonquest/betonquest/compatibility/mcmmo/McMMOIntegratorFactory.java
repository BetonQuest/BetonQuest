package org.betonquest.betonquest.compatibility.mcmmo;

import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.IntegratorFactory;
import org.bukkit.plugin.Plugin;

/**
 * Factory for creating {@link McMMOIntegrator} instances.
 */
public class McMMOIntegratorFactory implements IntegratorFactory {

    /**
     * Plugin to register listener with.
     */
    private final Plugin plugin;

    /**
     * Creates a new instance of the factory.
     *
     * @param plugin the plugin to register listener with
     */
    public McMMOIntegratorFactory(final Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public Integrator getIntegrator() {
        return new McMMOIntegrator(plugin);
    }
}
