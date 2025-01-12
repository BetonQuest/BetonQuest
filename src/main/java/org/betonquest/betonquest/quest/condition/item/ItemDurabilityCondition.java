package org.betonquest.betonquest.quest.condition.item;

import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.quest.condition.online.OnlineCondition;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
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
    private final EquipmentSlot slot;

    /**
     * The durability needed.
     */
    private final VariableNumber amount;

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
    public ItemDurabilityCondition(final EquipmentSlot slot, final VariableNumber amount, final boolean relative) {
        this.slot = slot;
        this.amount = amount;
        this.relative = relative;
    }

    @Override
    public boolean check(final OnlineProfile profile) throws QuestRuntimeException {
        final ItemStack itemStack = profile.getPlayer().getEquipment().getItem(slot);
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
