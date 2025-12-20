package org.betonquest.betonquest.quest.objective.crafting;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.DefaultInstruction;
import org.betonquest.betonquest.api.instruction.Item;
import org.betonquest.betonquest.api.instruction.argument.Argument;
import org.betonquest.betonquest.api.instruction.argument.InstructionIdentifierArgument;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;

/**
 * Factory for creating {@link CraftingObjective} instances from {@link DefaultInstruction}s.
 */
public class CraftingObjectiveFactory implements ObjectiveFactory {

    /**
     * Creates a new CraftingObjectiveFactory instance.
     */
    public CraftingObjectiveFactory() {
    }

    @Override
    public Objective parseInstruction(final DefaultInstruction instruction) throws QuestException {
        final Variable<Item> item = instruction.get(InstructionIdentifierArgument.ITEM);
        final Variable<Number> targetAmount = instruction.get(Argument.NUMBER_NOT_LESS_THAN_ONE);
        return new CraftingObjective(instruction, targetAmount, item);
    }
}
