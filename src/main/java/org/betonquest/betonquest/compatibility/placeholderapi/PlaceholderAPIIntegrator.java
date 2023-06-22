package org.betonquest.betonquest.compatibility.placeholderapi;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.compatibility.Integrator;

@SuppressWarnings("PMD.CommentRequired")
public class PlaceholderAPIIntegrator implements Integrator {

    private final BetonQuest plugin;

    public PlaceholderAPIIntegrator() {
        plugin = BetonQuest.getInstance();
    }

    @Override
    public void hook() {
        plugin.registerVariable("ph", PlaceholderVariable.class);
        new BetonQuestPlaceholder(plugin.getLoggerFactory().create(BetonQuestPlaceholder.class, "PlaceholderAPI Integration")).register();
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
