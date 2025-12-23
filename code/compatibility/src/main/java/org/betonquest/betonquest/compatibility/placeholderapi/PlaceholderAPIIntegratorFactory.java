package org.betonquest.betonquest.compatibility.placeholderapi;

import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.IntegratorFactory;
import org.bukkit.plugin.PluginDescriptionFile;

/**
 * Factory for creating {@link PlaceholderAPIIntegrator} instances.
 */
public class PlaceholderAPIIntegratorFactory implements IntegratorFactory {

    /**
     * Description to use for placeholder.
     */
    private final PluginDescriptionFile descriptionFile;

    /**
     * Creates a new instance of the factory.
     *
     * @param descriptionFile the description to use for placeholder
     */
    public PlaceholderAPIIntegratorFactory(final PluginDescriptionFile descriptionFile) {
        this.descriptionFile = descriptionFile;
    }

    @Override
    public Integrator getIntegrator() {
        return new PlaceholderAPIIntegrator(descriptionFile);
    }
}
