package org.betonquest.betonquest.quest.variable.item;

import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.variable.online.OnlineVariable;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

/**
 * Returns the durability of a vanilla item.
 */
public class ItemDurabilityVariable implements OnlineVariable {

    /**
     * The slot of the item.
     */
    private final EquipmentSlot slot;

    /**
     * If the durability should be displayed relative to maximum.
     * Maximum is 1.
     */
    private final boolean relative;

    /**
     * The amount of digits displayed after comma.
     */
    private final int digitsAfter;

    /**
     * If the output should be multiplied with 100 and with a '%' in the end.
     */
    private final boolean inPercent;

    /**
     * Creates a new item durability variable.
     *
     * @param slot        the slot of the item
     * @param relative    if the durability should be displayed relative to maximum
     * @param digitsAfter the amount of digits displayed after comma
     * @param inPercent   if the output should be multiplied with 100 and with a '%' in the end
     */
    public ItemDurabilityVariable(final EquipmentSlot slot, final boolean relative, final int digitsAfter, final boolean inPercent) {
        this.slot = slot;
        this.relative = relative;
        this.digitsAfter = digitsAfter;
        this.inPercent = inPercent;
    }

    @Override
    public String getValue(final OnlineProfile profile) throws QuestException {
        final ItemStack itemStack = profile.getPlayer().getEquipment().getItem(slot);
        final int maxDurability = itemStack.getType().getMaxDurability();
        if (!(itemStack.getItemMeta() instanceof final Damageable damageable)) {
            return String.valueOf(maxDurability);
        }
        final int durability = maxDurability - damageable.getDamage();
        if (relative && maxDurability != 0) {
            String format = "%." + digitsAfter + 'f';
            double value = (double) durability / maxDurability;
            if (inPercent) {
                format += "%%";
                value *= 100;
            }
            return String.format(format, value);
        }
        return String.valueOf(durability);
    }
}
