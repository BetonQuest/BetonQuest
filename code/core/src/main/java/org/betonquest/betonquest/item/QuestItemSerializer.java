package org.betonquest.betonquest.item;

import org.betonquest.betonquest.api.quest.QuestException;
import org.bukkit.inventory.ItemStack;

/**
 * Allows to serialize ItemStacks as string format parsed by a
 * {@link org.betonquest.betonquest.kernel.registry.TypeFactory} to {@link QuestItem}.
 */
@FunctionalInterface
public interface QuestItemSerializer {

    /**
     * Converts the given ItemStack into string format, which can be later parsed as QuestItem.
     *
     * @param itemStack the item to serialize
     * @return the string which results as the ItemStack when used as instruction for a {@link QuestItem}
     * @throws QuestException when the item stack cannot be serialized
     */
    String serialize(ItemStack itemStack) throws QuestException;
}
