package org.betonquest.betonquest.quest.condition.item;

import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.online.OnlineCondition;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.instruction.Item;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * A condition that checks if the player has the specified items.
 */
public class ItemCondition implements OnlineCondition {

    /**
     * Storage for player data.
     */
    private final PlayerDataStorage dataStorage;

    /**
     * The items to check for.
     */
    private final Item[] questItems;

    /**
     * Create a new item condition.
     *
     * @param questItems  the items to check for
     * @param dataStorage the storage providing player data
     */
    public ItemCondition(final Item[] questItems, final PlayerDataStorage dataStorage) {
        this.questItems = Arrays.copyOf(questItems, questItems.length);
        this.dataStorage = dataStorage;
    }

    @Override
    public boolean check(final OnlineProfile profile) throws QuestException {
        final ItemStack[] inventoryItems = profile.getPlayer().getInventory().getContents();
        final List<ItemStack> backpackItems = dataStorage.get(profile).getBackpack();
        for (final Item questItem : questItems) {
            final long totalAmount = Stream.concat(
                            Stream.of(inventoryItems),
                            backpackItems.stream()
                    )
                    .filter(itemStack -> itemStack != null && questItem.isItemEqual(itemStack))
                    .mapToInt(ItemStack::getAmount)
                    .sum();
            if (totalAmount < questItem.getAmount().getValue(profile).intValue()) {
                return false;
            }
        }
        return true;
    }
}
