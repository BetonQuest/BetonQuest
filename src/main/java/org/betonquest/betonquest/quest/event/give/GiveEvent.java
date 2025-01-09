package org.betonquest.betonquest.quest.event.give;

import org.betonquest.betonquest.Instruction.Item;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.quest.event.online.OnlineEvent;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.item.QuestItem;
import org.betonquest.betonquest.modules.data.PlayerDataStorage;
import org.betonquest.betonquest.quest.event.NotificationSender;
import org.betonquest.betonquest.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Locale;

/**
 * Gives the player items.
 */
public class GiveEvent implements OnlineEvent {

    /**
     * The items to give.
     */
    private final Item[] questItems;

    /**
     * The notification sender to use when putting items into the player's inventory.
     */
    private final NotificationSender itemsGivenSender;

    /**
     * The notification sender to use when putting items into the player's backpack.
     */
    private final NotificationSender itemsInBackpackSender;

    /**
     * The notification sender to use when dropping items at the player's location.
     */
    private final NotificationSender itemsDroppedSender;

    /**
     * Whether to put the items to the player's backpack.
     */
    private final boolean backpack;

    /**
     * Storage for player backpack.
     */
    private final PlayerDataStorage dataStorage;

    /**
     * Create the give event.
     *
     * @param questItems            the items to give
     * @param itemsGivenSender      the notification sender when giving items
     * @param itemsInBackpackSender the notification sender when putting items into the backpack
     * @param itemsDroppedSender    the notification sender when dropping items
     * @param backpack              whether to put the items to the player's backpack
     * @param dataStorage           the storage providing player backpack
     */
    public GiveEvent(final Item[] questItems, final NotificationSender itemsGivenSender, final NotificationSender itemsInBackpackSender,
                     final NotificationSender itemsDroppedSender, final boolean backpack, final PlayerDataStorage dataStorage) {
        this.questItems = Arrays.copyOf(questItems, questItems.length);
        this.itemsGivenSender = itemsGivenSender;
        this.itemsInBackpackSender = itemsInBackpackSender;
        this.itemsDroppedSender = itemsDroppedSender;
        this.backpack = backpack;
        this.dataStorage = dataStorage;
    }

    @Override
    public void execute(final OnlineProfile profile) throws QuestException {
        final Player player = profile.getPlayer();
        for (final Item item : questItems) {
            final QuestItem questItem = item.getItem();
            final int amount = item.getAmount().getValue(profile).intValue();
            giveItems(profile, player, questItem, amount);
            final String questItemName = questItem.getName() == null
                    ? questItem.getMaterial().toString().toLowerCase(Locale.ROOT).replace("_", " ")
                    : questItem.getName();
            itemsGivenSender.sendNotification(profile, questItemName, String.valueOf(amount));
        }
    }

    @SuppressWarnings("PMD.CognitiveComplexity")
    private void giveItems(final OnlineProfile profile, final Player player, final QuestItem questItem, final int totalAmount)
            throws QuestException {
        int amount = totalAmount;
        while (amount > 0) {
            final ItemStack itemStackTemplate = questItem.generate(1, profile);
            final int stackSize = Math.min(amount, itemStackTemplate.getMaxStackSize());
            if (stackSize <= 0) {
                throw new QuestException("Item stack size is 0 or less!");
            }
            boolean fullInventory = false;
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
     * Gives the item stack to the player. Returns null if all items of the stack were given successfully,
     * otherwise returns the items that were not given.
     *
     * @param player    the player to give the item to
     * @param itemStack the items to give
     * @return the items that could not be given
     */
    @Nullable
    private ItemStack giveToInventory(final Player player, final ItemStack itemStack) {
        return player.getInventory().addItem(itemStack).values().stream().findAny().orElse(null);
    }

    /**
     * Gives the item to the player's backpack.
     *
     * @param profile   the player to give the item to
     * @param itemStack the item to give
     */
    private void giveToBackpack(final OnlineProfile profile, final ItemStack itemStack) {
        dataStorage.get(profile).addItem(itemStack, itemStack.getAmount());
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
