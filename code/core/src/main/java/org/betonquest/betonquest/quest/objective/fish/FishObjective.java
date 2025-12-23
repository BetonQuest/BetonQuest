package org.betonquest.betonquest.quest.objective.fish;

import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.type.QuestItemWrapper;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerFishEvent.State;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * Requires the player to catch the fish.
 */
public class FishObjective extends CountingObjective implements Listener {

    /**
     * Item to catch.
     */
    private final Variable<QuestItemWrapper> item;

    /**
     * Location where the fish should be caught.
     */
    @Nullable
    private final Variable<Location> hookTargetLocation;

    /**
     * Range around the location where the fish should be caught.
     */
    @Nullable
    private final Variable<Number> rangeVar;

    /**
     * Constructor for the FishObjective.
     *
     * @param instruction        the instruction that created this objective
     * @param targetAmount       the target amount of fish to catch
     * @param item               the item to catch
     * @param hookTargetLocation the location where the fish should be caught
     * @param rangeVar           the range around the location where the item should be fished
     * @throws QuestException if there is an error in the instruction
     */
    public FishObjective(final Instruction instruction, final Variable<Number> targetAmount,
                         final Variable<QuestItemWrapper> item, @Nullable final Variable<Location> hookTargetLocation,
                         @Nullable final Variable<Number> rangeVar) throws QuestException {
        super(instruction, targetAmount, "fish_to_catch");
        this.item = item;
        this.hookTargetLocation = hookTargetLocation;
        this.rangeVar = rangeVar;
    }

    /**
     * Check if the fish was caught in the right location.
     *
     * @param event the event that was triggered
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onFishCatch(final PlayerFishEvent event) {
        if (event.getState() != State.CAUGHT_FISH) {
            return;
        }
        final OnlineProfile onlineProfile = profileProvider.getProfile(event.getPlayer());
        if (!containsPlayer(onlineProfile) || event.getCaught() == null || event.getCaught().getType() != EntityType.DROPPED_ITEM) {
            return;
        }
        qeHandler.handle(() -> {
            if (isInvalidLocation(event, onlineProfile)) {
                return;
            }
            final ItemStack item = ((org.bukkit.entity.Item) event.getCaught()).getItemStack();
            if (this.item.getValue(onlineProfile).matches(item, onlineProfile) && checkConditions(onlineProfile)) {
                getCountingData(onlineProfile).progress(item.getAmount());
                completeIfDoneOrNotify(onlineProfile);
            }
        });
    }

    private boolean isInvalidLocation(final PlayerFishEvent event, final Profile profile) throws QuestException {
        if (hookTargetLocation == null || rangeVar == null) {
            return false;
        }

        final Location targetLocation = hookTargetLocation.getValue(profile);
        final double range = rangeVar.getValue(profile).doubleValue();
        final Location hookLocation = event.getHook().getLocation();
        return !hookLocation.getWorld().equals(targetLocation.getWorld()) || targetLocation.distanceSquared(hookLocation) > range * range;
    }
}
