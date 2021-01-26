package org.betonquest.betonquest.compatibility.mmogroup.mmoitems;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.CustomLog;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import net.mmogroup.mmolib.api.item.NBTItem;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("PMD.CommentRequired")
@CustomLog
public class MMOItemsTakeEvent extends QuestEvent {

    private final Type itemType;
    private final String itemID;
    private final boolean notify;
    private VariableNumber deleteAmountVar = new VariableNumber(1);

    @SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
    public MMOItemsTakeEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);

        itemType = MMOItems.plugin.getTypes().get(instruction.next());
        itemID = instruction.next();
        if (instruction.size() > 3) {
            deleteAmountVar = instruction.getVarNum();
        }
        notify = instruction.hasArgument("notify");
    }

    @SuppressWarnings({"PMD.PreserveStackTrace", "PMD.CyclomaticComplexity"})
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    @Override
    protected Void execute(final String playerID) throws QuestRuntimeException {
        final Inventory inv = PlayerConverter.getPlayer(playerID).getInventory();
        final ItemStack[] inventoryItems = inv.getContents();
        String itemName = itemID;
        final int deleteAmount = deleteAmountVar.getInt(playerID);

        int deletedCounter = 0;

        for (final ItemStack item : inventoryItems) {
            if (item == null) {
                continue;
            }
            final NBTItem realItemNBT = NBTItem.get(item);
            final String realItemType = realItemNBT.getString("MMOITEMS_ITEM_TYPE");
            final String realItemID = realItemNBT.getString("MMOITEMS_ITEM_ID");

            if (realItemID.equalsIgnoreCase(itemID) && realItemType.equalsIgnoreCase(itemType.getId())) {
                itemName = item.getItemMeta().getDisplayName();
                for (int i = item.getAmount(); i > 0; i--) {
                    if (deletedCounter < deleteAmount) {
                        item.setAmount(item.getAmount() - 1);
                        deletedCounter++;
                    }
                }
            }
        }
        inv.setContents(inventoryItems);

        if (notify && deletedCounter > 1) {
            try {
                Config.sendNotify(instruction.getPackage().getName(), playerID, "items_taken",
                        new String[]{itemName, String.valueOf(deletedCounter)},
                        "items_taken,info");
            } catch (final QuestRuntimeException e) {
                LOG.warning(instruction.getPackage(), "The notify system was unable to play a sound for the 'items_taken' category in '" + getFullId() + "'. Error was: '" + e.getMessage() + "'", e);
            }
        }
        return null;
    }
}

