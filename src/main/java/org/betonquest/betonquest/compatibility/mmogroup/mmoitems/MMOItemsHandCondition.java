package org.betonquest.betonquest.compatibility.mmogroup.mmoitems;

import io.lumine.mythic.lib.api.item.NBTItem;
import net.Indyuce.mmoitems.api.Type;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

@SuppressWarnings("PMD.CommentRequired")
public class MMOItemsHandCondition extends Condition {
    /**
     * The offhand key.
     */
    private static final String OFFHAND_KEY = "offhand";

    private final Type itemType;

    private final String itemID;

    private boolean offhand;

    private VariableNumber amount;

    public MMOItemsHandCondition(final Instruction instruction) throws QuestException {
        super(instruction, true);

        itemType = MMOItemsUtils.getMMOItemType(instruction.next());
        itemID = instruction.next();

        amount = instruction.get("1", VariableNumber::new);
        while (instruction.hasNext()) {
            final String next = instruction.next();
            if (OFFHAND_KEY.equals(next)) {
                offhand = true;
            } else {
                amount = instruction.get(next, VariableNumber::new);
            }
        }
    }

    @Override
    protected Boolean execute(final Profile profile) throws QuestException {
        final PlayerInventory inv = profile.getOnlineProfile().get().getPlayer().getInventory();
        final ItemStack item = offhand ? inv.getItemInOffHand() : inv.getItemInMainHand();

        final NBTItem realItemNBT = NBTItem.get(item);
        final String realItemType = realItemNBT.getString("MMOITEMS_ITEM_TYPE");
        final String realItemID = realItemNBT.getString("MMOITEMS_ITEM_ID");

        return realItemID.equalsIgnoreCase(itemID)
                && realItemType.equalsIgnoreCase(itemType.getId())
                && item.getAmount() == amount.getInt(profile);
    }
}
