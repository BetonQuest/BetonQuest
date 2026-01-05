package org.betonquest.betonquest.compatibility.craftengine.item;

import net.momirealms.craftengine.bukkit.api.CraftEngineItems;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.item.QuestItemSerializer;
import org.bukkit.inventory.ItemStack;

/**
 * Serializes {@link ItemStack}s to their CraftEngine IDs.
 */
public class CraftEngineQuestItemSerializer implements QuestItemSerializer {

    /**
     * Serializes an item stack to its CraftEngine ID string.
     * @param itemStack the item to serialize
     * @return the CraftEngine item ID
     * @throws QuestException if the item is not a valid CraftEngine item
     */
    @Override
    public String serialize(final ItemStack itemStack) throws QuestException {
        final Object customItemId = CraftEngineItems.getCustomItemId(itemStack);
        if (customItemId == null) throw new QuestException("Item is not a CraftEngine Item!");
        return customItemId.toString();
    }
}
