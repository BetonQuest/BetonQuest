package org.betonquest.betonquest.quest.objective.pickup;

import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.type.ItemWrapper;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Represents an objective that is completed when a player picks up a specific item.
 */
public class PickupObjective extends CountingObjective implements Listener {

    /**
     * The target amount of items to be picked up.
     */
    private final Variable<List<ItemWrapper>> pickupItems;

    /**
     * Constructor for the PickupObjective.
     *
     * @param instruction  the instruction that created this objective
     * @param targetAmount the target amount of items to be picked up
     * @param pickupItems  the items to be picked up
     * @throws QuestException if there is an error in the instruction
     */
    public PickupObjective(final Instruction instruction, final Variable<Number> targetAmount,
                           final Variable<List<ItemWrapper>> pickupItems) throws QuestException {
        super(instruction, targetAmount, "items_to_pickup");
        this.pickupItems = pickupItems;
    }

    /**
     * Handles when the player picks up an item.
     *
     * @param event The event object.
     */
    @EventHandler(ignoreCancelled = true)
    public void onPickup(final EntityPickupItemEvent event) {
        if (event.getEntity() instanceof final Player player) {
            final OnlineProfile onlineProfile = profileProvider.getProfile(player);
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
