package org.betonquest.betonquest.quest.variable.item;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
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
    private final Argument<EquipmentSlot> slot;

    /**
     * If the durability should be displayed relative to maximum.
     * Maximum is 1.
     */
    private final FlagArgument<Boolean> relative;

    /**
     * The amount of digits displayed after comma.
     */
    private final Argument<Number> digitsAfter;

    /**
     * If the output should be multiplied with 100 and with a '%' in the end.
     */
    private final FlagArgument<Boolean> inPercent;

    /**
     * Creates a new item durability variable.
     *
     * @param slot        the slot of the item
     * @param relative    if the durability should be displayed relative to maximum
     * @param digitsAfter the amount of digits displayed after comma
     * @param inPercent   if the output should be multiplied with 100 and with a '%' in the end
     */
    public ItemDurabilityVariable(final Argument<EquipmentSlot> slot, final FlagArgument<Boolean> relative,
                                  final Argument<Number> digitsAfter, final FlagArgument<Boolean> inPercent) {
        this.slot = slot;
        this.relative = relative;
        this.digitsAfter = digitsAfter;
        this.inPercent = inPercent;
    }

    @Override
    public String getValue(final OnlineProfile profile) throws QuestException {
        final ItemStack itemStack = profile.getPlayer().getEquipment().getItem(slot.getValue(profile));
        final int maxDurability = itemStack.getType().getMaxDurability();
        if (!(itemStack.getItemMeta() instanceof final Damageable damageable)) {
            return String.valueOf(maxDurability);
        }
        final int durability = maxDurability - damageable.getDamage();
        if (relative.getValue(profile).orElse(false) && maxDurability != 0) {
            String format = "%." + digitsAfter.getValue(profile).intValue() + 'f';
            double value = (double) durability / maxDurability;
            if (inPercent.getValue(profile).orElse(false)) {
                format += "%%";
                value *= 100;
            }
            return String.format(format, value);
        }
        return String.valueOf(durability);
    }
}
