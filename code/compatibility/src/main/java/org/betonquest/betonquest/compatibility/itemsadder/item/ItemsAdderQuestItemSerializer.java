package org.betonquest.betonquest.compatibility.itemsadder.item;

import dev.lone.itemsadder.api.CustomStack;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.item.QuestItemSerializer;
import org.bukkit.inventory.ItemStack;

/**
 * Serializes {@link ItemStack}s to their ItemsAdder namespaced IDs.
 */
public class ItemsAdderQuestItemSerializer implements QuestItemSerializer {

    /**
     * The empty default constructor.
     */
    public ItemsAdderQuestItemSerializer() {
        // Empty
    }

    @Override
    public String serialize(final ItemStack itemStack) throws QuestException {
        final CustomStack customStack = CustomStack.byItemStack(itemStack);
        if (customStack == null) {
            throw new QuestException("Item is not a ItemsAdder Item!");
        }
        return customStack.getNamespacedID();
    }
}
