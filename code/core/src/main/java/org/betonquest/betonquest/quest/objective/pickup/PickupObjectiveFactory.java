package org.betonquest.betonquest.quest.objective.pickup;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.type.ItemWrapper;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;

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
        final Variable<List<ItemWrapper>> pickupItems = instruction.item().getList();
        final Variable<Number> targetAmount = instruction.number().get("amount", 1);
        return new PickupObjective(instruction, targetAmount, pickupItems);
    }
}
