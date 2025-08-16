package org.betonquest.betonquest.compatibility.mmogroup.mmoitems.objective;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;

/**
 * Factory for creating {@link MMOItemsUpgradeObjective} instances from {@link Instruction}s.
 */
public class MMOItemsUpgradeObjectiveFactory implements ObjectiveFactory {
    /**
     * Creates a new instance of the MMOItemsUpgradeObjectiveFactory.
     */
    public MMOItemsUpgradeObjectiveFactory() {
    }

    @Override
    public Objective parseInstruction(final Instruction instruction) throws QuestException {
        final String itemType = instruction.next();
        final String itemID = instruction.next();
        return new MMOItemsUpgradeObjective(instruction, itemType, itemID);
    }
}
