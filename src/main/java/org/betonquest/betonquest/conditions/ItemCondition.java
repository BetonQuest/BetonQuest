package org.betonquest.betonquest.conditions;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.Instruction.Item;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Requires the player to have specified amount of items in the inventory
 */
@SuppressWarnings("PMD.CommentRequired")
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
