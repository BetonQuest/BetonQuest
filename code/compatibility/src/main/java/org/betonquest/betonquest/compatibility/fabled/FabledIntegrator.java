package org.betonquest.betonquest.compatibility.fabled;

import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.api.quest.condition.ConditionRegistry;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.fabled.condition.FabledClassConditionFactory;
import org.betonquest.betonquest.compatibility.fabled.condition.FabledLevelConditionFactory;
import org.bukkit.plugin.Plugin;

/**
 * Integrator for Fabled.
 */
public class FabledIntegrator implements Integrator {

    /**
     * Plugin to register listener with.
     */
    private final Plugin plugin;

    /**
     * Creates a new Integrator.
     *
     * @param plugin the plugin to register listener with
     */
    public FabledIntegrator(final Plugin plugin) {
        this.plugin = plugin;
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
