package org.betonquest.betonquest.quest.condition.slots;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.online.OnlineConditionAdapter;

/**
 * Factory for {@link EmptySlotsCondition}s.
 */
public class EmptySlotsConditionFactory implements PlayerConditionFactory {

    /**
     * Logger factory to create a logger for the conditions.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Create the empty slots condition factory.
     *
     * @param loggerFactory the logger factory to create a logger for the conditions
     */
    public EmptySlotsConditionFactory(final BetonQuestLoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<Number> required = instruction.number().get();
        final FlagArgument<Boolean> equal = instruction.bool().getFlag("equal", true);
        final BetonQuestLogger log = loggerFactory.create(EmptySlotsCondition.class);
        return new OnlineConditionAdapter(new EmptySlotsCondition(required, equal), log, instruction.getPackage());
    }
}
