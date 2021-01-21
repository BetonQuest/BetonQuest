package org.betonquest.betonquest.compatibility.mmogroup.mmoitems;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.manager.TypeManager;
import net.mmogroup.mmolib.api.item.NBTItem;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@SuppressWarnings("PMD.CommentRequired")
public class MMOItemsItemCondition extends Condition {

    private final Type itemType;
    private final String itemID;
    private int amount = 1;

    public MMOItemsItemCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);

        final TypeManager typeManager = MMOItems.plugin.getTypes();
        itemType = typeManager.get(instruction.next());
        itemID = instruction.next();

        final List<Integer> potentialAmount = instruction.getAllNumbers();
        if (!potentialAmount.isEmpty()) {
            amount = potentialAmount.get(0);
        }
    }

    @Override
    protected Boolean execute(final String playerID) throws QuestRuntimeException {
        final ItemStack[] inventoryItems = PlayerConverter.getPlayer(playerID).getInventory().getContents();

        int counter = 0;
        for (final ItemStack item : inventoryItems) {
            if (item == null) {
                continue;
            }
            final NBTItem realItemNBT = NBTItem.get(item);
            final String realItemType = realItemNBT.getString("MMOITEMS_ITEM_TYPE");
            final String realItemID = realItemNBT.getString("MMOITEMS_ITEM_ID");

            if (realItemID.equalsIgnoreCase(itemID) && realItemType.equalsIgnoreCase(itemType.getId())) {
                counter = counter + item.getAmount();
            }
        }
        return counter >= amount;
    }
}
