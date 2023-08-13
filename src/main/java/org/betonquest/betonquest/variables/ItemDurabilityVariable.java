package org.betonquest.betonquest.variables;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Variable;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.jetbrains.annotations.NotNull;

/**
 * Returns the durability of a vanilla item
 */
public class ItemDurabilityVariable extends Variable {
    /**
     * The default amount of digits after comma
     */
    private static final int DEFAULT_DIGITS = 2;

    /**
     * The slot of the item
     */
    private final EquipmentSlot slot;

    /**
     * If the durability should be displayed relative to maximum,
     * where 1 is max
     */
    private final boolean relative;

    /**
     * The amount of digits displayed after comma
     */
    private final int digitsAfter;

    /**
     * If the output should be multiplied with 100 and with a '%' in the end
     */
    private final boolean inPercent;

    /**
     * Creates a new item durability variable
     */
    public ItemDurabilityVariable(@NotNull final Instruction instruction) throws InstructionParseException {
        super(instruction);
        this.slot = instruction.getEnum(EquipmentSlot.class);
        this.relative = instruction.hasArgument("relative");
        this.digitsAfter = digits(instruction);
        this.inPercent = instruction.hasArgument("percent");
    }

    private int digits(@NotNull final Instruction instruction) throws InstructionParseException {
        if (instruction.hasArgument("digits")) {
            for (int i = instruction.size() - 2; i >= 0; i--) {
                final String part = instruction.getPart(i);
                if ("digits".equalsIgnoreCase(part)) {
                    return instruction.getInt(instruction.getPart(i + 1), DEFAULT_DIGITS);
                }
            }
        }
        return DEFAULT_DIGITS;
    }

    @Override
    public String getValue(@NotNull final Profile profile) {
        final Player player = profile.getOnlineProfile().get().getPlayer();
        final ItemStack itemStack = player.getEquipment().getItem(slot);
        final int maxDurability = itemStack.getType().getMaxDurability();
        if (!(itemStack.getItemMeta() instanceof final Damageable damageable)) {
            return String.valueOf(maxDurability);
        }
        final int durability = maxDurability - damageable.getDamage();
        if (relative && maxDurability != 0) {
            String format = "%." + digitsAfter + 'f';
            double value = (double) durability / (double) maxDurability;
            if (inPercent) {
                format += "%%";
                value *= 100;
            }
            return String.format(player.locale(), format, value);
        }
        return String.valueOf(durability);
    }
}
