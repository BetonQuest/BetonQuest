package org.betonquest.betonquest.compatibility.brewery;

import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.QuestTypeRegistries;
import org.betonquest.betonquest.api.quest.action.ActionRegistry;
import org.betonquest.betonquest.api.quest.condition.ConditionRegistry;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.brewery.action.GiveBrewActionFactory;
import org.betonquest.betonquest.compatibility.brewery.action.TakeBrewActionFactory;
import org.betonquest.betonquest.compatibility.brewery.condition.DrunkConditionFactory;
import org.betonquest.betonquest.compatibility.brewery.condition.DrunkQualityConditionFactory;
import org.betonquest.betonquest.compatibility.brewery.condition.HasBrewConditionFactory;

/**
 * Integrator for the Brewery plugin.
 */
public class BreweryIntegrator implements Integrator {

    /**
     * Create a new Brewery Integrator.
     */
    public BreweryIntegrator() {
    }

    @Override
    public void hook(final BetonQuestApi api) {
        final BetonQuestLoggerFactory loggerFactory = api.getLoggerFactory();
        final QuestTypeRegistries questRegistries = api.getQuestRegistries();
        final ActionRegistry actionRegistry = questRegistries.action();
        actionRegistry.register("givebrew", new GiveBrewActionFactory(loggerFactory));
        actionRegistry.register("takebrew", new TakeBrewActionFactory(loggerFactory));

        final ConditionRegistry conditionRegistry = questRegistries.condition();
        conditionRegistry.register("drunk", new DrunkConditionFactory(loggerFactory));
        conditionRegistry.register("drunkquality", new DrunkQualityConditionFactory(loggerFactory));
        conditionRegistry.register("hasbrew", new HasBrewConditionFactory(loggerFactory));
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
