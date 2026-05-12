package org.betonquest.betonquest.quest.action.take;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.common.component.VariableReplacement;
import org.betonquest.betonquest.api.identifier.ActionIdentifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.type.ItemWrapper;
import org.betonquest.betonquest.api.item.QuestItem;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.action.OnlineAction;
import org.betonquest.betonquest.api.service.action.ActionManager;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.quest.action.NotificationSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Removes items from player's inventory and/or backpack.
 */
public class TakeAction implements OnlineAction {

    /**
     * The storage for player data.
     */
    private final PlayerDataStorage playerDataStorage;

    /**
     * The notification sender to send notifications to the player.
     */
    private final NotificationSender notificationSender;

    /**
     * The action manager.
     */
    private final ActionManager actionManager;

    /**
     * The items to be removed.
     */
    private final Argument<List<ItemWrapper>> questItems;

    /**
     * The order in which the checks should be performed to remove the item.
     */
    private final Argument<List<CheckType>> checkOrder;

    /**
     * If not all items being present should not take items at all.
     */
    private final FlagArgument<Boolean> abort;

    /**
     * Actions to execute if not all items could be taken.
     */
    @Nullable
    private final Argument<List<ActionIdentifier>> failActions;

    /**
     * Constructs a new TakeAction.
     *
     * @param playerDataStorage  the storage for player data
     * @param notificationSender the notification sender to use
     * @param actionManager      the action manager
     * @param questItems         the items to be removed
     * @param checkOrder         the order in which the checks should be performed
     * @param abort              if not all items being present should not take items at all
     * @param failActions        the actions to execute if not all items could be taken
     */
    public TakeAction(final PlayerDataStorage playerDataStorage, final NotificationSender notificationSender, final ActionManager actionManager,
                      final Argument<List<ItemWrapper>> questItems, final Argument<List<CheckType>> checkOrder,
                      final FlagArgument<Boolean> abort, @Nullable final Argument<List<ActionIdentifier>> failActions) {
        this.playerDataStorage = playerDataStorage;
        this.actionManager = actionManager;
        this.checkOrder = checkOrder;
        this.notificationSender = notificationSender;
        this.questItems = questItems;
        this.abort = abort;
        this.failActions = failActions;
    }

    @Override
    public void execute(final OnlineProfile profile) throws QuestException {
        final Map<QuestItem, Integer> neededDeletions = new HashMap<>();
        for (final ItemWrapper item : questItems.getValue(profile)) {
            final QuestItem questItem = item.getItem(profile);
            final int deleteAmount = item.getAmount().getValue(profile).intValue();
            neededDeletions.compute(questItem, (ignored, integer) -> (integer == null ? 0 : integer) + deleteAmount);
        }
        final Session session = new Session(profile, neededDeletions, abort.getValue(profile).orElse(false));
        session.checkSelectedTypes();
        neededDeletions.forEach((questItem, deleteAmount) -> {
            final Integer notTaken = session.stillToTake.getOrDefault(questItem, 0);
            notificationSender.sendNotification(profile,
                    new VariableReplacement("item", questItem.getName()),
                    new VariableReplacement("amount", Component.text(deleteAmount - notTaken)));
        });
    }

    /**
     * Take session which stores changed inventories and items to still remove.
     */
    @SuppressWarnings("PMD.ReturnEmptyCollectionRatherThanNull")
    private final class Session {

        /**
         * Profile of the player.
         */
        private final OnlineProfile profile;

        /**
         * The player to take the items from.
         */
        private final Player player;

        /**
         * If not all items being present should result in a fail.
         */
        private final boolean abort;

        /**
         * Items which still need to be taken.
         */
        private final Map<QuestItem, Integer> stillToTake;

        /**
         * The inventory storage contents to set, if changed.
         */
        private ItemStack @Nullable [] newInventory;

        /**
         * The armor to set, if changed.
         */
        private ItemStack @Nullable [] newArmor;

        /**
         * The offhand, if changed.
         */
        private ItemStack @Nullable [] newOffhand;

        /**
         * The BetonQuest backpack, if changed.
         */
        private @Nullable List<ItemStack> newBackpack;

