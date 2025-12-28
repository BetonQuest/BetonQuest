package org.betonquest.betonquest.quest.variable.item;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.variable.PlayerVariable;
import org.betonquest.betonquest.api.quest.variable.PlayerVariableFactory;
import org.betonquest.betonquest.api.quest.variable.online.OnlineVariableAdapter;
import org.bukkit.inventory.EquipmentSlot;

/**
 * Factory to create item durability variables from {@link Instruction}s.
 */
public class ItemDurabilityVariableFactory implements PlayerVariableFactory {

    /**
     * The key for digits.
     */
    private static final String DIGITS_KEY = "digits";

    /**
     * The default number of digits after comma.
     */
    private static final int DEFAULT_DIGITS = 2;

    /**
     * Create the item durability variable factory.
     */
    public ItemDurabilityVariableFactory() {
    }

    @Override
    public PlayerVariable parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<EquipmentSlot> slot = instruction.enumeration(EquipmentSlot.class).get();
        final FlagArgument<Boolean> relative = instruction.bool().getFlag("relative", true);
        final Argument<Number> digitsAfter = instruction.number().get(DIGITS_KEY, DEFAULT_DIGITS);
        final FlagArgument<Boolean> inPercent = instruction.bool().getFlag("percent", true);
        return new OnlineVariableAdapter(new ItemDurabilityVariable(slot, relative, digitsAfter, inPercent));
    }
}
