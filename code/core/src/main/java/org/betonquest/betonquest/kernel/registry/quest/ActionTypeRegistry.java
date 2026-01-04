package org.betonquest.betonquest.kernel.registry.quest;

import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.kernel.TypeFactory;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.PlayerQuestFactory;
import org.betonquest.betonquest.api.quest.PlayerlessQuestFactory;
import org.betonquest.betonquest.api.quest.event.EventRegistry;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerlessEvent;
import org.betonquest.betonquest.kernel.processor.adapter.ActionAdapter;
import org.betonquest.betonquest.kernel.processor.adapter.EventAdapterFactory;
import org.betonquest.betonquest.kernel.registry.QuestTypeRegistry;
import org.jetbrains.annotations.Nullable;

/**
 * Stores the action types that can be used in BetonQuest.
 */
public class ActionTypeRegistry extends QuestTypeRegistry<PlayerEvent, PlayerlessEvent, ActionAdapter>
        implements EventRegistry {

    /**
     * Logger factory to create class specific logger for quest type factories.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * BetonQuest API to get QuestTypeApi from.
     */
    private final BetonQuestApi api;

    /**
     * Create a new event type registry.
     *
     * @param log           the logger that will be used for logging
     * @param loggerFactory the logger factory to create a new custom logger
     * @param api           the BetonQuest API to get QuestTypeApi from once initialized
     */
    public ActionTypeRegistry(final BetonQuestLogger log, final BetonQuestLoggerFactory loggerFactory, final BetonQuestApi api) {
        super(log, "event");
        this.loggerFactory = loggerFactory;
        this.api = api;
    }

    @Override
    protected TypeFactory<ActionAdapter> getFactoryAdapter(
            @Nullable final PlayerQuestFactory<PlayerEvent> playerFactory,
            @Nullable final PlayerlessQuestFactory<PlayerlessEvent> playerlessFactory) {
        return new EventAdapterFactory(loggerFactory, api.getQuestTypeApi(), playerFactory, playerlessFactory);
    }
}
