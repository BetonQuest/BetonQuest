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

import java.util.EnumSet;
import java.util.Set;

/**
 * Factory for {@link EquipAction}.
 */
public class EquipActionFactory implements PlayerActionFactory {

    /**
     * Equipment slots available to players.
     */
    private static final Set<EquipmentSlot> PLAYER_SLOTS = EnumSet.of(EquipmentSlot.HAND, EquipmentSlot.OFF_HAND,
            EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD);

    /**
     * Creates the equip action factory.
     */
    public EquipActionFactory() {
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<ItemWrapper> item = instruction.item().get();
        final Argument<EquipmentSlot> slot = instruction.enumeration(EquipmentSlot.class)
                .validate(PLAYER_SLOTS::contains, "Invalid player equipment slot: '%s'")
                .get();
        final FlagArgument<Boolean> drop = instruction.bool().getFlag("drop", true);
        final FlagArgument<Boolean> force = instruction.bool().getFlag("force", true);
        return new OnlineActionAdapter(new EquipAction(item, slot, drop, force));
    }
}
