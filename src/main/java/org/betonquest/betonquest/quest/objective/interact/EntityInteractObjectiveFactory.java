package org.betonquest.betonquest.quest.objective.interact;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.argument.VariableArgument;
import org.betonquest.betonquest.instruction.variable.VariableIdentifier;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.instruction.variable.location.VariableLocation;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.Nullable;

/**
 * Factory for creating {@link EntityInteractObjective} instances from {@link Instruction}s.
 */
public class EntityInteractObjectiveFactory implements ObjectiveFactory {
    /**
     * The key for any hand.
     */
    private static final String ANY = "any";

    /**
     * Logger factory to create a logger for the objectives.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Creates a new instance of the EntityInteractObjectiveFactory.
     *
     * @param loggerFactory the logger factory to create a logger for the objectives
     */
    public EntityInteractObjectiveFactory(final BetonQuestLoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    @Override
    public Objective parseInstruction(final Instruction instruction) throws QuestException {
        final Interaction interaction = instruction.getEnum(Interaction.class);
        final EntityType mobType = instruction.getEnum(EntityType.class);
        final VariableNumber targetAmount = instruction.get(VariableArgument.NUMBER_NOT_LESS_THAN_ONE);
        final String customName = instruction.getOptional("name");
        final String realName = instruction.getOptional("realname");
        final VariableIdentifier marked = instruction.get(instruction.getOptional("marked"), VariableIdentifier::new);
        final boolean cancel = instruction.hasArgument("cancel");
        final VariableLocation loc = instruction.get(instruction.getOptional("loc"), VariableLocation::new);
        final String stringRange = instruction.getOptional("range");
        final VariableNumber range = instruction.get(stringRange == null ? "1" : stringRange, VariableNumber::new);
        final EquipmentSlot slot = getEquipmentSlot(instruction);
        final BetonQuestLogger log = loggerFactory.create(EntityInteractObjective.class);
        return new EntityInteractObjective(instruction, targetAmount, log, loc, range, customName, realName, slot, mobType, marked, interaction, cancel);
    }

    @Nullable
    private EquipmentSlot getEquipmentSlot(final Instruction instruction) throws QuestException {
        final String handString = instruction.getOptional("hand");
        if (handString == null || handString.equalsIgnoreCase(EquipmentSlot.HAND.toString())) {
            return EquipmentSlot.HAND;
        }
        if (handString.equalsIgnoreCase(EquipmentSlot.OFF_HAND.toString())) {
            return EquipmentSlot.OFF_HAND;
        }
        if (ANY.equalsIgnoreCase(handString)) {
            return null;
        }
        throw new QuestException("Invalid hand value: " + handString);
    }
}
