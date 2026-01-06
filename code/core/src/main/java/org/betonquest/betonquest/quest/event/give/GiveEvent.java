package org.betonquest.betonquest.quest.event.give;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.common.component.VariableReplacement;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.type.ItemWrapper;
import org.betonquest.betonquest.api.item.QuestItem;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.action.online.OnlineAction;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.item.typehandler.QuestHandler;
import org.betonquest.betonquest.quest.event.NotificationSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Gives the player items.
 */
public class GiveEvent implements OnlineAction {

    /**
     * The items to give.
     */
    private final Argument<List<ItemWrapper>> questItems;

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
    private final FlagArgument<Boolean> backpack;

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
    public GiveEvent(final Argument<List<ItemWrapper>> questItems, final NotificationSender itemsGivenSender,
                     final NotificationSender itemsInBackpackSender, final NotificationSender itemsDroppedSender,
                     final FlagArgument<Boolean> backpack, final PlayerDataStorage dataStorage) {
        this.questItems = questItems;
        this.itemsGivenSender = itemsGivenSender;
        this.itemsInBackpackSender = itemsInBackpackSender;
        this.itemsDroppedSender = itemsDroppedSender;
        this.backpack = backpack;
        this.dataStorage = dataStorage;
    }

    @Override
    public void execute(final OnlineProfile profile) throws QuestException {
        final Player player = profile.getPlayer();
        for (final ItemWrapper item : questItems.getValue(profile)) {
            final QuestItem questItem = item.getItem(profile);
            final int amount = item.getAmount().getValue(profile).intValue();
            giveItems(profile, player, questItem, amount);
            itemsGivenSender.sendNotification(profile,
                    new VariableReplacement("item", questItem.getName()),
                    new VariableReplacement("amount", Component.text(amount)));
        }
    }

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
            if (!backpack.getValue(profile).orElse(false)) {
                final ItemStack leftItems = giveToInventory(player, itemStack);
                if (leftItems == null) {
                    amount -= stackSize;
                    continue;
                }
                itemStack = leftItems;
                fullInventory = true;
            }
            if (QuestHandler.isQuestItem(itemStack)) {
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

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
