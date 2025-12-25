package org.betonquest.betonquest.quest.objective.smelt;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.type.ItemWrapper;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;

/**
 * Factory for creating {@link SmeltingObjective} instances from {@link Instruction}s.
 */
public class SmeltingObjectiveFactory implements ObjectiveFactory {

    /**
     * Creates a new instance of the SmeltingObjectiveFactory.
     */
    public SmeltingObjectiveFactory() {
    }

    @Override
    public Objective parseInstruction(final Instruction instruction) throws QuestException {
        final Argument<ItemWrapper> item = instruction.item().get();
        final Argument<Number> targetAmount = instruction.number().atLeast(1).get();
        return new SmeltingObjective(instruction, targetAmount, item);
    }
}
