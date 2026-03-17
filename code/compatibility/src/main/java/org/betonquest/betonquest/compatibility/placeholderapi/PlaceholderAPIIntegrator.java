package org.betonquest.betonquest.compatibility.placeholderapi;

import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.integration.Integration;
import org.bukkit.plugin.PluginDescriptionFile;

/**
 * Integrator for PlaceholderAPI.
 */
public class PlaceholderAPIIntegrator implements Integration {

    /**
     * Description to use for placeholder.
     */
    private final PluginDescriptionFile description;

    /**
     * Creates a new Integrator.
     *
     * @param description the description to use for placeholder
     */
    public PlaceholderAPIIntegrator(final PluginDescriptionFile description) {
        this.description = description;
    }

    @Override
    public void enable(final BetonQuestApi api) {
        api.placeholders().registry().registerCombined("ph", new PlaceholderAPIPlaceholderFactory());
        new BetonQuestPlaceholder(api.loggerFactory().create(BetonQuestPlaceholder.class, "PlaceholderAPI Integration"),
                api.profiles(), api.placeholders().manager(), description.getAuthors().toString(), description.getVersion()).register();
    }

    @Override
    public void postEnable(final BetonQuestApi api) {
        // Empty
    }

    @Override
    public void disable() {
        // Empty
    }
}
