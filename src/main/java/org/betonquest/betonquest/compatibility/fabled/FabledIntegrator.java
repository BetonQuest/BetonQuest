package org.betonquest.betonquest.compatibility.fabled;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.compatibility.Integrator;

@SuppressWarnings("PMD.CommentRequired")
public class FabledIntegrator implements Integrator {
    private final BetonQuest plugin;

    public FabledIntegrator() {
        plugin = BetonQuest.getInstance();
    }

    @Override
    public void hook() {
        plugin.registerConditions("fabledclass", FabledClassCondition.class);
        plugin.registerConditions("fabledlevel", FabledLevelCondition.class);
        plugin.getServer().getPluginManager().registerEvents(new FabledKillListener(), plugin);
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
