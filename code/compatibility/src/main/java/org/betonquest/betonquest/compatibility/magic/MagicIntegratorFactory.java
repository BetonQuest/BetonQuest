package org.betonquest.betonquest.compatibility.magic;

import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.IntegratorFactory;
import org.bukkit.plugin.Plugin;

/**
 * Factory for creating {@link MagicIntegrator} instances.
 */
public class MagicIntegratorFactory implements IntegratorFactory {

    /**
     * Plugin to register listener with.
     */
    private final Plugin plugin;

    /**
     * Creates a new instance of the factory.
     *
     * @param plugin the plugin to register listener with
     */
    public MagicIntegratorFactory(final Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public Integrator getIntegrator() {
        return new MagicIntegrator(plugin);
    }
}
