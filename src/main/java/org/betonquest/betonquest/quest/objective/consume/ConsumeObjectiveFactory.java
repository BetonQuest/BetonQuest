package org.betonquest.betonquest.quest.objective.consume;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.Item;
import org.betonquest.betonquest.instruction.argument.VariableArgument;
import org.betonquest.betonquest.instruction.variable.VariableNumber;

/**
 * Factory for creating {@link ConsumeObjective} instances from {@link Instruction}s.
 */
public class ConsumeObjectiveFactory implements ObjectiveFactory {
    /**
     * The name of the argument that determines the amount of items to consume.
     */
    public static final String AMOUNT_ARGUMENT = "amount";

    /**
     * Logger factory to create a logger for the objectives.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Constructs a new ConsumeObjectiveFactory.
     *
     * @param loggerFactory the logger factory to create a logger for the objectives
     */
    public ConsumeObjectiveFactory(final BetonQuestLoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    @Override
    public Objective parseInstruction(final Instruction instruction) throws QuestException {
        final Item item = instruction.getItem();
        final VariableNumber targetAmount = instruction.get(instruction.getOptional(AMOUNT_ARGUMENT, "1"), VariableArgument.NUMBER_NOT_LESS_THAN_ONE);
        final BetonQuestLogger log = loggerFactory.create(ConsumeObjective.class);
        return new ConsumeObjective(instruction, targetAmount, log, item);
    }
}