        private Session(final OnlineProfile profile, final Map<QuestItem, Integer> neededDeletions, final boolean abort) {
            this.profile = profile;
            this.player = profile.getPlayer();
            this.stillToTake = new HashMap<>(neededDeletions);
            this.abort = abort;
        }

        private void checkSelectedTypes() throws QuestException {
            for (final CheckType type : checkOrder.getValue(profile)) {
                switch (type) {
                    case INVENTORY -> checkInventory();
                    case ARMOR -> newArmor = takeDesiredAmount(player.getInventory().getArmorContents());
                    case MAINHAND -> checkMainHandEquipmentSlot();
                    case OFFHAND -> newOffhand = takeDesiredAmount(player.getInventory().getItemInOffHand());
                    case BACKPACK -> newBackpack = takeDesiredAmount(playerDataStorage.get(profile).getBackpack());
                }
                if (stillToTake.isEmpty()) {
                    break;
                }
            }

            setNewContents();
        }

        private void setNewContents() throws QuestException {
            if (!stillToTake.isEmpty()) {
                if (failActions != null) {
                    actionManager.run(profile, failActions.getValue(profile));
                }
                if (abort) {
                    return;
                }
            }
            if (newInventory != null) {
                player.getInventory().setStorageContents(newInventory);
            }
            if (newOffhand != null) {
                player.getInventory().setItemInOffHand(newOffhand[0]);
            }
            if (newArmor != null) {
                player.getInventory().setArmorContents(newArmor);
            }
            if (newBackpack != null) {
                playerDataStorage.get(profile).setBackpack(newBackpack);
            }
        }

        /**
         * Removes the items from the general inventory with respect to changes on the main hand.
         */
        private void checkInventory() {
            final ItemStack[] inventory = newInventory == null ? player.getInventory().getStorageContents() : newInventory;
            final ItemStack[] changed = takeDesiredAmount(inventory);
            if (changed != null) {
                newInventory = changed;
            }
        }

        /**
         * Removes the items from the main hand with respect to changes on the general inventory.
         */
        private void checkMainHandEquipmentSlot() {
            final ItemStack[] inventory = newInventory == null ? player.getInventory().getStorageContents() : newInventory;
            final int heldItemSlot = player.getInventory().getHeldItemSlot();
            final ItemStack[] changed = takeDesiredAmount(inventory[heldItemSlot]);
            if (changed != null) {
                if (newInventory == null) {
                    newInventory = inventory;
                }
                newInventory[heldItemSlot] = changed[0];
            }
        }

        /**
         * List to Array (and reverse) conversion for usage with the {@link #takeDesiredAmount(ItemStack...)} method.
         */
        @Nullable
        private List<ItemStack> takeDesiredAmount(final List<ItemStack> items) {
            final ItemStack[] itemArray = items.toArray(new ItemStack[0]);
            final ItemStack[] remainingItems = takeDesiredAmount(itemArray);
            if (remainingItems == null) {
                return null;
            }
            return Arrays.stream(remainingItems)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toCollection(ArrayList::new));
        }

        /**
         * Method to take the desired amount of items.
         *
         * @param items the items to take from
         * @return the remaining items after taking the desired amount, or null if no item was taken at all
         */
        @SuppressWarnings("PMD.CognitiveComplexity")
        private ItemStack @Nullable [] takeDesiredAmount(final ItemStack... items) {
            boolean changed = false;
            final Iterator<Map.Entry<QuestItem, Integer>> iterator = stillToTake.entrySet().iterator();
            while (iterator.hasNext()) {
                final Map.Entry<QuestItem, Integer> entry = iterator.next();
                final QuestItem questItem = entry.getKey();
                int desiredDeletions = entry.getValue();

                for (int i = 0; i < items.length && desiredDeletions > 0; i++) {
                    final ItemStack item = items[i];
                    if (item != null && questItem.matches(item)) {
                        changed = true;
                        if (item.getAmount() <= desiredDeletions) {
                            items[i] = null;
                            desiredDeletions -= item.getAmount();
                        } else {
                            item.subtract(desiredDeletions);
                            desiredDeletions = 0;
                        }
                    }
                }

                entry.setValue(desiredDeletions);
                if (desiredDeletions <= 0) {
                    iterator.remove();
                }
            }

            if (changed) {
                return items;
            }
            return null;
        }
    }
}
