package org.betonquest.betonquest.compatibility.placeholderapi;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.compatibility.Integrator;
import org.bukkit.plugin.PluginDescriptionFile;

/**
 * Integrator for PlaceholderAPI.
 */
public class PlaceholderAPIIntegrator implements Integrator {

    /**
     * The BetonQuest plugin instance.
     */
    private final BetonQuest plugin;

    /**
     * The default constructor.
     */
    public PlaceholderAPIIntegrator() {
        plugin = BetonQuest.getInstance();
    }

    @Override
    public void hook(final BetonQuestApi api) {
        api.getQuestRegistries().variable().registerCombined("ph", new PlaceholderVariableFactory());
        final PluginDescriptionFile description = plugin.getDescription();
        new BetonQuestPlaceholder(api.getLoggerFactory().create(BetonQuestPlaceholder.class, "PlaceholderAPI Integration"),
                api.getProfileProvider(), api.getQuestTypeApi().variables(), description.getAuthors().toString(), description.getVersion()).register();
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
