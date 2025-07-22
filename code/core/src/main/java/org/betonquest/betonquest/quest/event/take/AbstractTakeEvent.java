package org.betonquest.betonquest.quest.event.take;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.online.OnlineEvent;
import org.betonquest.betonquest.database.PlayerData;
import org.betonquest.betonquest.quest.event.NotificationSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Removes items from player's inventory and/or backpack.
 */
public abstract class AbstractTakeEvent implements OnlineEvent {

    /**
     * The order in which the checks should be performed to remove the item.
     */
    protected final List<CheckType> checkOrder;

    /**
     * The notification sender to send notifications to the player.
     */
    protected final NotificationSender notificationSender;

    /**
     * Create the abstract take event.
     *
     * @param checkOrder         the order in which the checks should be performed
     * @param notificationSender the notification sender to use
     */
    public AbstractTakeEvent(final List<CheckType> checkOrder, final NotificationSender notificationSender) {
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
        for (final CheckType type : checkOrder) {
            switch (type) {
                case INVENTORY -> checkInventory(profile);
                case ARMOR -> checkArmor(profile);
                case OFFHAND -> checkOffhand(profile);
                case BACKPACK -> checkBackpack(profile);
            }
        }
    }

    private void checkInventory(final OnlineProfile profile) throws QuestException {
        final Player player = profile.getPlayer();
        final ItemStack[] inventory = player.getInventory().getStorageContents();
        final ItemStack[] newInv = takeDesiredAmount(profile, inventory);
        player.getInventory().setStorageContents(newInv);
    }

    private void checkArmor(final OnlineProfile profile) throws QuestException {
        final Player player = profile.getPlayer();
        final ItemStack[] armorSlots = player.getInventory().getArmorContents();
        final ItemStack[] newArmor = takeDesiredAmount(profile, armorSlots);
        player.getInventory().setArmorContents(newArmor);
    }

    private void checkOffhand(final OnlineProfile profile) throws QuestException {
        final Player player = profile.getPlayer();
        final ItemStack offhand = player.getInventory().getItemInOffHand();
        final ItemStack[] newOffhand = takeDesiredAmount(profile, offhand);
        player.getInventory().setItemInOffHand(newOffhand[0]);
    }

    private void checkBackpack(final OnlineProfile profile) throws QuestException {
        final PlayerData playerData = BetonQuest.getInstance().getPlayerDataStorage().get(profile);
        final List<ItemStack> backpack = playerData.getBackpack();
        final List<ItemStack> newBackpack = removeDesiredAmount(profile, backpack);
        playerData.setBackpack(newBackpack);
    }

    private List<ItemStack> removeDesiredAmount(final Profile profile, final List<ItemStack> items) throws QuestException {
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
     * @throws QuestException if an error occurs during the process
     */
    protected abstract ItemStack[] takeDesiredAmount(Profile profile, ItemStack... items) throws QuestException;
}
