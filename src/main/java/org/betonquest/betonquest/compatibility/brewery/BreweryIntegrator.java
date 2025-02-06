package org.betonquest.betonquest.compatibility.brewery;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.brewery.condition.DrunkConditionFactory;
import org.betonquest.betonquest.compatibility.brewery.condition.DrunkQualityConditionFactory;
import org.betonquest.betonquest.compatibility.brewery.condition.HasBrewConditionFactory;
import org.betonquest.betonquest.compatibility.brewery.event.GiveBrewEventFactory;
import org.betonquest.betonquest.compatibility.brewery.event.TakeBrewEventFactory;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.registry.QuestTypeRegistries;
import org.betonquest.betonquest.quest.registry.type.ConditionTypeRegistry;
import org.betonquest.betonquest.quest.registry.type.EventTypeRegistry;
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
    public void hook() {
        final Server server = plugin.getServer();
        final PrimaryServerThreadData data = new PrimaryServerThreadData(server, server.getScheduler(), plugin);

        final QuestTypeRegistries questRegistries = plugin.getQuestRegistries();
        final EventTypeRegistry eventTypes = questRegistries.event();
        eventTypes.register("givebrew", new GiveBrewEventFactory(data));
        eventTypes.register("takebrew", new TakeBrewEventFactory(data));

        final ConditionTypeRegistry conditionTypes = questRegistries.condition();
        conditionTypes.register("drunk", new DrunkConditionFactory(data));
        conditionTypes.register("drunkquality", new DrunkQualityConditionFactory(data));
        conditionTypes.register("hasbrew", new HasBrewConditionFactory(data));
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
