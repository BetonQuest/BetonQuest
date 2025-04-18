package org.betonquest.betonquest.compatibility.mmogroup.mmoitems.objective;

import net.Indyuce.mmoitems.api.Type;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.compatibility.mmogroup.mmoitems.MMOItemsUtils;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.argument.VariableArgument;
import org.betonquest.betonquest.instruction.variable.VariableNumber;

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
        final VariableNumber targetAmount = instruction.get(instruction.getOptional("amount", "1"), VariableArgument.NUMBER_NOT_LESS_THAN_ONE);
        return new MMOItemsCraftObjective(instruction, targetAmount, itemType, itemId);
    }
}
