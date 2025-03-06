package org.betonquest.betonquest.quest.objective.enchant;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.Item;
import org.betonquest.betonquest.instruction.argument.VariableArgument;
import org.betonquest.betonquest.instruction.variable.VariableNumber;

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
     * Logger factory to create a logger for the objectives.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Creates a new instance of the EnchantObjectiveFactory.
     *
     * @param loggerFactory the logger factory to create a logger for the objectives
     */
    public EnchantObjectiveFactory(final BetonQuestLoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    @Override
    public Objective parseInstruction(final Instruction instruction) throws QuestException {
        final VariableNumber targetAmount = instruction.get(instruction.getOptional("amount", "1"), VariableArgument.NUMBER_NOT_LESS_THAN_ONE);
        final Item item = instruction.getItem();
        final List<EnchantObjective.EnchantmentData> desiredEnchantments = instruction.getList(EnchantObjective.EnchantmentData::convert);
        if (desiredEnchantments.isEmpty()) {
            throw new QuestException("No enchantments were given! You must specify at least one enchantment.");
        }
        final boolean requireOne = instruction.getOptionalArgument("requirementMode").map(JUST_ONE_ENCHANT::equalsIgnoreCase).orElse(false);
        final BetonQuestLogger log = loggerFactory.create(EnchantObjective.class);
        return new EnchantObjective(instruction, targetAmount, log, item, desiredEnchantments, requireOne);
    }
}
