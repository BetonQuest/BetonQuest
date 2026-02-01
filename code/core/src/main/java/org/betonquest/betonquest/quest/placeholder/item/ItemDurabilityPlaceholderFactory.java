package org.betonquest.betonquest.quest.placeholder.item;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.placeholder.OnlinePlaceholderAdapter;
import org.betonquest.betonquest.api.quest.placeholder.PlayerPlaceholder;
import org.betonquest.betonquest.api.quest.placeholder.PlayerPlaceholderFactory;
import org.bukkit.inventory.EquipmentSlot;

/**
 * Factory to create item durability placeholders from {@link Instruction}s.
 */
public class ItemDurabilityPlaceholderFactory implements PlayerPlaceholderFactory {

    /**
     * The key for digits.
     */
    private static final String DIGITS_KEY = "digits";

    /**
     * The default number of digits after comma.
     */
    private static final int DEFAULT_DIGITS = 2;

    /**
     * Create the item durability placeholder factory.
     */
    public ItemDurabilityPlaceholderFactory() {
    }

    @Override
    public PlayerPlaceholder parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<EquipmentSlot> slot = instruction.enumeration(EquipmentSlot.class).get();
        final FlagArgument<Boolean> relative = instruction.bool().getFlag("relative", true);
        final Argument<Number> digitsAfter = instruction.number().get(DIGITS_KEY, DEFAULT_DIGITS);
        final FlagArgument<Boolean> inPercent = instruction.bool().getFlag("percent", true);
        return new OnlinePlaceholderAdapter(new ItemDurabilityPlaceholder(slot, relative, digitsAfter, inPercent));
    }
}
