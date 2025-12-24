package org.betonquest.betonquest.compatibility.brewery;

import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.QuestTypeRegistries;
import org.betonquest.betonquest.api.quest.condition.ConditionRegistry;
import org.betonquest.betonquest.api.quest.event.EventRegistry;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.brewery.condition.DrunkConditionFactory;
import org.betonquest.betonquest.compatibility.brewery.condition.DrunkQualityConditionFactory;
import org.betonquest.betonquest.compatibility.brewery.condition.HasBrewConditionFactory;
import org.betonquest.betonquest.compatibility.brewery.event.GiveBrewEventFactory;
import org.betonquest.betonquest.compatibility.brewery.event.TakeBrewEventFactory;

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
        final EventRegistry eventRegistry = questRegistries.event();
        eventRegistry.register("givebrew", new GiveBrewEventFactory(loggerFactory));
        eventRegistry.register("takebrew", new TakeBrewEventFactory(loggerFactory));

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
