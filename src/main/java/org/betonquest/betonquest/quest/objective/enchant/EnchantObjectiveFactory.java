package org.betonquest.betonquest.quest.objective.enchant;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.Item;
import org.betonquest.betonquest.instruction.argument.Argument;
import org.betonquest.betonquest.instruction.argument.PackageArgument;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.betonquest.betonquest.instruction.variable.VariableList;

/**
 * Factory for creating {@link EnchantObjective} instances from {@link Instruction}s.
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
    public Objective parseInstruction(final Instruction instruction) throws QuestException {
        final Variable<Number> targetAmount = instruction.getVariable(instruction.getOptional("amount", "1"), Argument.NUMBER_NOT_LESS_THAN_ONE);
        final Variable<Item> item = instruction.get(PackageArgument.ITEM);
        final VariableList<EnchantObjective.EnchantmentData> desiredEnchantments = instruction.get(Argument.ofList(EnchantObjective.EnchantmentData::convert, VariableList.notEmptyChecker()));
        final boolean requireOne = instruction.getOptionalArgument("requirementMode").map(JUST_ONE_ENCHANT::equalsIgnoreCase).orElse(false);
        return new EnchantObjective(instruction, targetAmount, item, desiredEnchantments, requireOne);
    }
}
