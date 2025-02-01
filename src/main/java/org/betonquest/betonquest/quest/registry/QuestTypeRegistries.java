package org.betonquest.betonquest.quest.registry;

import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.quest.registry.type.ConditionTypeRegistry;
import org.betonquest.betonquest.quest.registry.type.EventTypeRegistry;
import org.betonquest.betonquest.quest.registry.type.NpcTypeRegistry;
import org.betonquest.betonquest.quest.registry.type.ObjectiveTypeRegistry;
import org.betonquest.betonquest.quest.registry.type.VariableTypeRegistry;

/**
 * Registry for quest core elements.
 *
 * @param condition The Registry holding registered condition types.
 * @param event     The Registry holding registered event types.
 * @param objective The Registry holding registered objective types.
 * @param npc       The Registry holding registered npc types.
 * @param variable  The Registry holding registered variable types.
 */
public record QuestTypeRegistries(
        ConditionTypeRegistry condition,
        EventTypeRegistry event,
        ObjectiveTypeRegistry objective,
        NpcTypeRegistry npc,
        VariableTypeRegistry variable
) {

    /**
     * Create a new quest registry for quest core elements.
     *
     * @param loggerFactory the logger factory to create individual class logger
     * @return the newly created registries
     */
    public static QuestTypeRegistries create(final BetonQuestLoggerFactory loggerFactory) {
        return new QuestTypeRegistries(
                new ConditionTypeRegistry(loggerFactory.create(ConditionTypeRegistry.class), loggerFactory),
                new EventTypeRegistry(loggerFactory.create(EventTypeRegistry.class), loggerFactory),
                new ObjectiveTypeRegistry(loggerFactory.create(ObjectiveTypeRegistry.class)),
                new NpcTypeRegistry(loggerFactory.create(NpcTypeRegistry.class)),
                new VariableTypeRegistry(loggerFactory.create(VariableTypeRegistry.class), loggerFactory)
        );
    }
}
