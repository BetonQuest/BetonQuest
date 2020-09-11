package pl.betoncraft.betonquest.conditions;

import org.bukkit.inventory.ItemStack;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.Instruction.Item;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.util.List;

/**
 * Requires the player to have specified amount of items in the inventory
 */
public class ItemCondition extends Condition {

    private final Item[] questItems;

    public ItemCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        questItems = instruction.getItemList();
    }

    @Override
    protected Boolean execute(final String playerID) throws QuestRuntimeException {
        int successfulChecks = 0; // Count of successful checks

        for (final Item questItem : questItems) {
            int counter = 0; // Reset counter for each item
            final int amount = questItem.getAmount().getInt(playerID);

            final ItemStack[] inventoryItems = PlayerConverter.getPlayer(playerID).getInventory().getContents();
            for (final ItemStack item : inventoryItems) {
                if (item == null || !questItem.isItemEqual(item)) {
                    continue;
                }
                counter += item.getAmount();
            }

            final List<ItemStack> backpackItems = BetonQuest.getInstance().getPlayerData(playerID).getBackpack();
            for (final ItemStack item : backpackItems) {
                if (item == null || !questItem.isItemEqual(item)) {
                    continue;
                }
                counter += item.getAmount();
            }
            if (counter >= amount) {
                successfulChecks++;
            }
        }
        return successfulChecks == questItems.length;
    }
}
