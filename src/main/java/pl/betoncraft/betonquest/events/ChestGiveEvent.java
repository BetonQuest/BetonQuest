package pl.betoncraft.betonquest.events;

import org.bukkit.block.Block;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.Instruction.Item;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.item.QuestItem;
import pl.betoncraft.betonquest.utils.location.CompoundLocation;

import java.util.HashMap;

/**
 * Puts items in a specified chest.
 */
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
                for (final Integer leftNumber : left.keySet()) {
                    final ItemStack itemStack = left.get(leftNumber);
                    block.getWorld().dropItem(block.getLocation(), itemStack);
                }
                amount = amount - stackSize;
            }
        }
        return null;
    }
}
