package org.betonquest.betonquest.quest.objective.fish;

import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.type.ItemWrapper;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.objective.event.ObjectiveFactoryService;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerFishEvent.State;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * Requires the player to catch the fish.
 */
public class FishObjective extends CountingObjective {

    /**
     * Item to catch.
     */
    private final Argument<ItemWrapper> item;

    /**
     * Location where the fish should be caught.
     */
    @Nullable
    private final Argument<Location> hookTargetLocation;

    /**
     * Range around the location where the fish should be caught.
     */
    @Nullable
    private final Argument<Number> range;

    /**
     * Constructor for the FishObjective.
     *
     * @param service            the objective factory service
     * @param targetAmount       the target amount of fish to catch
     * @param item               the item to catch
     * @param hookTargetLocation the location where the fish should be caught
     * @param range              the range around the location where the item should be fished
     * @throws QuestException if there is an error in the instruction
     */
    public FishObjective(final ObjectiveFactoryService service, final Argument<Number> targetAmount,
                         final Argument<ItemWrapper> item, @Nullable final Argument<Location> hookTargetLocation,
                         @Nullable final Argument<Number> range) throws QuestException {
        super(service, targetAmount, "fish_to_catch");
        this.item = item;
        this.hookTargetLocation = hookTargetLocation;
        this.range = range;
    }

    /**
     * Check if the fish was caught in the right location.
     *
     * @param event         the event that was triggered
     * @param onlineProfile the profile of the player that caught the fish
     */
    public void onFishCatch(final PlayerFishEvent event, final OnlineProfile onlineProfile) {
        if (event.getState() != State.CAUGHT_FISH) {
            return;
        }
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
        if (hookTargetLocation == null || range == null) {
            return false;
        }

        final Location targetLocation = hookTargetLocation.getValue(profile);
        final double range = this.range.getValue(profile).doubleValue();
        final Location hookLocation = event.getHook().getLocation();
        return !hookLocation.getWorld().equals(targetLocation.getWorld()) || targetLocation.distanceSquared(hookLocation) > range * range;
    }
}
