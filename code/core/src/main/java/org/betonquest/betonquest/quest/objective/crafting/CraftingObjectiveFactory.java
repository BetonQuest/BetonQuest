package org.betonquest.betonquest.quest.objective.crafting;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.type.QuestItemWrapper;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;

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
        final Variable<QuestItemWrapper> item = instruction.item().get();
        final Variable<Number> targetAmount = instruction.number().atLeast(1).get();
        return new CraftingObjective(instruction, targetAmount, item);
    }
}
