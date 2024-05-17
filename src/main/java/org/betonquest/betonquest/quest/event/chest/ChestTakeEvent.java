package org.betonquest.betonquest.quest.event.chest;

import edu.umd.cs.findbugs.annotations.Nullable;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.betonquest.betonquest.Instruction.Item;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.event.HybridEvent;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.item.QuestItem;
import org.betonquest.betonquest.utils.location.CompoundLocation;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Removes items from a chest.
 */
public class ChestTakeEvent extends AbstractChestEvent implements HybridEvent {

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
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    public void execute(@Nullable final Profile profile) throws QuestRuntimeException {
        try {
            final Inventory inventory = getChest(profile).getInventory();
            for (final Item item : items) {
                final QuestItem questItem = item.getItem();
                final int amount = item.getAmount().getInt(profile);
                final ItemStack[] newItems = removeItems(inventory.getContents(), questItem, amount);
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
