package org.betonquest.betonquest.kernel.processor.adapter;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.ConditionIdentifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.PlayerQuestFactory;
import org.betonquest.betonquest.api.quest.PlayerlessQuestFactory;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerlessAction;
import org.betonquest.betonquest.api.service.condition.ConditionManager;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * Wrapper for factories creating actions.
 */
public class ActionAdapterFactory extends QuestAdapterFactory<PlayerAction, PlayerlessAction, ActionAdapter> {

    /**
     * Logger factory to create class-specific logger for quest type factories.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * The condition manager.
     */
    private final ConditionManager conditionManager;

    /**
     * Create a new adapter factory from {@link org.betonquest.betonquest.api.quest QuestFactories} for
     * {@link org.betonquest.betonquest.api.quest.action Actions}.
     *
     * @param loggerFactory     the logger factory to create a new custom logger
     * @param conditionManager  the condition manager
     * @param playerFactory     the player factory to use
     * @param playerlessFactory the playerless factory to use
     * @throws IllegalArgumentException if no factory is given
     */
    public ActionAdapterFactory(final BetonQuestLoggerFactory loggerFactory, final ConditionManager conditionManager,
                                @Nullable final PlayerQuestFactory<PlayerAction> playerFactory,
                                @Nullable final PlayerlessQuestFactory<PlayerlessAction> playerlessFactory) {
        super(playerFactory, playerlessFactory);
        this.loggerFactory = loggerFactory;
        this.conditionManager = conditionManager;
    }

    @Override
    protected ActionAdapter getAdapter(final Instruction instruction,
                                       @Nullable final PlayerAction playerType,
                                       @Nullable final PlayerlessAction playerlessType) throws QuestException {
        final Argument<List<ConditionIdentifier>> conditions = instruction.identifier(ConditionIdentifier.class)
                .list().get("conditions", Collections.emptyList());
        return new ActionAdapter(loggerFactory.create(ActionAdapter.class), conditionManager, instruction.getID(),
                playerType, playerlessType, conditions);
    }
}
