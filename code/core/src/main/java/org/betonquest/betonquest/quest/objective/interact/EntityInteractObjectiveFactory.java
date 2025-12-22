package org.betonquest.betonquest.quest.objective.interact;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.argument.PackageArgument;
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
        final Variable<Interaction> interaction = instruction.get(instruction.getParsers().forEnum(Interaction.class));
        final Variable<EntityType> mobType = instruction.get(instruction.getParsers().forEnum(EntityType.class));
        final Variable<Number> targetAmount = instruction.get(instruction.getParsers().number().atLeast(1));
        final Variable<Component> customName = instruction.getValue("name", instruction.getParsers().component());
        final Variable<String> realName = instruction.getValue("realname", instruction.getParsers().string());
        final Variable<String> marked = instruction.getValue("marked", PackageArgument.IDENTIFIER);
        final boolean cancel = instruction.hasArgument("cancel");
        final Variable<Location> loc = instruction.getValue("loc", instruction.getParsers().location());
        final Variable<Number> range = instruction.getValue("range", instruction.getParsers().number(), 1);
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
