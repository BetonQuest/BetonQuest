package org.betonquest.betonquest.compatibility.mmogroup.mmoitems.condition;

import io.lumine.mythic.lib.api.item.NBTItem;
import net.Indyuce.mmoitems.api.Type;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.online.OnlineCondition;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/**
 * Condition that checks if a player has a certain amount of an MMOItems item in their hand.
 */
public class MMOItemsHandCondition implements OnlineCondition {

    /**
     * The type of the item to check for.
     */
    private final Type itemType;

    /**
     * The ID of the item to check for.
     */
    private final String itemID;

    /**
     * Whether to check the offhand.
     */
    private final boolean offhand;

    /**
     * The amount of the item to check for.
     */
    private final VariableNumber amount;

    /**
     * Constructs a new MMOItemsHandCondition.
     *
     * @param itemType the type of the item
     * @param itemID   the ID of the item
     * @param offhand  whether to check the offhand
     * @param amount   the amount of the item
     */
    public MMOItemsHandCondition(final Type itemType, final String itemID, final boolean offhand, final VariableNumber amount) {
        this.itemType = itemType;
        this.itemID = itemID;
        this.offhand = offhand;
        this.amount = amount;
    }

    @Override
    public boolean check(final OnlineProfile profile) throws QuestException {
        final PlayerInventory inv = profile.getPlayer().getInventory();
        final ItemStack item = offhand ? inv.getItemInOffHand() : inv.getItemInMainHand();

        final NBTItem realItemNBT = NBTItem.get(item);
        final String realItemType = realItemNBT.getString("MMOITEMS_ITEM_TYPE");
        final String realItemID = realItemNBT.getString("MMOITEMS_ITEM_ID");

        return realItemID.equalsIgnoreCase(itemID)
                && realItemType.equalsIgnoreCase(itemType.getId())
                && item.getAmount() == amount.getValue(profile).intValue();
    }
}
