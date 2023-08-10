package org.betonquest.betonquest.quest.event.give;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction.Item;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.item.QuestItem;
import org.betonquest.betonquest.quest.event.NotificationSender;
import org.betonquest.betonquest.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Locale;

/**
 * Gives the player items.
 */
public class GiveEvent implements Event {

    /**
     * The items to give.
     */
    private final Item[] questItems;

    /**
     * The notification sender for items given.
     */
    private final NotificationSender itemsGivenSender;

    /**
     * The notification sender for items in backpack.
     */
    private final NotificationSender itemsInBackpackSender;

    /**
     * The notification sender for items dropped.
     */
    private final NotificationSender itemsDroppedSender;

    /**
     * Whether to put the items to the player's backpack.
     */
    private final boolean backpack;

    /**
     * Create the give event.
     *
     * @param questItems            the items to give
     * @param itemsGivenSender      the notification sender for items given
     * @param itemsInBackpackSender the notification sender for items in backpack
     * @param itemsDroppedSender    the notification sender for items dropped
     * @param backpack              whether to put the items to the player's backpack
     */
    public GiveEvent(final Item[] questItems, final NotificationSender itemsGivenSender, final NotificationSender itemsInBackpackSender, final NotificationSender itemsDroppedSender, final boolean backpack) {
        this.questItems = Arrays.copyOf(questItems, questItems.length);
        this.itemsGivenSender = itemsGivenSender;
        this.itemsInBackpackSender = itemsInBackpackSender;
        this.itemsDroppedSender = itemsDroppedSender;
        this.backpack = backpack;
    }

    @Override
    public void execute(final Profile profile) throws QuestRuntimeException {
        final Player player = profile.getOnlineProfile().get().getPlayer();
        Arrays.stream(questItems).toList().forEach(item -> {
            final QuestItem questItem = item.getItem();
            final int amount = item.getAmount().getInt(profile);
            giveItems(profile, player, questItem, amount);
            final String questItemName = questItem.getName() == null
                    ? questItem.getMaterial().toString().toLowerCase(Locale.ROOT).replace("_", " ")
                    : questItem.getName();
            itemsGivenSender.sendNotification(profile, questItemName, String.valueOf(amount));
        });
    }

    private void giveItems(final Profile profile, final Player player, final QuestItem questItem, final int totalAmount) {
        int amount = totalAmount;
        while (amount > 0) {
            boolean fullInventory = false;
            final ItemStack itemStackTemplate = questItem.generate(1, profile);
            final int stackSize = Math.min(amount, itemStackTemplate.getMaxStackSize());
            ItemStack itemStack = itemStackTemplate.clone();
            itemStack.setAmount(stackSize);
            if (!backpack) {
                final ItemStack leftItems = giveToInventory(player, itemStack);
                if (leftItems == null) {
                    amount -= stackSize;
                    continue;
                } else {
                    itemStack = leftItems;
                    fullInventory = true;
                }
            }
            if (Utils.isQuestItem(itemStack)) {
                giveToBackpack(profile, itemStack);
                if (fullInventory) {
                    itemsInBackpackSender.sendNotification(profile);
                }
            } else {
                dropItems(player, itemStack);
                itemsDroppedSender.sendNotification(profile);
            }
            amount -= stackSize;
        }
    }

    /**
     * Gives the item to the player. Returns null if the item was given successfully, otherwise returns the
     * items that was not given.
     *
     * @param player    the player to give the item to
     * @param itemStack the item to give
     * @return the item that was not given
     */
    private ItemStack giveToInventory(final Player player, final ItemStack itemStack) {
        return player.getInventory().addItem(itemStack).values().stream().findAny().orElse(null);
    }

    /**
     * Gives the item to the player's backpack.
     *
     * @param profile   the player to give the item to
     * @param itemStack the item to give
     */
    private void giveToBackpack(final Profile profile, final ItemStack itemStack) {
        BetonQuest.getInstance().getPlayerData(profile).addItem(itemStack, itemStack.getAmount());
    }

    /**
     * Drops the item on the ground.
     *
     * @param player    the player to drop the item for
     * @param itemStack the item to drop
     */
    private void dropItems(final Player player, final ItemStack itemStack) {
        player.getWorld().dropItem(player.getLocation(), itemStack);
    }
}
