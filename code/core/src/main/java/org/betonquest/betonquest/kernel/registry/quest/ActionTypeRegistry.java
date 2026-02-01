package org.betonquest.betonquest.kernel.registry.quest;

import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.PlayerQuestFactory;
import org.betonquest.betonquest.api.quest.PlayerlessQuestFactory;
import org.betonquest.betonquest.api.quest.TypeFactory;
import org.betonquest.betonquest.api.quest.action.ActionRegistry;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerlessAction;
import org.betonquest.betonquest.kernel.processor.adapter.ActionAdapter;
import org.betonquest.betonquest.kernel.processor.adapter.ActionAdapterFactory;
import org.betonquest.betonquest.kernel.registry.QuestTypeRegistry;
import org.jetbrains.annotations.Nullable;

/**
 * Stores the action types that can be used in BetonQuest.
 */
public class ActionTypeRegistry extends QuestTypeRegistry<PlayerAction, PlayerlessAction, ActionAdapter>
        implements ActionRegistry {

    /**
     * Logger factory to create class specific logger for quest type factories.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * BetonQuest API to get QuestTypeApi from.
     */
    private final BetonQuestApi api;

    /**
     * Create a new action type registry.
     *
     * @param log           the logger that will be used for logging
     * @param loggerFactory the logger factory to create a new custom logger
     * @param api           the BetonQuest API to get QuestTypeApi from once initialized
     */
    public ActionTypeRegistry(final BetonQuestLogger log, final BetonQuestLoggerFactory loggerFactory, final BetonQuestApi api) {
        super(log, "action");
        this.loggerFactory = loggerFactory;
        this.api = api;
    }

    @Override
    protected TypeFactory<ActionAdapter> getFactoryAdapter(
            @Nullable final PlayerQuestFactory<PlayerAction> playerFactory,
            @Nullable final PlayerlessQuestFactory<PlayerlessAction> playerlessFactory) {
        return new ActionAdapterFactory(loggerFactory, api.getQuestTypeApi(), playerFactory, playerlessFactory);
    }
}
