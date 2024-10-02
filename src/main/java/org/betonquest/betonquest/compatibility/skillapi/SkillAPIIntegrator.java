package org.betonquest.betonquest.compatibility.skillapi;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.compatibility.Integrator;

@SuppressWarnings("PMD.CommentRequired")
public class SkillAPIIntegrator implements Integrator {
    private final BetonQuest plugin;

    public SkillAPIIntegrator() {
        plugin = BetonQuest.getInstance();
    }

    @Override
    public void hook() {
        plugin.registerConditions("skillapiclass", SkillAPIClassCondition.class);
        plugin.registerConditions("skillapilevel", SkillAPILevelCondition.class);
        plugin.getServer().getPluginManager().registerEvents(new SkillAPIKillListener(), plugin);
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
