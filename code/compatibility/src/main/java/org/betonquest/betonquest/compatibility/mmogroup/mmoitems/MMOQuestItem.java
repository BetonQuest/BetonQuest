package org.betonquest.betonquest.compatibility.mmogroup.mmoitems;

import net.Indyuce.mmoitems.api.Type;
import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.item.QuestItem;
import org.betonquest.betonquest.api.profile.Profile;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

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
     * UUID the item is soulbound to.
     */
    @Nullable
    private final UUID createdFor;

    /**
     * Create a new MMO Item.
     *
     * @param resolvedItem the already resolved item stack
     * @param itemType     the item type
     * @param itemID       the item id
     * @param createdFor   the soulbound uuid to match item with
     */
    public MMOQuestItem(final ItemStack resolvedItem, final Type itemType, final String itemID, @Nullable final UUID createdFor) {
        this.resolvedItem = resolvedItem;
        this.itemType = itemType;
        this.itemID = itemID;
        this.createdFor = createdFor;
    }

    @Override
    public Component getName() {
        final ItemMeta itemMeta = resolvedItem.getItemMeta();
        return itemMeta.hasDisplayName() ? itemMeta.displayName() : Component.text(itemID);
    }

    @Override
    public List<Component> getLore() {
        final ItemMeta itemMeta = resolvedItem.getItemMeta();
        return itemMeta.hasLore() ? itemMeta.lore() : List.of();
    }

    @Override
    public ItemStack generate(final int stackSize, @Nullable final Profile profile) {
        return resolvedItem.clone();
    }

    @Override
    public boolean matches(@Nullable final ItemStack item) {
        return MMOItemsUtils.equalsMMOItem(item, itemType, itemID, createdFor);
    }
}
