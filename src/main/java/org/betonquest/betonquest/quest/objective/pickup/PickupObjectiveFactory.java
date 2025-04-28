package org.betonquest.betonquest.quest.objective.pickup;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.Item;
import org.betonquest.betonquest.instruction.argument.Argument;
import org.betonquest.betonquest.instruction.argument.PackageArgument;
import org.betonquest.betonquest.instruction.variable.Variable;

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

    @Override
    public Objective parseInstruction(final Instruction instruction) throws QuestException {
        final Variable<List<Item>> pickupItems = instruction.getList(PackageArgument.ITEM);
        final Variable<Number> targetAmount = instruction.get(instruction.getValue("amount", "1"), Argument.NUMBER_NOT_LESS_THAN_ONE);
        return new PickupObjective(instruction, targetAmount, pickupItems);
    }
}
