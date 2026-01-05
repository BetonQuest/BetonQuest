package org.betonquest.betonquest.compatibility.itemsadder.item;

import dev.lone.itemsadder.api.CustomStack;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.item.QuestItemSerializer;
import org.bukkit.inventory.ItemStack;

public class ItemsAdderQuestItemSerializer implements QuestItemSerializer {

    @Override
    public String serialize(final ItemStack itemStack) throws QuestException {
        final CustomStack itemsAdderItem = CustomStack.byItemStack(itemStack);
        if (itemsAdderItem == null) throw new QuestException("Item is not a ItemsAdder Item!");
        return itemsAdderItem.getNamespacedID();
    }
}
