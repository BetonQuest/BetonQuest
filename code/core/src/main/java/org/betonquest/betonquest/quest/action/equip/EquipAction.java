package org.betonquest.betonquest.quest.action.equip;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.type.ItemWrapper;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.action.OnlineAction;
import org.betonquest.betonquest.util.InventoryUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

/**
 * Equips an item in a player's equipment slot.
 */
public class EquipAction implements OnlineAction {

    /**
     * The item to equip.
     */
    private final Argument<ItemWrapper> item;

    /**
     * The equipment slot to target.
     */
    private final Argument<EquipmentSlot> slot;

    /**
     * Whether to drop an item that cannot remain in the slot.
     */
    private final FlagArgument<Boolean> drop;

    /**
     * Whether to replace an item already in the slot.
     */
    private final FlagArgument<Boolean> force;

    /**
     * Creates the equip action.
     *
     * @param item  the item to equip
     * @param slot  the equipment slot to target
     * @param drop  whether to drop an item that cannot remain in the slot
     * @param force whether to replace an item already in the slot
     */
    public EquipAction(final Argument<ItemWrapper> item, final Argument<EquipmentSlot> slot,
                       final FlagArgument<Boolean> drop, final FlagArgument<Boolean> force) {
        this.item = item;
        this.slot = slot;
        this.drop = drop;
        this.force = force;
    }

    @Override
    public void execute(final OnlineProfile profile) throws QuestException {
        final Player player = profile.getPlayer();
        final EntityEquipment equipment = player.getEquipment();
        final EquipmentSlot resolvedSlot = slot.getValue(profile);
        final ItemStack equippedItem = equipment.getItem(resolvedSlot);
        final boolean shouldDrop = drop.getValue(profile).orElse(false);
        final boolean shouldForce = force.getValue(profile).orElse(false);
        final ItemStack newItem = item.getValue(profile).generate(profile);

        if (InventoryUtils.isEmptySlot(equippedItem)) {
            equipment.setItem(resolvedSlot, newItem);
            return;
        }

        if (!shouldForce) {
            if (shouldDrop) {
                dropItem(player, newItem);
            }
            return;
        }

        if (shouldDrop) {
            dropItem(player, equippedItem);
        }
        equipment.setItem(resolvedSlot, newItem);
    }

    private void dropItem(final Player player, final ItemStack item) {
        final Location location = player.getLocation();
        location.getWorld().dropItem(location, item);
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
