package org.betonquest.betonquest.compatibility.mythicmobs.item;

import io.lumine.mythic.core.items.ItemExecutor;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.item.QuestItemSerializer;
import org.bukkit.inventory.ItemStack;

/**
 * Serializes MythicItems to its id.
 */
public class MythicQuestItemSerializer implements QuestItemSerializer {
    /**
     * Manager to get mythic type from stacks.
     */
    private final ItemExecutor itemManager;

    /**
     * Create a new Serializer.
     *
     * @param itemManager the manager to get mythic type from stacks
     */
    public MythicQuestItemSerializer(final ItemExecutor itemManager) {
        this.itemManager = itemManager;
    }

    @Override
    public String serialize(final ItemStack itemStack) throws QuestException {
        final String mythicTypeFromItem = itemManager.getMythicTypeFromItem(itemStack);
        if (mythicTypeFromItem == null) {
            throw new QuestException("Item is not a Mythic Item!");
        }
        return mythicTypeFromItem;
    }
}
