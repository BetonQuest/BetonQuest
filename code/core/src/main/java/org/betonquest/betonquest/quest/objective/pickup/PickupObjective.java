package org.betonquest.betonquest.quest.objective.pickup;

import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.type.ItemWrapper;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.objective.event.ObjectiveFactoryService;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Represents an objective that is completed when a player picks up a specific item.
 */
public class PickupObjective extends CountingObjective {

    /**
     * The target amount of items to be picked up.
     */
    private final Argument<List<ItemWrapper>> pickupItems;

    /**
     * Constructor for the PickupObjective.
     *
     * @param service      the objective factory service
     * @param targetAmount the target amount of items to be picked up
     * @param pickupItems  the items to be picked up
     * @throws QuestException if there is an error in the instruction
     */
    public PickupObjective(final ObjectiveFactoryService service, final Argument<Number> targetAmount,
                           final Argument<List<ItemWrapper>> pickupItems) throws QuestException {
        super(service, targetAmount, "items_to_pickup");
        this.pickupItems = pickupItems;
    }

    /**
     * Handles when the player picks up an item.
     *
     * @param event         The event object.
     * @param onlineProfile The profile of the player that picked up the item.
     */
    public void onPickup(final EntityPickupItemEvent event, final OnlineProfile onlineProfile) {
        qeHandler.handle(() -> {
            if (containsPlayer(onlineProfile)
                    && isValidItem(onlineProfile, event.getItem().getItemStack())
                    && checkConditions(onlineProfile)) {
                final ItemStack pickupItem = event.getItem().getItemStack();
                getCountingData(onlineProfile).progress(pickupItem.getAmount());
                completeIfDoneOrNotify(onlineProfile);
            }
        });
    }

    private boolean isValidItem(final OnlineProfile onlineProfile, final ItemStack itemStack) throws QuestException {
        for (final ItemWrapper item : pickupItems.getValue(onlineProfile)) {
            if (item.matches(itemStack, onlineProfile)) {
                return true;
            }
        }
        return false;
    }
}
