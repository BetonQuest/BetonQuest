package org.betonquest.betonquest.kernel.registry.quest;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;

/**
 * Registry for quest core elements.
 *
 * @param condition The Registry holding registered condition types.
 * @param event     The Registry holding registered event types.
 * @param objective The Registry holding registered objective types.
 * @param variable  The Registry holding registered variable types.
 */
public record QuestTypeRegistries(
        ConditionTypeRegistry condition,
        EventTypeRegistry event,
        ObjectiveTypeRegistry objective,
        VariableTypeRegistry variable
) {

    /**
     * Create a new quest registry for quest core elements.
     *
     * @param loggerFactory the logger factory to create individual class logger
     * @param betonQuest    the plugin instance to get QuestTypeAPI from once initialized
     * @return the newly created registries
     */
    public static QuestTypeRegistries create(final BetonQuestLoggerFactory loggerFactory, final BetonQuest betonQuest) {
        return new QuestTypeRegistries(
                new ConditionTypeRegistry(loggerFactory.create(ConditionTypeRegistry.class)),
                new EventTypeRegistry(loggerFactory.create(EventTypeRegistry.class), loggerFactory, betonQuest),
                new ObjectiveTypeRegistry(loggerFactory.create(ObjectiveTypeRegistry.class)),
                new VariableTypeRegistry(loggerFactory.create(VariableTypeRegistry.class))
        );
    }
}
