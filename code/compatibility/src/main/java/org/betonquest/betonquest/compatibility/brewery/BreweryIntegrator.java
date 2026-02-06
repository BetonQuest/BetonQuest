package org.betonquest.betonquest.compatibility.brewery;

import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.item.ItemRegistry;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.condition.ConditionRegistry;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.brewery.condition.DrunkConditionFactory;
import org.betonquest.betonquest.compatibility.brewery.condition.DrunkQualityConditionFactory;
import org.betonquest.betonquest.compatibility.brewery.item.BrewItemFactory;
import org.betonquest.betonquest.compatibility.brewery.item.BrewQuestItemSerializer;

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
        final ConditionRegistry conditionRegistry = api.getQuestRegistries().condition();
        conditionRegistry.register("drunk", new DrunkConditionFactory(loggerFactory));
        conditionRegistry.register("drunkquality", new DrunkQualityConditionFactory(loggerFactory));

        final ItemRegistry item = api.getFeatureRegistries().item();
        item.register("brew", new BrewItemFactory());
        item.registerSerializer("brew", new BrewQuestItemSerializer());
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
