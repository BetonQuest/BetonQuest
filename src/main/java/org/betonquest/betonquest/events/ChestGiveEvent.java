package org.betonquest.betonquest.events;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.Instruction.Item;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.item.QuestItem;
import org.betonquest.betonquest.utils.location.CompoundLocation;
import org.bukkit.block.Block;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

/**
 * Puts items in a specified chest.
 */
@SuppressWarnings("PMD.CommentRequired")
public class ChestGiveEvent extends QuestEvent {

    private final Item[] questItems;
    private final CompoundLocation loc;

    public ChestGiveEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        staticness = true;
        persistent = true;
        loc = instruction.getLocation();
        questItems = instruction.getItemList();
    }

    @Override
    @SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
    protected Void execute(final String playerID) throws QuestRuntimeException {
        final Block block = loc.getLocation(playerID).getBlock();
        final InventoryHolder chest;
        try {
            chest = (InventoryHolder) block.getState();
        } catch (ClassCastException e) {
            throw new QuestRuntimeException("Trying to put items in chest, but there's no chest! Location: X"
                    + block.getX() + " Y" + block.getY() + " Z" + block.getZ(), e);
        }
        for (final Item theItem : questItems) {
            final QuestItem questItem = theItem.getItem();
            int amount = theItem.getAmount().getInt(playerID);
            while (amount > 0) {
                final int stackSize;
                if (amount > 64) {
                    stackSize = 64;
                } else {
                    stackSize = amount;
                }
                final ItemStack item = questItem.generate(stackSize);
                final HashMap<Integer, ItemStack> left = chest.getInventory().addItem(item);
                for (final ItemStack itemStack : left.values()) {
                    block.getWorld().dropItem(block.getLocation(), itemStack);
                }
                amount = amount - stackSize;
            }
        }
        return null;
    }
}
