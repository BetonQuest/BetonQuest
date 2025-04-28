package org.betonquest.betonquest.compatibility.mmogroup.mmoitems.objective;

import net.Indyuce.mmoitems.api.Type;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.compatibility.mmogroup.mmoitems.MMOItemsUtils;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.argument.Argument;
import org.betonquest.betonquest.instruction.variable.Variable;

/**
 * Factory for creating {@link MMOItemsCraftObjective} instances from {@link Instruction}s.
 */
public class MMOItemsCraftObjectiveFactory implements ObjectiveFactory {
    /**
     * Creates a new instance of the MMOItemsCraftObjectiveFactory.
     */
    public MMOItemsCraftObjectiveFactory() {
    }

    @Override
    public Objective parseInstruction(final Instruction instruction) throws QuestException {
        final Type itemType = MMOItemsUtils.getMMOItemType(instruction.next());
        final String itemId = instruction.next();
        final Variable<Number> targetAmount = instruction.getValue("amount", Argument.NUMBER_NOT_LESS_THAN_ONE, 1);
        return new MMOItemsCraftObjective(instruction, targetAmount, itemType, itemId);
    }
}
