package org.betonquest.betonquest.compatibility.mmogroup.mmoitems;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.lumine.mythic.lib.api.item.NBTItem;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import org.betonquest.betonquest.api.QuestException;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * A utility class for working with MMOItems.
 */
public final class MMOItemsUtils {

    /**
     * Utility classes shouldn't be instantiated.
     */
    private MMOItemsUtils() {
    }

    /**
     * Compares an ItemStack and an MMOItem based on the MMOItem's type and ID.
     *
     * @param item       any ItemStack
     * @param type       the type of the MMOItem that shall be checked for
     * @param itemID     the item of the MMOItem that shall be checked for
     * @param createdFor the soulbound uuid to match, if present
     * @return whether the input item matches the defined MMOItems
     */
    public static boolean equalsMMOItem(@Nullable final ItemStack item, final Type type, final String itemID,
                                        @Nullable final UUID createdFor) {
        if (item == null) {
            return false;
        }
        final NBTItem realItemNBT = NBTItem.get(item);
        final String realItemType = realItemNBT.getString("MMOITEMS_ITEM_TYPE");
        final String realItemID = realItemNBT.getString("MMOITEMS_ITEM_ID");
        if (!realItemID.equalsIgnoreCase(itemID) || !realItemType.equalsIgnoreCase(type.getId())) {
            return false;
        }
        if (createdFor == null) {
            return true;
        }
        final String soulbound = realItemNBT.hasTag("MMOITEMS_SOULBOUND") ? realItemNBT.getString("MMOITEMS_SOULBOUND") : null;
        if (soulbound == null) {
            return false;
        }
        final JsonObject json = JsonParser.parseString(soulbound).getAsJsonObject();
        final String ownerUuid = json.get("UUID").getAsString();
        return ownerUuid.equals(createdFor.toString());
    }

    /**
     * Gets a mmo item type by string or throws.
     *
     * @param itemType to parse
     * @return the item type
     * @throws QuestException if no item type with that id is present
     */
    public static Type getMMOItemType(final String itemType) throws QuestException {
        final Type type = MMOItems.plugin.getTypes().get(itemType);
        if (type == null) {
            throw new QuestException("The item type '%s' does not exist!".formatted(itemType));
        }
        return type;
    }
}
