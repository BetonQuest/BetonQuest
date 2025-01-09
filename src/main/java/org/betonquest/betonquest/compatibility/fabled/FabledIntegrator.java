package org.betonquest.betonquest.compatibility.fabled;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.quest.registry.type.ConditionTypeRegistry;

@SuppressWarnings("PMD.CommentRequired")
public class FabledIntegrator implements Integrator {
    private final BetonQuest plugin;

    public FabledIntegrator() {
        plugin = BetonQuest.getInstance();
    }

    @Override
    public void hook() {
        final ConditionTypeRegistry conditionTypes = plugin.getQuestRegistries().getConditionTypes();
        conditionTypes.register("fabledclass", FabledClassCondition.class);
        conditionTypes.register("fabledlevel", FabledLevelCondition.class);
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
