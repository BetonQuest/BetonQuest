package org.betonquest.betonquest.compatibility.placeholderapi;

import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.compatibility.Integrator;
import org.bukkit.plugin.PluginDescriptionFile;

/**
 * Integrator for PlaceholderAPI.
 */
public class PlaceholderAPIIntegrator implements Integrator {

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
    public void hook(final BetonQuestApi api) {
        api.getQuestRegistries().placeholder().registerCombined("ph", new PlaceholderAPIPlaceholderFactory());
        new BetonQuestPlaceholder(api.getLoggerFactory().create(BetonQuestPlaceholder.class, "PlaceholderAPI Integration"),
                api.getProfileProvider(), api.getQuestTypeApi().placeholders(), description.getAuthors().toString(), description.getVersion()).register();
    }

    @Override
    public void reload() {
        // Empty
    }

    @Override
    public void close() {
        // Empty
    }
}
