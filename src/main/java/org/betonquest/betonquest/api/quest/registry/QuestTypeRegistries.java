package org.betonquest.betonquest.api.quest.registry;

import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.ConditionTypeRegistry;
import org.betonquest.betonquest.api.quest.EventTypeRegistry;

/**
 * Registry for quest core elements.
 */
public class QuestTypeRegistries {
    /**
     * Condition type registry.
     */
    private final ConditionTypeRegistry conditionTypes;

    /**
     * Event type registry.
     */
    private final EventTypeRegistry eventTypes;

    /**
     * Create a new quest registry for quest core elements.
     *
     * @param loggerFactory the logger factory to create individual class logger
     */
    public QuestTypeRegistries(final BetonQuestLoggerFactory loggerFactory) {
        this.conditionTypes = new ConditionTypeRegistry(loggerFactory.create(ConditionTypeRegistry.class), loggerFactory);
        this.eventTypes = new EventTypeRegistry(loggerFactory.create(EventTypeRegistry.class), loggerFactory);
    }

    /**
     * Gets the Registry holding registered condition types.
     *
     * @return registry containing usable condition types
     */
    public ConditionTypeRegistry getConditionTypes() {
        return conditionTypes;
    }

    /**
     * Gets the Registry holding registered event types.
     *
     * @return registry containing usable event types
     */
    public EventTypeRegistry getEventTypes() {
        return eventTypes;
    }
}
