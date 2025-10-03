package org.betonquest.betonquest.compatibility.mmogroup.mmoitems.objective;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.argument.Argument;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;

/**
 * Factory for creating {@link MMOItemsApplyGemObjective} instances from {@link Instruction}s.
 */
public class MMOItemsApplyGemObjectiveFactory implements ObjectiveFactory {
    /**
     * Creates a new instance of the MMOItemsApplyGemObjectiveFactory.
     */
    public MMOItemsApplyGemObjectiveFactory() {
    }

    @Override
    public Objective parseInstruction(final Instruction instruction) throws QuestException {
        final Variable<String> itemType = instruction.get(Argument.STRING);
        final Variable<String> itemID = instruction.get(Argument.STRING);
        final Variable<String> gemID = instruction.get(Argument.STRING);
        return new MMOItemsApplyGemObjective(instruction, itemType, itemID, gemID);
    }
}
