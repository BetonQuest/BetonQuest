package org.betonquest.betonquest.quest.objective.interact;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.argument.PackageArgument;
import org.betonquest.betonquest.api.instruction.argument.parser.DefaultArgumentParsers;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
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
        final Variable<Interaction> interaction = instruction.get(DefaultArgumentParsers.forEnumeration(Interaction.class));
        final Variable<EntityType> mobType = instruction.get(DefaultArgumentParsers.forEnumeration(EntityType.class));
        final Variable<Number> targetAmount = instruction.get(DefaultArgumentParsers.NUMBER_NOT_LESS_THAN_ONE);
        final Variable<Component> customName = instruction.getValue("name", DefaultArgumentParsers.MESSAGE);
        final Variable<String> realName = instruction.getValue("realname", DefaultArgumentParsers.STRING);
        final Variable<String> marked = instruction.getValue("marked", PackageArgument.IDENTIFIER);
        final boolean cancel = instruction.hasArgument("cancel");
        final Variable<Location> loc = instruction.getValue("loc", DefaultArgumentParsers.LOCATION);
        final Variable<Number> range = instruction.getValue("range", DefaultArgumentParsers.NUMBER, 1);
        final EquipmentSlot slot = getEquipmentSlot(instruction);
        return new EntityInteractObjective(instruction, targetAmount, loc, range, customName, realName, slot, mobType, marked, interaction, cancel);
    }

    @Nullable
    private EquipmentSlot getEquipmentSlot(final Instruction instruction) throws QuestException {
        final String handString = instruction.getValue("hand");
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
