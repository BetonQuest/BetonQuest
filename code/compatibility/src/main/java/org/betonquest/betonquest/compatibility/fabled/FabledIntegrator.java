package org.betonquest.betonquest.compatibility.fabled;

import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.service.condition.ConditionRegistry;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.fabled.condition.FabledClassConditionFactory;
import org.betonquest.betonquest.compatibility.fabled.condition.FabledLevelConditionFactory;

/**
 * Integrator for Fabled.
 */
public class FabledIntegrator implements Integrator {

    /**
     * Creates a new Integrator.
     */
    public FabledIntegrator() {
    }

    @Override
    public void hook(final BetonQuestApi api) {
        final ConditionRegistry conditionRegistry = api.conditions().registry();
        conditionRegistry.register("fabledclass", new FabledClassConditionFactory());
        conditionRegistry.register("fabledlevel", new FabledLevelConditionFactory());
        api.bukkit().registerEvents(new FabledKillListener(api.profiles()));
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
