package org.betonquest.betonquest.kernel.registry.quest;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.PlayerQuestFactory;
import org.betonquest.betonquest.api.quest.PlayerlessQuestFactory;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerlessEvent;
import org.betonquest.betonquest.kernel.processor.adapter.EventAdapter;
import org.betonquest.betonquest.kernel.processor.adapter.EventAdapterFactory;
import org.betonquest.betonquest.kernel.registry.QuestTypeRegistry;
import org.betonquest.betonquest.kernel.registry.TypeFactory;
import org.jetbrains.annotations.Nullable;

/**
 * Stores the event types that can be used in BetonQuest.
 */
public class EventTypeRegistry extends QuestTypeRegistry<PlayerEvent, PlayerlessEvent, EventAdapter> {

    /**
     * Logger factory to create class specific logger for quest type factories.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Plugin instance to get QuestTypeApi from.
     */
    private final BetonQuest betonQuest;

    /**
     * Create a new event type registry.
     *
     * @param log           the logger that will be used for logging
     * @param loggerFactory the logger factory to create a new custom logger
     * @param betonQuest    the plugin instance to get QuestTypeApi from once initialized
     */
    public EventTypeRegistry(final BetonQuestLogger log, final BetonQuestLoggerFactory loggerFactory, final BetonQuest betonQuest) {
        super(log, "event");
        this.loggerFactory = loggerFactory;
        this.betonQuest = betonQuest;
    }

    @Override
    protected TypeFactory<EventAdapter> getFactoryAdapter(
            @Nullable final PlayerQuestFactory<PlayerEvent> playerFactory,
            @Nullable final PlayerlessQuestFactory<PlayerlessEvent> playerlessFactory) {
        return new EventAdapterFactory(loggerFactory, betonQuest.getQuestTypeApi(), playerFactory, playerlessFactory);
    }
}
