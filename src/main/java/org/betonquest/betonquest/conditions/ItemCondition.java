package org.betonquest.betonquest.conditions;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.Instruction.Item;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
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
    @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.CognitiveComplexity"})
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    protected Boolean execute(final Profile profile) throws QuestRuntimeException {
        int successfulChecks = 0; // Count of successful checks

        for (final Item questItem : questItems) {
            int counter = 0; // Reset counter for each item
            final int amount = questItem.getAmount().getInt(profile);

            final ItemStack[] inventoryItems = profile.getOnlineProfile().getOnlinePlayer().getInventory().getContents();
            for (final ItemStack item : inventoryItems) {
                if (item == null || !questItem.isItemEqual(item)) {
                    continue;
                }
                counter += item.getAmount();
            }

            final List<ItemStack> backpackItems = BetonQuest.getInstance().getPlayerData(profile).getBackpack();
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
