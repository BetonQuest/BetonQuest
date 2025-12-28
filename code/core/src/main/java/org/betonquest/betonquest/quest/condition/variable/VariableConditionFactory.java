package org.betonquest.betonquest.quest.condition.variable;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerlessConditionFactory;
import org.betonquest.betonquest.api.quest.condition.nullable.NullableConditionAdapter;

/**
 * Factory for {@link VariableCondition}s.
 */
public class VariableConditionFactory implements PlayerConditionFactory, PlayerlessConditionFactory {

    /**
     * Logger factory to create a logger for the conditions.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Creates a new factory for {@link VariableCondition}s.
     *
     * @param loggerFactory the logger factory to create a logger for the conditions
     */
    public VariableConditionFactory(final BetonQuestLoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        return new NullableConditionAdapter(parse(instruction));
    }

    @Override
    public PlayerlessCondition parsePlayerless(final Instruction instruction) throws QuestException {
        return new NullableConditionAdapter(parse(instruction));
    }

    private VariableCondition parse(final Instruction instruction) throws QuestException {
        final Argument<String> argument = instruction.string().get();
        final Argument<String> regex = instruction.string().get();
        final String variableAddress = instruction.getID().toString();
        final FlagArgument<Boolean> forceSync = instruction.bool().getFlag("forceSync", true);
        final BetonQuestLogger log = loggerFactory.create(VariableCondition.class);
        return new VariableCondition(log, argument, regex, variableAddress, forceSync.getValue(null).orElse(false));
    }
}
