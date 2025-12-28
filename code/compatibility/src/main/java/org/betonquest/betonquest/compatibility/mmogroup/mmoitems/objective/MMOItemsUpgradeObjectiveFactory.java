package org.betonquest.betonquest.compatibility.mmogroup.mmoitems.objective;

import org.betonquest.betonquest.api.DefaultObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
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
    public DefaultObjective parseInstruction(final Instruction instruction) throws QuestException {
        final Argument<String> itemType = instruction.string().get();
        final Argument<String> itemID = instruction.string().get();
        return new MMOItemsUpgradeObjective(instruction, itemType, itemID);
    }
}
