package org.betonquest.betonquest.quest.action.take;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.action.OnlineAction;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.database.PlayerData;
import org.betonquest.betonquest.quest.action.NotificationSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Removes items from player's inventory and/or backpack.
 */
public abstract class AbstractTakeAction implements OnlineAction {

    /**
     * The order in which the checks should be performed to remove the item.
     */
    protected final Argument<List<CheckType>> checkOrder;

    /**
     * The notification sender to send notifications to the player.
     */
    protected final NotificationSender notificationSender;

    /**
     * The storage for player data.
     */
    private final PlayerDataStorage playerDataStorage;

    /**
     * Create the abstract take action.
     *
     * @param playerDataStorage  the storage for player data
     * @param checkOrder         the order in which the checks should be performed
     * @param notificationSender the notification sender to use
     */
    public AbstractTakeAction(final PlayerDataStorage playerDataStorage, final Argument<List<CheckType>> checkOrder,
                              final NotificationSender notificationSender) {
        this.playerDataStorage = playerDataStorage;
        this.checkOrder = checkOrder;
        this.notificationSender = notificationSender;
    }

    /**
     * Check the selected types for the profile.
     *
     * @param profile the profile to check the types for
     * @throws QuestException if an error occurs during the check
     */
    protected void checkSelectedTypes(final OnlineProfile profile) throws QuestException {
        for (final CheckType type : checkOrder.getValue(profile)) {
            switch (type) {
                case INVENTORY -> checkInventory(profile);
                case ARMOR -> checkArmor(profile);
                case MAINHAND -> checkEquipmentSlot(profile, EquipmentSlot.HAND);
                case OFFHAND -> checkEquipmentSlot(profile, EquipmentSlot.OFF_HAND);
                case BACKPACK -> checkBackpack(profile);
            }
        }
    }

    private void checkInventory(final OnlineProfile profile) {
        final Player player = profile.getPlayer();
        final ItemStack[] inventory = player.getInventory().getStorageContents();
        final ItemStack[] newInv = takeDesiredAmount(profile, inventory);
        player.getInventory().setStorageContents(newInv);
    }

    private void checkArmor(final OnlineProfile profile) {
        final Player player = profile.getPlayer();
        final ItemStack[] armorSlots = player.getInventory().getArmorContents();
        final ItemStack[] newArmor = takeDesiredAmount(profile, armorSlots);
        player.getInventory().setArmorContents(newArmor);
    }

    private void checkEquipmentSlot(final OnlineProfile profile, final EquipmentSlot slot) {
        final Player player = profile.getPlayer();
        final ItemStack item = player.getInventory().getItem(slot);
        final ItemStack[] newItem = takeDesiredAmount(profile, item);
        player.getInventory().setItem(slot, newItem[0]);
    }

    private void checkBackpack(final OnlineProfile profile) {
        final PlayerData playerData = playerDataStorage.get(profile);
        final List<ItemStack> backpack = playerData.getBackpack();
        final List<ItemStack> newBackpack = removeDesiredAmount(profile, backpack);
        playerData.setBackpack(newBackpack);
    }

    private List<ItemStack> removeDesiredAmount(final Profile profile, final List<ItemStack> items) {
        final ItemStack[] itemArray = items.toArray(new ItemStack[0]);
        final ItemStack[] remainingItems = takeDesiredAmount(profile, itemArray);
        return Arrays.stream(remainingItems)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Abstract method to take the desired amount of items.
     *
     * @param profile the profile of the player
     * @param items   the items to take from
     * @return the remaining items after taking the desired amount
     */
    protected abstract ItemStack[] takeDesiredAmount(Profile profile, ItemStack... items);
}
