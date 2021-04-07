package pl.betoncraft.betonquest.conditions;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.bukkit.block.Block;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.Instruction.Item;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.utils.location.CompoundLocation;

/**
 * Checks if the chest contains specified items.
 */
@SuppressWarnings("PMD.CommentRequired")
public class ChestItemCondition extends Condition {

    private final Item[] questItems;
    private final CompoundLocation loc;

    public ChestItemCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        staticness = true;
        persistent = true;
        // extract data
        loc = instruction.getLocation();
        questItems = instruction.getItemList();
    }

    @Override
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    protected Boolean execute(final String playerID) throws QuestRuntimeException {
        final Block block = loc.getLocation(playerID).getBlock();
        final InventoryHolder chest;
        try {
            chest = (InventoryHolder) block.getState();
        } catch (ClassCastException e) {
            throw new QuestRuntimeException("Trying to check items in a chest, but there's no chest! Location: X" + block.getX() + " Y"
                    + block.getY() + " Z" + block.getZ(), e);
        }
        int counter = 0;
        for (final Item questItem : questItems) {
            int amount = questItem.getAmount().getInt(playerID);
            final ItemStack[] inventoryItems = chest.getInventory().getContents();
            for (final ItemStack item : inventoryItems) {
                if (item == null) {
                    continue;
                }
                if (!questItem.isItemEqual(item)) {
                    continue;
                }
                amount -= item.getAmount();
                if (amount <= 0) {
                    counter++;
                    break;
                }
            }
        }
        return counter == questItems.length;
    }

}
