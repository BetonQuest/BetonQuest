package org.betonquest.betonquest.compatibility.fabled;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.api.quest.condition.ConditionRegistry;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.fabled.condition.FabledClassConditionFactory;
import org.betonquest.betonquest.compatibility.fabled.condition.FabledLevelConditionFactory;

/**
 * Integrator for Fabled.
 */
public class FabledIntegrator implements Integrator {
    /**
     * The plugin instance.
     */
    private final BetonQuest plugin;

    /**
     * The default constructor.
     */
    public FabledIntegrator() {
        plugin = BetonQuest.getInstance();
    }

    @Override
    public void hook(final BetonQuestApi api) {
        final PrimaryServerThreadData data = api.getPrimaryServerThreadData();

        final ConditionRegistry conditionRegistry = api.getQuestRegistries().condition();
        conditionRegistry.register("fabledclass", new FabledClassConditionFactory(data));
        conditionRegistry.register("fabledlevel", new FabledLevelConditionFactory(data));
        plugin.getServer().getPluginManager().registerEvents(new FabledKillListener(api.getProfileProvider()), plugin);
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
