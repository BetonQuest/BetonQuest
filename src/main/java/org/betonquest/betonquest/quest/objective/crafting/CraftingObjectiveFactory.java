package org.betonquest.betonquest.quest.objective.crafting;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.Item;
import org.betonquest.betonquest.instruction.argument.VariableArgument;
import org.betonquest.betonquest.instruction.variable.VariableNumber;

/**
 * Factory for creating {@link CraftingObjective} instances from {@link Instruction}s.
 */
public class CraftingObjectiveFactory implements ObjectiveFactory {
    /**
     * Creates a new CraftingObjectiveFactory instance.
     */
    public CraftingObjectiveFactory() {
    }

    @Override
    public Objective parseInstruction(final Instruction instruction) throws QuestException {
        final Item item = instruction.getItem();
        final VariableNumber targetAmount = instruction.get(VariableArgument.NUMBER_NOT_LESS_THAN_ONE);
        return new CraftingObjective(instruction, item, targetAmount);
    }
}
