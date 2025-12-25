package org.betonquest.betonquest.quest.objective.interact;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

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
        final Argument<Interaction> interaction = instruction.enumeration(Interaction.class).get();
        final Argument<EntityType> mobType = instruction.enumeration(EntityType.class).get();
        final Argument<Number> targetAmount = instruction.number().atLeast(1).get();
        final Argument<Component> customName = instruction.component().get("name").orElse(null);
        final Argument<String> realName = instruction.string().get("realname").orElse(null);
        final Argument<String> marked = instruction.packageIdentifier().get("marked").orElse(null);
        final boolean cancel = instruction.hasArgument("cancel");
        final Argument<Location> loc = instruction.location().get("loc").orElse(null);
        final Argument<Number> range = instruction.number().get("range", 1);
        final EquipmentSlot slot = getEquipmentSlot(instruction);
        return new EntityInteractObjective(instruction, targetAmount, loc, range, customName, realName, slot, mobType, marked, interaction, cancel);
    }

    @Nullable
    private EquipmentSlot getEquipmentSlot(final Instruction instruction) throws QuestException {
        final Argument<Optional<EquipmentSlot>> hand = instruction.enumeration(EquipmentSlot.class)
                .validate(slot -> slot == EquipmentSlot.HAND || slot == EquipmentSlot.OFF_HAND, "Invalid hand value: '%s'")
                .prefilterOptional(ANY, null)
                .get("hand").orElse(null);
        return hand == null ? null : hand.getValue(null).orElse(null);
    }
}
