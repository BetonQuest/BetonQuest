package org.betonquest.betonquest.compatibility.mmogroup.mmoitems;

import net.Indyuce.mmoitems.api.Type;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.item.QuestItem;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Quest Item implementation for MMOItems.
 */
public class MMOQuestItem implements QuestItem {
    /**
     * resolved Item Stack.
     */
    private final ItemStack resolvedItem;

    /**
     * Item Type.
     */
    private final Type itemType;

    /**
     * Item ID.
     */
    private final String itemID;

    /**
     * Create a new MMO Item.
     *
     * @param resolvedItem the already resolved item stack
     * @param itemType     the item type
     * @param itemID       the item id
     */
    public MMOQuestItem(final ItemStack resolvedItem, final Type itemType, final String itemID) {
        this.resolvedItem = resolvedItem;
        this.itemType = itemType;
        this.itemID = itemID;
    }

    @Override
    public String getName() {
        final ItemMeta itemMeta = resolvedItem.getItemMeta();
        return itemMeta.hasDisplayName() ? itemMeta.getDisplayName() : itemID;
    }

    @Override
    public List<String> getLore() {
        final ItemMeta itemMeta = resolvedItem.getItemMeta();
        return itemMeta.hasLore() ? itemMeta.getLore() : List.of();
    }

    @Override
    public ItemStack generate(final int stackSize, @Nullable final Profile profile) {
        return resolvedItem.clone();
    }

    @Override
    public boolean matches(@Nullable final ItemStack item) {
        return MMOItemsUtils.equalsMMOItem(item, itemType, itemID);
    }
}
