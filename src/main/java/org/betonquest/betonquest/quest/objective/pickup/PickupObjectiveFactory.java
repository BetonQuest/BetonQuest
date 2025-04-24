package org.betonquest.betonquest.quest.objective.pickup;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.Item;
import org.betonquest.betonquest.instruction.argument.Argument;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.betonquest.betonquest.instruction.variable.VariableList;

/**
 * Factory for creating {@link PickupObjective} instances from {@link Instruction}s.
 */
public class PickupObjectiveFactory implements ObjectiveFactory {
    /**
     * Logger factory to create a logger for the objectives.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Creates a new instance of the PickupObjectiveFactory.
     *
     * @param loggerFactory the logger factory to create a logger for the objectives
     */
    public PickupObjectiveFactory(final BetonQuestLoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    @SuppressWarnings("NullAway")
    @Override
    public Objective parseInstruction(final Instruction instruction) throws QuestException {
        final VariableList<Item> pickupItems = instruction.getItemList();
        final Variable<Number> targetAmount = instruction.getVariable(instruction.getOptional("amount", "1"), Argument.NUMBER_NOT_LESS_THAN_ONE);
        final BetonQuestLogger log = loggerFactory.create(PickupObjective.class);
        return new PickupObjective(instruction, targetAmount, log, pickupItems);
    }
}
