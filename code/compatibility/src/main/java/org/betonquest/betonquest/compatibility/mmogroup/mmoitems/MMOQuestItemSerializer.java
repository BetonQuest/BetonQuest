package org.betonquest.betonquest.compatibility.mmogroup.mmoitems;

import io.lumine.mythic.lib.api.item.NBTItem;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.item.QuestItemSerializer;
import org.bukkit.inventory.ItemStack;

/**
 * Serializes MMOItems to its type and id.
 */
public class MMOQuestItemSerializer implements QuestItemSerializer {

    /**
     * The empty default constructor.
     */
    public MMOQuestItemSerializer() {
    }

    @Override
    public String serialize(final ItemStack itemStack) throws QuestException {
        final NBTItem realItemNBT = NBTItem.get(itemStack);
        final String realItemType = realItemNBT.getString("MMOITEMS_ITEM_TYPE");
        if (realItemType == null) {
            throw new QuestException("Item does not have MMOITEMS_ITEM_TYPE!");
        }
        final String realItemID = realItemNBT.getString("MMOITEMS_ITEM_ID");
        if (realItemID == null) {
            throw new QuestException("Item does not have MMOITEMS_ITEM_ID NBT data!");
        }
        return realItemType + " " + realItemID;
    }
}
