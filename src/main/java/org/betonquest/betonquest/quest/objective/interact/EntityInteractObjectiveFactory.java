package org.betonquest.betonquest.quest.objective.interact;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.argument.Argument;
import org.betonquest.betonquest.instruction.argument.PackageArgument;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.bukkit.Location;
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
     * Creates a new instance of the EntityInteractObjectiveFactory.
     */
    public EntityInteractObjectiveFactory() {
    }

    @Override
    public Objective parseInstruction(final Instruction instruction) throws QuestException {
        final Variable<Interaction> interaction = instruction.getVariable(Argument.ENUM(Interaction.class));
        final Variable<EntityType> mobType = instruction.getVariable(Argument.ENUM(EntityType.class));
        final Variable<Number> targetAmount = instruction.getVariable(Argument.NUMBER_NOT_LESS_THAN_ONE);
        final String customName = instruction.getOptional("name");
        final String realName = instruction.getOptional("realname");
        final Variable<String> marked = instruction.get(instruction.getOptional("marked"), PackageArgument.IDENTIFIER);
        final boolean cancel = instruction.hasArgument("cancel");
        final Variable<Location> loc = instruction.getVariable(instruction.getOptional("loc"), Argument.LOCATION);
        final String stringRange = instruction.getOptional("range");
        final Variable<Number> range = stringRange == null ? new Variable<>(1) : instruction.getVariable(stringRange, Argument.NUMBER);
        final EquipmentSlot slot = getEquipmentSlot(instruction);
        return new EntityInteractObjective(instruction, targetAmount, loc, range, customName, realName, slot, mobType, marked, interaction, cancel);
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
