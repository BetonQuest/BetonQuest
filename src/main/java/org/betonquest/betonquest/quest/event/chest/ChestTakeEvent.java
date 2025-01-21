package org.betonquest.betonquest.quest.event.chest;

import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.Item;
import org.betonquest.betonquest.instruction.variable.location.VariableLocation;
import org.betonquest.betonquest.item.QuestItem;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * Removes items from a chest.
 */
public class ChestTakeEvent extends AbstractChestEvent {

    /**
     * The items to take from the chest.
     */
    private final Item[] items;

    /**
     * Creates a new ChestTakeEvent.
     *
     * @param variableLocation The location of the chest.
     * @param items            The items to take from the chest.
     */
    public ChestTakeEvent(final VariableLocation variableLocation, final Item... items) {
        super(variableLocation);
        this.items = items.clone();
    }

    @Override
    public void execute(@Nullable final Profile profile) throws QuestException {
        try {
            final Inventory inventory = getChest(profile).getInventory();
            for (final Item item : items) {
                final QuestItem questItem = item.getItem();
                final int amount = item.getAmount().getValue(profile).intValue();
                final ItemStack[] newItems = removeItems(inventory.getContents(), questItem, amount);
                inventory.setContents(newItems);
            }
        } catch (final QuestException e) {
            throw new QuestException("Trying to take items from chest. " + e.getMessage(), e);
        }
    }

    private ItemStack[] removeItems(final ItemStack[] items, final QuestItem questItem, final int amount) {
        int inputAmount = amount;
        for (int i = 0; i < items.length; i++) {
            final ItemStack item = items[i];
            if (questItem.compare(item)) {
                if (item.getAmount() - inputAmount <= 0) {
                    inputAmount = inputAmount - item.getAmount();
                    items[i] = null;
                } else {
                    item.setAmount(item.getAmount() - inputAmount);
                    inputAmount = 0;
                }
                if (inputAmount <= 0) {
                    break;
                }
            }
        }
        return items;
    }
}
