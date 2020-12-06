package pl.betoncraft.betonquest.compatibility.placeholderapi;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.compatibility.Integrator;


@SuppressWarnings("PMD.CommentRequired")
public class PlaceholderAPIIntegrator implements Integrator {

    private final BetonQuest plugin;

    public PlaceholderAPIIntegrator() {
        plugin = BetonQuest.getInstance();
    }

    @Override
    public void hook() {
        plugin.registerVariable("ph", PlaceholderVariable.class);
        new BetonQuestPlaceholder().register();
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
