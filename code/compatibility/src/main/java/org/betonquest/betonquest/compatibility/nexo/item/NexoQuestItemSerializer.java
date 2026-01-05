package org.betonquest.betonquest.compatibility.nexo.item;

import com.nexomc.nexo.api.NexoItems;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.item.QuestItemSerializer;
import org.bukkit.inventory.ItemStack;

public class NexoQuestItemSerializer implements QuestItemSerializer {

    @Override
    public String serialize(final ItemStack itemStack) throws QuestException {
        final String nexoItem = NexoItems.idFromItem(itemStack);
        if (nexoItem == null) throw new QuestException("Item is not a Nexo Item!");
        return nexoItem;
    }
}
