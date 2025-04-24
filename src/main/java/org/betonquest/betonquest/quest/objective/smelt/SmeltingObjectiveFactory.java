package org.betonquest.betonquest.quest.objective.smelt;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.Item;
import org.betonquest.betonquest.instruction.argument.Argument;
import org.betonquest.betonquest.instruction.variable.Variable;

/**
 * Factory for creating {@link SmeltingObjective} instances from {@link Instruction}s.
 */
public class SmeltingObjectiveFactory implements ObjectiveFactory {
    /**
     * Logger factory to create a logger for the objectives.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Creates a new instance of the SmeltingObjectiveFactory.
     *
     * @param loggerFactory the logger factory to create a logger for the objectives
     */
    public SmeltingObjectiveFactory(final BetonQuestLoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    @Override
    public Objective parseInstruction(final Instruction instruction) throws QuestException {
        final Item item = instruction.getItem();
        final Variable<Number> targetAmount = instruction.getVariable(Argument.NUMBER_NOT_LESS_THAN_ONE);
        final BetonQuestLogger log = loggerFactory.create(SmeltingObjective.class);
        return new SmeltingObjective(instruction, targetAmount, log, item);
    }
}
