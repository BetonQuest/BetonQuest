package org.betonquest.betonquest.compatibility.mmogroup.mmoitems;

import io.lumine.mythic.lib.api.item.NBTItem;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.manager.TypeManager;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.List;

@SuppressWarnings("PMD.CommentRequired")
public class MMOItemsHandCondition extends Condition {
    private final Type itemType;

    private final String itemID;

    private final boolean offhand;

    private int amount = 1;

    public MMOItemsHandCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);

        final TypeManager typeManager = MMOItems.plugin.getTypes();
        itemType = typeManager.get(instruction.next());
        itemID = instruction.next();

        final List<Integer> potentialAmount = instruction.getAllNumbers();
        if (!potentialAmount.isEmpty()) {
            amount = potentialAmount.get(0);
        }

        offhand = instruction.hasArgument("offhand");
    }

    @Override
    protected Boolean execute(final Profile profile) throws QuestRuntimeException {
        final PlayerInventory inv = profile.getOnlineProfile().get().getPlayer().getInventory();
        final ItemStack item = offhand ? inv.getItemInOffHand() : inv.getItemInMainHand();

        final NBTItem realItemNBT = NBTItem.get(item);
        final String realItemType = realItemNBT.getString("MMOITEMS_ITEM_TYPE");
        final String realItemID = realItemNBT.getString("MMOITEMS_ITEM_ID");

        return realItemID.equalsIgnoreCase(itemID) && realItemType.equalsIgnoreCase(itemType.getId()) && item.getAmount() == amount;
    }
}
