package org.betonquest.betonquest.quest.event.chest;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.type.QuestItemWrapper;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.item.QuestItem;
import org.betonquest.betonquest.api.profile.Profile;
import org.bukkit.Location;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Removes items from a chest.
 */
public class ChestTakeEvent extends AbstractChestEvent {

    /**
     * The items to take from the chest.
     */
    private final Variable<List<QuestItemWrapper>> items;

    /**
     * Creates a new ChestTakeEvent.
     *
     * @param variableLocation The location of the chest.
     * @param items            The items to take from the chest.
     */
    public ChestTakeEvent(final Variable<Location> variableLocation, final Variable<List<QuestItemWrapper>> items) {
        super(variableLocation);
        this.items = items;
    }

    @Override
    public void execute(@Nullable final Profile profile) throws QuestException {
        try {
            final Inventory inventory = getChest(profile).getInventory();
            for (final QuestItemWrapper item : items.getValue(profile)) {
                final QuestItem questItem = item.getItem(profile);
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
            if (questItem.matches(item)) {
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
