package org.betonquest.betonquest.quest.event.take;

import org.apache.commons.lang3.tuple.Pair;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.instruction.Item;
import org.betonquest.betonquest.item.QuestItem;
import org.betonquest.betonquest.quest.event.NotificationSender;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Removes items from player's inventory and/or backpack.
 */
public class TakeEvent extends AbstractTakeEvent {

    /**
     * The items to be removed.
     */
    private final Item[] questItems;

    /**
     * A map to keep track of the needed deletions for each player.
     */
    private final Map<UUID, Pair<QuestItem, Integer>> neededDeletions = new ConcurrentHashMap<>();

    /**
     * Constructs a new TakeEvent.
     *
     * @param questItems         the items to be removed
     * @param checkOrder         the order in which the checks should be performed
     * @param notificationSender the notification sender to use
     */
    public TakeEvent(final Item[] questItems, final List<CheckType> checkOrder, final NotificationSender notificationSender) {
        super(checkOrder, notificationSender);
        this.questItems = questItems.clone();
    }

    @Override
    public void execute(final OnlineProfile profile) throws QuestException {
        for (final Item item : questItems) {
            final QuestItem questItem = item.getItem();
            final int deleteAmount = item.getAmount().getValue(profile).intValue();
            neededDeletions.put(profile.getProfileUUID(), Pair.of(questItem, deleteAmount));

            checkSelectedTypes(profile);
            final String itemName = questItem.getName() == null
                    ? new ItemStack(questItem.getMaterial()).getItemMeta().getDisplayName()
                    : questItem.getName();
            notificationSender.sendNotification(profile, itemName, String.valueOf(deleteAmount - neededDeletions.get(profile.getProfileUUID()).getRight()));
        }
    }

    @Override
    protected ItemStack[] takeDesiredAmount(final Profile profile, final ItemStack... items) {
        final Pair<QuestItem, Integer> pair = Objects.requireNonNull(neededDeletions.get(profile.getProfileUUID()));
        final QuestItem questItem = pair.getLeft();
        int desiredDeletions = pair.getRight();

        for (int i = 0; i < items.length && desiredDeletions > 0; i++) {
            final ItemStack item = items[i];
            if (item != null && questItem.compare(item)) {
                if (item.getAmount() <= desiredDeletions) {
                    items[i] = null;
                    desiredDeletions = desiredDeletions - item.getAmount();
                } else {
                    item.setAmount(item.getAmount() - desiredDeletions);
                    desiredDeletions = 0;
                }
            }
        }

        neededDeletions.put(profile.getProfileUUID(), Pair.of(questItem, desiredDeletions));
        return items;
    }
}
