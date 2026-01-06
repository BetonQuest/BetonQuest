package org.betonquest.betonquest.kernel.processor.adapter;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.PlayerQuestFactory;
import org.betonquest.betonquest.api.quest.PlayerlessQuestFactory;
import org.betonquest.betonquest.api.quest.QuestTypeApi;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerlessAction;
import org.jetbrains.annotations.Nullable;

/**
 * Wrapper for factories creating actions.
 */
public class EventAdapterFactory extends QuestAdapterFactory<PlayerAction, PlayerlessAction, ActionAdapter> {

    /**
     * Logger factory to create class specific logger for quest type factories.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Quest Type API.
     */
    private final QuestTypeApi questTypeApi;

    /**
     * Create a new adapter factory from {@link org.betonquest.betonquest.api.quest QuestFactories} for
     * {@link org.betonquest.betonquest.api.quest.action Actions}.
     *
     * @param loggerFactory     the logger factory to create a new custom logger
     * @param questTypeApi      the QuestTypeAPi
     * @param playerFactory     the player factory to use
     * @param playerlessFactory the playerless factory to use
     * @throws IllegalArgumentException if no factory is given
     */
    public EventAdapterFactory(final BetonQuestLoggerFactory loggerFactory,
                               final QuestTypeApi questTypeApi, @Nullable final PlayerQuestFactory<PlayerAction> playerFactory,
                               @Nullable final PlayerlessQuestFactory<PlayerlessAction> playerlessFactory) {
        super(playerFactory, playerlessFactory);
        this.loggerFactory = loggerFactory;
        this.questTypeApi = questTypeApi;
    }

    @Override
    protected ActionAdapter getAdapter(final Instruction instruction,
                                       @Nullable final PlayerAction playerType,
                                       @Nullable final PlayerlessAction playerlessType) throws QuestException {
        return new ActionAdapter(loggerFactory.create(ActionAdapter.class), questTypeApi, instruction, playerType, playerlessType);
    }
}
