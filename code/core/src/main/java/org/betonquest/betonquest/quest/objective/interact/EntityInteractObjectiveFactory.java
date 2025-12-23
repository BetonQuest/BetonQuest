package org.betonquest.betonquest.quest.objective.interact;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
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
        final Variable<Interaction> interaction = instruction.enumeration(Interaction.class).get();
        final Variable<EntityType> mobType = instruction.enumeration(EntityType.class).get();
        final Variable<Number> targetAmount = instruction.number().atLeast(1).get();
        final Variable<Component> customName = instruction.component().get("name").orElse(null);
        final Variable<String> realName = instruction.string().get("realname").orElse(null);
        final Variable<String> marked = instruction.packageIdentifier().get("marked").orElse(null);
        final boolean cancel = instruction.hasArgument("cancel");
        final Variable<Location> loc = instruction.location().get("loc").orElse(null);
        final Variable<Number> range = instruction.number().get("range", 1);
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
