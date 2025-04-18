package org.betonquest.betonquest.compatibility.mmogroup.mmoitems.condition;

import net.Indyuce.mmoitems.api.Type;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.online.OnlineCondition;
import org.betonquest.betonquest.compatibility.mmogroup.mmoitems.MMOItemsUtils;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Condition that checks if a player has a certain amount of an MMOItems item.
 */
public class MMOItemsItemCondition implements OnlineCondition {
    /**
     * The player data storage.
     */
    private final PlayerDataStorage playerDataStorage;

    /**
     * The type of the item to check for.
     */
    private final Type itemType;

    /**
     * The ID of the item to check for.
     */
    private final String itemID;

    /**
     * The amount of the item to check for.
     */
    private final VariableNumber amount;

    /**
     * Constructs a new MMOItemsItemCondition.
     *
     * @param playerDataStorage the player data storage
     * @param itemType          the type of the item
     * @param itemID            the ID of the item
     * @param amount            the amount of the item
     */
    public MMOItemsItemCondition(final PlayerDataStorage playerDataStorage, final Type itemType, final String itemID, final VariableNumber amount) {
        this.playerDataStorage = playerDataStorage;
        this.itemType = itemType;
        this.itemID = itemID;
        this.amount = amount;
    }

    @Override
    public boolean check(final OnlineProfile profile) throws QuestException {
        int counter = 0;

        final ItemStack[] inventoryItems = profile.getPlayer().getInventory().getContents();
        for (final ItemStack item : inventoryItems) {
            if (MMOItemsUtils.equalsMMOItem(item, itemType, itemID)) {
                counter += item.getAmount();
            }
        }

        final List<ItemStack> backpackItems = playerDataStorage.get(profile).getBackpack();
        for (final ItemStack item : backpackItems) {
            if (MMOItemsUtils.equalsMMOItem(item, itemType, itemID)) {
                counter += item.getAmount();
            }
        }

        return counter >= amount.getValue(profile).intValue();
    }
}
