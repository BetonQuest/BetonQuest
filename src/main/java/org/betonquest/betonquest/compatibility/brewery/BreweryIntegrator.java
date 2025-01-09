package org.betonquest.betonquest.compatibility.brewery;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.quest.registry.QuestTypeRegistries;
import org.betonquest.betonquest.quest.registry.type.ConditionTypeRegistry;
import org.betonquest.betonquest.quest.registry.type.EventTypeRegistry;

@SuppressWarnings("PMD.CommentRequired")
public class BreweryIntegrator implements Integrator {

    private final BetonQuest plugin;

    public BreweryIntegrator() {
        plugin = BetonQuest.getInstance();
    }

    @Override
    public void hook() {
        final QuestTypeRegistries questRegistries = plugin.getQuestRegistries();
        final EventTypeRegistry eventTypes = questRegistries.getEventTypes();
        eventTypes.register("givebrew", GiveBrewEvent.class);
        eventTypes.register("takebrew", TakeBrewEvent.class);

        final ConditionTypeRegistry conditionTypes = questRegistries.getConditionTypes();
        conditionTypes.register("drunk", DrunkCondition.class);
        conditionTypes.register("drunkquality", DrunkQualityCondition.class);
        conditionTypes.register("hasbrew", HasBrewCondition.class);
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
