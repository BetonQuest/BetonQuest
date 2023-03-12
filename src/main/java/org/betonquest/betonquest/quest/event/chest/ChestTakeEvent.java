package org.betonquest.betonquest.quest.event.chest;

import org.betonquest.betonquest.Instruction.Item;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.item.QuestItem;
import org.betonquest.betonquest.utils.location.CompoundLocation;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Removes items from a chest.
 */
public class ChestTakeEvent extends AbstractChestEvent implements Event {

    /**
     * The items to take from the chest.
     */
    private final Item[] items;

    /**
     * Creates a new ChestTakeEvent.
     *
     * @param compoundLocation The location of the chest.
     * @param items            The items to take from the chest.
     */
    public ChestTakeEvent(final CompoundLocation compoundLocation, final Item... items) {
        super(compoundLocation);
        this.items = items.clone();
    }

    @Override
    public void execute(final Profile profile) throws QuestRuntimeException {
        try {
            final Inventory inventory = getChest(profile).getInventory();
            for (final Item item : items) {
                final QuestItem questItem = item.getItem();
                final int amount = item.getAmount().getInt(profile);
                final ItemStack[] contents = inventory.getContents();
                final ItemStack[] newItems = removeItems(contents, questItem, amount);
                inventory.setContents(newItems);
            }
        } catch (final QuestRuntimeException e) {
            throw new QuestRuntimeException("Trying to take items from chest. " + e.getMessage(), e);
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
