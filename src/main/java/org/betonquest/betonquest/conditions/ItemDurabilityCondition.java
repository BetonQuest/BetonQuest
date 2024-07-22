package org.betonquest.betonquest.conditions;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.item.QuestItem;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

/**
 * To check durability on the item on a specific slot,
 * in opposite to {@link QuestItem#getDurability()}.
 */
public class ItemDurabilityCondition extends Condition {
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
     * @param instruction the instruction to create the condition from
     * @throws InstructionParseException if there was an error parsing the instruction
     */
    public ItemDurabilityCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, false);
        this.slot = instruction.getEnum(EquipmentSlot.class);
        this.amount = instruction.getVarNum();
        this.relative = instruction.hasArgument("relative");
    }

    @Override
    protected Boolean execute(final Profile profile) throws QuestRuntimeException {
        final ItemStack itemStack = profile.getOnlineProfile().get().getPlayer().getEquipment().getItem(slot);
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
