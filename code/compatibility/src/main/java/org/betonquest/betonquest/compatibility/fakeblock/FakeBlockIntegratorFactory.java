package org.betonquest.betonquest.compatibility.fakeblock;

import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.IntegratorFactory;
import org.bukkit.plugin.Plugin;

/**
 * Factory for creating {@link FakeBlockIntegrator} instances.
 */
public class FakeBlockIntegratorFactory implements IntegratorFactory {

    /**
     * Plugin.
     */
    private final Plugin plugin;

    /**
     * Creates a new instance of the factory.
     *
     * @param plugin the plugin
     */
    public FakeBlockIntegratorFactory(final Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public Integrator getIntegrator() {
        return new FakeBlockIntegrator(plugin);
    }
}
