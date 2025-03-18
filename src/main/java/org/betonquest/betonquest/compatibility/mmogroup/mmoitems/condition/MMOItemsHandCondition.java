package org.betonquest.betonquest.compatibility.mmogroup.mmoitems.condition;

import io.lumine.mythic.lib.api.item.NBTItem;
import net.Indyuce.mmoitems.api.Type;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.online.OnlineCondition;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

@SuppressWarnings("PMD.CommentRequired")
public class MMOItemsHandCondition implements OnlineCondition {

    private final Type itemType;

    private final String itemID;

    private final boolean offhand;

    private final VariableNumber amount;

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
