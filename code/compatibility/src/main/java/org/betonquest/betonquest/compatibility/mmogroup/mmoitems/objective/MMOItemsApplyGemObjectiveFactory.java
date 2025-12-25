package org.betonquest.betonquest.compatibility.mmogroup.mmoitems.objective;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
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
        final Argument<String> itemType = instruction.string().get();
        final Argument<String> itemID = instruction.string().get();
        final Argument<String> gemID = instruction.string().get();
        return new MMOItemsApplyGemObjective(instruction, itemType, itemID, gemID);
    }
}
