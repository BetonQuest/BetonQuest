package org.betonquest.betonquest.quest.action.equip;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.type.ItemWrapper;
import org.betonquest.betonquest.api.quest.action.OnlineActionAdapter;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.bukkit.inventory.EquipmentSlot;

/**
 * Factory for {@link EquipAction}.
 */
public class EquipActionFactory implements PlayerActionFactory {

    /**
     * Creates the equip action factory.
     */
    public EquipActionFactory() {
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<ItemWrapper> item = instruction.item().get();
        final Argument<EquipmentSlot> slot = instruction.enumeration(EquipmentSlot.class)
                .validate(EquipActionFactory::isPlayerSlot, "Invalid player equipment slot: '%s'")
                .get();
        final FlagArgument<Boolean> drop = instruction.bool().getFlag("drop", true);
        final FlagArgument<Boolean> force = instruction.bool().getFlag("force", true);
        return new OnlineActionAdapter(new EquipAction(item, slot, drop, force));
    }

    private static boolean isPlayerSlot(final EquipmentSlot slot) {
        return slot == EquipmentSlot.HAND || slot == EquipmentSlot.OFF_HAND
                || slot == EquipmentSlot.FEET || slot == EquipmentSlot.LEGS
                || slot == EquipmentSlot.CHEST || slot == EquipmentSlot.HEAD;
    }
}
