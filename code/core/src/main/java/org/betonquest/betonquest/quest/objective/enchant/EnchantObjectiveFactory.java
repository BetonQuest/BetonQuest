package org.betonquest.betonquest.quest.objective.enchant;

import org.betonquest.betonquest.api.DefaultObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.type.ItemWrapper;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;

import java.util.List;

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
    public DefaultObjective parseInstruction(final Instruction instruction) throws QuestException {
        final Argument<Number> targetAmount = instruction.number().atLeast(1).get("amount", 1);
        final Argument<ItemWrapper> item = instruction.item().get();
        final Argument<List<EnchantObjective.EnchantmentData>> desiredEnchantments =
                instruction.parse(EnchantObjective.EnchantmentData::convert).list().notEmpty().get();
        final boolean requireOne = instruction.parse(JUST_ONE_ENCHANT::equalsIgnoreCase)
                .get("requirementMode", false).getValue(null);
        return new EnchantObjective(instruction, targetAmount, item, desiredEnchantments, requireOne);
    }
}
