package org.betonquest.betonquest.compatibility.brewery;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.api.quest.QuestTypeRegistries;
import org.betonquest.betonquest.api.quest.condition.ConditionRegistry;
import org.betonquest.betonquest.api.quest.event.EventRegistry;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.brewery.condition.DrunkConditionFactory;
import org.betonquest.betonquest.compatibility.brewery.condition.DrunkQualityConditionFactory;
import org.betonquest.betonquest.compatibility.brewery.condition.HasBrewConditionFactory;
import org.betonquest.betonquest.compatibility.brewery.event.GiveBrewEventFactory;
import org.betonquest.betonquest.compatibility.brewery.event.TakeBrewEventFactory;
import org.bukkit.Server;

/**
 * Integrator for the Brewery plugin.
 */
public class BreweryIntegrator implements Integrator {
    /**
     * The {@link BetonQuest} plugin instance.
     */
    private final BetonQuest plugin;

    /**
     * Create a new Brewery Integrator.
     */
    public BreweryIntegrator() {
        plugin = BetonQuest.getInstance();
    }

    @Override
    public void hook(final BetonQuestApi api) {
        final Server server = plugin.getServer();
        final PrimaryServerThreadData data = new PrimaryServerThreadData(server, server.getScheduler(), plugin);

        final BetonQuestLoggerFactory loggerFactory = api.getLoggerFactory();
        final QuestTypeRegistries questRegistries = api.getQuestRegistries();
        final EventRegistry eventRegistry = questRegistries.event();
        eventRegistry.register("givebrew", new GiveBrewEventFactory(loggerFactory, data));
        eventRegistry.register("takebrew", new TakeBrewEventFactory(loggerFactory, data));

        final ConditionRegistry conditionRegistry = questRegistries.condition();
        conditionRegistry.register("drunk", new DrunkConditionFactory(loggerFactory, data));
        conditionRegistry.register("drunkquality", new DrunkQualityConditionFactory(loggerFactory, data));
        conditionRegistry.register("hasbrew", new HasBrewConditionFactory(loggerFactory, data));
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
