package org.betonquest.betonquest.quest.objective.pickup;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.Item;
import org.betonquest.betonquest.instruction.argument.VariableArgument;
import org.betonquest.betonquest.instruction.variable.VariableNumber;

import java.util.List;

/**
 * Factory for creating {@link PickupObjective} instances from {@link Instruction}s.
 */
public class PickupObjectiveFactory implements ObjectiveFactory {
    /**
     * Creates a new instance of the PickupObjectiveFactory.
     */
    public PickupObjectiveFactory() {
    }

    @SuppressWarnings("NullAway")
    @Override
    public Objective parseInstruction(final Instruction instruction) throws QuestException {
        final List<Item> pickupItems = instruction.getList(instruction::getItem);
        final VariableNumber targetAmount = instruction.get(instruction.getOptional("amount", "1"), VariableArgument.NUMBER_NOT_LESS_THAN_ONE);
        return new PickupObjective(instruction, pickupItems, targetAmount);
    }
}
