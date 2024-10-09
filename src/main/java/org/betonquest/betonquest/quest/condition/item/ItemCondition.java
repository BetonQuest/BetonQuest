package org.betonquest.betonquest.quest.condition.item;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction.Item;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.quest.condition.online.OnlineCondition;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * A condition that checks if the player has the specified items.
 */
public class ItemCondition implements OnlineCondition {

    /**
     * The BetonQuest instance.
     */
    private final BetonQuest betonQuest;

    /**
     * The items to check for.
     */
    private final Item[] questItems;

    /**
     * Create a new item condition.
     *
     * @param questItems the items to check for
     * @param betonQuest the BetonQuest instance
     */
    public ItemCondition(final Item[] questItems, final BetonQuest betonQuest) {
        this.questItems = Arrays.copyOf(questItems, questItems.length);
        this.betonQuest = betonQuest;
    }

    @Override
    public boolean check(final OnlineProfile profile) throws QuestRuntimeException {
        final ItemStack[] inventoryItems = profile.getPlayer().getInventory().getContents();
        final List<ItemStack> backpackItems = betonQuest.getPlayerData(profile).getBackpack();
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
