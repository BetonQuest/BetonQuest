package org.betonquest.betonquest.quest.objective.enchant;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.DefaultInstruction;
import org.betonquest.betonquest.api.instruction.Item;
import org.betonquest.betonquest.api.instruction.argument.Argument;
import org.betonquest.betonquest.api.instruction.argument.InstructionIdentifierArgument;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.instruction.variable.VariableList;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;

import java.util.List;

/**
 * Factory for creating {@link EnchantObjective} instances from {@link DefaultInstruction}s.
 */
public class EnchantObjectiveFactory implements ObjectiveFactory {

    /**
     * The one keyword for the requirement mode.
     */
    private static final String JUST_ONE_ENCHANT = "one";

    /**
     * Creates a new instance of the EnchantObjectiveFactory.
     */
    public EnchantObjectiveFactory() {
    }

    @Override
    public Objective parseInstruction(final DefaultInstruction instruction) throws QuestException {
        final Variable<Number> targetAmount = instruction.getValue("amount", Argument.NUMBER_NOT_LESS_THAN_ONE, 1);
        final Variable<Item> item = instruction.get(InstructionIdentifierArgument.ITEM);
        final Variable<List<EnchantObjective.EnchantmentData>> desiredEnchantments =
                instruction.getList(EnchantObjective.EnchantmentData::convert, VariableList.notEmptyChecker());
        final boolean requireOne = JUST_ONE_ENCHANT.equalsIgnoreCase(instruction.getValue("requirementMode"));
        return new EnchantObjective(instruction, targetAmount, item, desiredEnchantments, requireOne);
    }
}
