package org.betonquest.betonquest.compatibility.mmogroup.mmoitems.objective;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.instruction.Instruction;
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
        final String itemType = instruction.next();
        final String itemID = instruction.next();
        final String gemID = instruction.next();
        return new MMOItemsApplyGemObjective(instruction, itemType, itemID, gemID);
    }
}
