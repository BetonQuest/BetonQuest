package org.betonquest.betonquest.quest.event.take;

import net.kyori.adventure.text.Component;
import org.apache.commons.lang3.tuple.Pair;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.common.component.VariableReplacement;
import org.betonquest.betonquest.api.instruction.type.ItemWrapper;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.item.QuestItem;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
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
    private final Variable<List<ItemWrapper>> questItems;

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
    public TakeEvent(final Variable<List<ItemWrapper>> questItems, final List<CheckType> checkOrder, final NotificationSender notificationSender) {
        super(checkOrder, notificationSender);
        this.questItems = questItems;
    }

    @Override
    public void execute(final OnlineProfile profile) throws QuestException {
        for (final ItemWrapper item : questItems.getValue(profile)) {
            final QuestItem questItem = item.getItem(profile);
            final int deleteAmount = item.getAmount().getValue(profile).intValue();
            neededDeletions.put(profile.getProfileUUID(), Pair.of(questItem, deleteAmount));

            checkSelectedTypes(profile);
            notificationSender.sendNotification(profile,
                    new VariableReplacement("item", questItem.getName()),
                    new VariableReplacement("amount", Component.text(deleteAmount - neededDeletions.get(profile.getProfileUUID()).getRight())));
        }
    }

    @Override
    protected ItemStack[] takeDesiredAmount(final Profile profile, final ItemStack... items) {
        final Pair<QuestItem, Integer> pair = Objects.requireNonNull(neededDeletions.get(profile.getProfileUUID()));
        final QuestItem questItem = pair.getLeft();
        int desiredDeletions = pair.getRight();

        for (int i = 0; i < items.length && desiredDeletions > 0; i++) {
            final ItemStack item = items[i];
            if (item != null && questItem.matches(item)) {
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
