package org.betonquest.betonquest.quest.objective.pickup;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.Item;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
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
    private final List<Item> pickupItems;

    /**
     * Constructor for the PickupObjective.
     *
     * @param instruction  the instruction that created this objective
     * @param pickupItems  the items to be picked up
     * @param targetAmount the target amount of items to be picked up
     * @throws QuestException if there is an error in the instruction
     */
    public PickupObjective(final Instruction instruction, final List<Item> pickupItems, final VariableNumber targetAmount) throws QuestException {
        super(instruction, "items_to_pickup");
        this.pickupItems = pickupItems;
        this.targetAmount = targetAmount;
    }

    /**
     * Handles when the player picks up an item.
     *
     * @param event The event object.
     */
    @EventHandler(ignoreCancelled = true)
    public void onPickup(final EntityPickupItemEvent event) {
        if (isValidItem(event.getItem().getItemStack()) && event.getEntity() instanceof Player) {
            final OnlineProfile onlineProfile = profileProvider.getProfile((Player) event.getEntity());
            if (containsPlayer(onlineProfile) && checkConditions(onlineProfile)) {
                final ItemStack pickupItem = event.getItem().getItemStack();
                getCountingData(onlineProfile).progress(pickupItem.getAmount());
                completeIfDoneOrNotify(onlineProfile);
            }
        }
    }

    private boolean isValidItem(final ItemStack itemStack) {
        for (final Item item : pickupItems) {
            if (item.matches(itemStack)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void start() {
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
    }

    @Override
    public void stop() {
        HandlerList.unregisterAll(this);
    }
}
