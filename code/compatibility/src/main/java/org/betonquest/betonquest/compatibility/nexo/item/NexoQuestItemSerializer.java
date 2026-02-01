package org.betonquest.betonquest.compatibility.nexo.item;

import com.nexomc.nexo.api.NexoItems;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.item.QuestItemSerializer;
import org.bukkit.inventory.ItemStack;

/**
 * Serializes {@link ItemStack}s to their Nexo IDs.
 */
public class NexoQuestItemSerializer implements QuestItemSerializer {

    /**
     * The empty default constructor.
     */
    public NexoQuestItemSerializer() {
        // Empty
    }

    @Override
    public String serialize(final ItemStack itemStack) throws QuestException {
        final String itemBuilder = NexoItems.idFromItem(itemStack);
        if (itemBuilder == null) {
            throw new QuestException("Item is not a Nexo Item!");
        }
        return itemBuilder;
    }
}
