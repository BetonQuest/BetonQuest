package org.betonquest.betonquest.quest.condition.hand;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.type.ItemWrapper;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.condition.OnlineConditionAdapter;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;

/**
 * Factory for {@link HandCondition}s.
 */
public class HandConditionFactory implements PlayerConditionFactory {

    /**
     * Logger factory to create a logger for the conditions.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Create the hand factory.
     *
     * @param loggerFactory the logger factory to create a logger for the conditions
     */
    public HandConditionFactory(final BetonQuestLoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<ItemWrapper> item = instruction.item().get();
        final FlagArgument<Boolean> offhand = instruction.bool().getFlag("offhand", true);
        final BetonQuestLogger log = loggerFactory.create(HandCondition.class);
        return new OnlineConditionAdapter(new HandCondition(item, offhand), log, instruction.getPackage());
    }
}
