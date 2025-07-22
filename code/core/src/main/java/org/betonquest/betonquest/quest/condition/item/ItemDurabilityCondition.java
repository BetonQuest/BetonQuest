package org.betonquest.betonquest.quest.condition.item;

import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.online.OnlineCondition;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

/**
 * To check durability on the item on a specific slot.
 */
public class ItemDurabilityCondition implements OnlineCondition {

    /**
     * The slot to check.
     */
    private final Variable<EquipmentSlot> slot;

    /**
     * The durability needed.
     */
    private final Variable<Number> amount;

    /**
     * If the durability should be handled as value from 0 to 1.
     */
    private final boolean relative;

    /**
     * Creates an item durability condition.
     *
     * @param slot     the slot to check
     * @param amount   the durability needed
     * @param relative if the durability should be handled as value from 0 to 1
     */
    public ItemDurabilityCondition(final Variable<EquipmentSlot> slot, final Variable<Number> amount, final boolean relative) {
        this.slot = slot;
        this.amount = amount;
        this.relative = relative;
    }

    @Override
    public boolean check(final OnlineProfile profile) throws QuestException {
        final ItemStack itemStack = profile.getPlayer().getEquipment().getItem(slot.getValue(profile));
        if (itemStack.getType().isAir() || !(itemStack.getItemMeta() instanceof final Damageable damageable)) {
            return false;
        }
        final int maxDurability = itemStack.getType().getMaxDurability();
        if (maxDurability == 0) {
            return false;
        }
        final int actualDurability = maxDurability - damageable.getDamage();
        final double requiredAmount = amount.getValue(profile).doubleValue();
        if (relative) {
            final double relativeValue = (double) actualDurability / maxDurability;
            return relativeValue >= requiredAmount;
        } else {
            return actualDurability >= requiredAmount;
        }
    }
}
