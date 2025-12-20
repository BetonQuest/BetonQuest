package org.betonquest.betonquest.quest.condition.variable;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.DefaultInstruction;
import org.betonquest.betonquest.api.instruction.argument.Argument;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerlessConditionFactory;
import org.betonquest.betonquest.api.quest.condition.nullable.NullableConditionAdapter;
import org.betonquest.betonquest.api.quest.condition.thread.PrimaryServerThreadPlayerCondition;
import org.betonquest.betonquest.api.quest.condition.thread.PrimaryServerThreadPlayerlessCondition;

/**
 * Factory for {@link VariableCondition}s.
 */
public class VariableConditionFactory implements PlayerConditionFactory, PlayerlessConditionFactory {

    /**
     * Logger factory to create a logger for the conditions.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Data used for primary server access.
     */
    private final PrimaryServerThreadData data;

    /**
     * Creates a new factory for {@link VariableCondition}s.
     *
     * @param loggerFactory the logger factory to create a logger for the conditions
     * @param data          the data used for primary server access
     */
    public VariableConditionFactory(final BetonQuestLoggerFactory loggerFactory, final PrimaryServerThreadData data) {
        this.loggerFactory = loggerFactory;
        this.data = data;
    }

    @Override
    public PlayerCondition parsePlayer(final DefaultInstruction instruction) throws QuestException {
        final NullableConditionAdapter condition = new NullableConditionAdapter(parse(instruction));
        if (instruction.hasArgument("forceSync")) {
            return new PrimaryServerThreadPlayerCondition(condition, data);
        }
        return condition;
    }

    @Override
    public PlayerlessCondition parsePlayerless(final DefaultInstruction instruction) throws QuestException {
        final NullableConditionAdapter condition = new NullableConditionAdapter(parse(instruction));
        if (instruction.hasArgument("forceSync")) {
            return new PrimaryServerThreadPlayerlessCondition(condition, data);
        }
        return condition;
    }

    private VariableCondition parse(final DefaultInstruction instruction) throws QuestException {
        final Variable<String> variable = instruction.get(Argument.STRING);
        final Variable<String> regex = instruction.get(Argument.STRING);
        final String variableAddress = instruction.getID().toString();
        final BetonQuestLogger log = loggerFactory.create(VariableCondition.class);
        return new VariableCondition(log, variable, regex, variableAddress);
    }
}
