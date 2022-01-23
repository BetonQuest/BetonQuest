package pl.betoncraft.betonquest.compatibility.mmogroup.mmoitems;

import io.lumine.mythic.lib.api.item.NBTItem;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import net.Indyuce.mmoitems.manager.ItemManager;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;

/**
 * A utility class for working with MMOItems.
 */
final public class MMOItemsUtils {

    /**
     * Utility classes shouldn't be instantiated.
     */
    private MMOItemsUtils() {
    }

    /**
     * Compares an ItemStack and an MMOItem based on the MMOItem's type and ID.
     *
     * @param item   any ItemStack.
     * @param type   the type of the MMOItem that shall be checked for
     * @param itemID the itemID of the MMOItem that shall be checked for
     * @return whether the input item matches the defined MMOItems
     */
    public static boolean equalsMMOItem(final ItemStack item, final Type type, final String itemID) {
        if (item == null) {
            return false;
        }
        final NBTItem realItemNBT = NBTItem.get(item);
        final String realItemType = realItemNBT.getString("MMOITEMS_ITEM_TYPE");
        final String realItemID = realItemNBT.getString("MMOITEMS_ITEM_ID");

        return realItemID.equalsIgnoreCase(itemID) && realItemType.equalsIgnoreCase(type.getId());
    }

    /**
     * Get's an ItemStack from an MMOItems item type and MMOItems item ID.
     *
     * @param type      the MMOItems item type
     * @param mmoItemId the MMOItems item ID
     * @return ItemStack constructed by MMOItems
     */
    @SuppressWarnings("deprecation")
    public static ItemStack getMMOItemStack(final Type type, final String mmoItemId) {
        final ItemManager itemManager = MMOItems.plugin.getItems();
        final MMOItem mmoitem = itemManager.getMMOItem(type, mmoItemId.toUpperCase(Locale.ROOT));
        return mmoitem.newBuilder().build();
    }
}
