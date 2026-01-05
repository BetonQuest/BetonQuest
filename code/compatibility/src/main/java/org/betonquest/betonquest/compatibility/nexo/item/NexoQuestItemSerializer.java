package org.betonquest.betonquest.compatibility.nexo.item;

import com.nexomc.nexo.api.NexoItems;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.item.QuestItemSerializer;
import org.bukkit.inventory.ItemStack;

/**
 * Serializes {@link ItemStack}s to their Nexo IDs.
 */
public class NexoQuestItemSerializer implements QuestItemSerializer {

    /**
     * Default constructor for NexoQuestItemSerializer.
     */
    public NexoQuestItemSerializer() {
    }

    /**
     * Serializes an item stack to its Nexo ID.
     *
     * @param itemStack the item to serialize
     * @return the Nexo item ID
     * @throws QuestException if the item is not a valid Nexo item
     */
    @Override
    public String serialize(final ItemStack itemStack) throws QuestException {
        final String nexoItem = NexoItems.idFromItem(itemStack);
        if (nexoItem == null) throw new QuestException("Item is not a Nexo Item!");
        return nexoItem;
    }
}
