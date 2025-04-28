package org.betonquest.betonquest.quest.objective.smelt;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.Item;
import org.betonquest.betonquest.instruction.argument.Argument;
import org.betonquest.betonquest.instruction.argument.PackageArgument;
import org.betonquest.betonquest.instruction.variable.Variable;

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
        final Variable<Item> item = instruction.get(PackageArgument.ITEM);
        final Variable<Number> targetAmount = instruction.get(Argument.NUMBER_NOT_LESS_THAN_ONE);
        return new SmeltingObjective(instruction, targetAmount, item);
    }
}
