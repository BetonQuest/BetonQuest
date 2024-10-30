package org.betonquest.betonquest.quest.condition.variable;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerlessConditionFactory;
import org.betonquest.betonquest.api.quest.condition.nullable.NullableConditionAdapter;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.instruction.variable.VariableString;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.condition.PrimaryServerThreadPlayerCondition;
import org.betonquest.betonquest.quest.condition.PrimaryServerThreadPlayerlessCondition;
import org.betonquest.betonquest.quest.registry.processor.VariableProcessor;

/**
 * Factory for {@link VariableCondition}s.
 */
public class VariableConditionFactory implements PlayerConditionFactory, PlayerlessConditionFactory {

    /**
     * Logger factory to create a logger for events.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Data used for primary server access.
     */
    private final PrimaryServerThreadData data;

    /**
     * Processor to create new variables.
     */
    private final VariableProcessor variableProcessor;

    /**
     * Creates a new factory for {@link VariableCondition}s.
     *
     * @param loggerFactory     the logger factory
     * @param data              the data used for primary server access
     * @param variableProcessor the processor to create new variables
     */
    public VariableConditionFactory(final BetonQuestLoggerFactory loggerFactory, final PrimaryServerThreadData data, final VariableProcessor variableProcessor) {
        this.loggerFactory = loggerFactory;
        this.data = data;
        this.variableProcessor = variableProcessor;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws InstructionParseException {
        final NullableConditionAdapter condition = new NullableConditionAdapter(parse(instruction));
        if (instruction.hasArgument("forceSync")) {
            return new PrimaryServerThreadPlayerCondition(condition, data);
        }
        return condition;
    }

    @Override
    public PlayerlessCondition parsePlayerless(final Instruction instruction) throws InstructionParseException {
        final NullableConditionAdapter condition = new NullableConditionAdapter(parse(instruction));
        if (instruction.hasArgument("forceSync")) {
            return new PrimaryServerThreadPlayerlessCondition(condition, data);
        }
        return condition;
    }

    private VariableCondition parse(final Instruction instruction) throws InstructionParseException {
        final VariableString variable = new VariableString(variableProcessor, instruction.getPackage(), instruction.next());
        final VariableString regex = new VariableString(variableProcessor, instruction.getPackage(), instruction.next(), true);
        final String variableAddress = instruction.getID().toString();
        final BetonQuestLogger log = loggerFactory.create(VariableCondition.class);
        return new VariableCondition(log, variable, regex, variableAddress);
    }
}
