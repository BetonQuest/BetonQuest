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
import pl.betoncraft.betonquest.utils.LocationData;

/**
 * Removes items from a chest.
 */
public class ChestTakeEvent extends QuestEvent {

    private final Item[] questItems;
    private final LocationData loc;

    public ChestTakeEvent(final Instruction instruction) throws InstructionParseException {
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
            throw new QuestRuntimeException("Trying to take items from chest, but there's no chest! Location: X"
                    + block.getX() + " Y" + block.getY() + " Z" + block.getZ(), e);
        }
        for (final Item item : questItems) {
            final QuestItem questItem = item.getItem();
            final int amount = item.getAmount().getInt(playerID);
            // Remove Quest items from player's inventory
            chest.getInventory().setContents(removeItems(chest.getInventory().getContents(), questItem, amount));
        }
        return null;
    }

    private ItemStack[] removeItems(final ItemStack[] items, final QuestItem questItem, int amount) {
        for (int i = 0; i < items.length; i++) {
            final ItemStack item = items[i];
            if (questItem.compare(item)) {
                if (item.getAmount() - amount <= 0) {
                    amount = amount - item.getAmount();
                    items[i] = null;
                } else {
                    item.setAmount(item.getAmount() - amount);
                    amount = 0;
                }
                if (amount <= 0) {
                    break;
                }
            }
        }
        return items;
    }

}
