package org.betonquest.betonquest.compatibility.placeholderapi;

import org.betonquest.betonquest.BetonQuest;
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
    public void hook() {
        plugin.getQuestRegistries().getVariableTypes().register("ph", PlaceholderVariable.class);
        final PluginDescriptionFile description = plugin.getDescription();
        new BetonQuestPlaceholder(plugin.getLoggerFactory().create(BetonQuestPlaceholder.class, "PlaceholderAPI Integration"),
                plugin.getVariableProcessor(), description.getAuthors().toString(), description.getVersion()).register();
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
