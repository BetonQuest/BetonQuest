package org.betonquest.betonquest.quest.objective.fish;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.Item;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.instruction.variable.location.VariableLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
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
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * Item to catch.
     */
    private final Item item;

    /**
     * Location where the fish should be caught.
     */
    @Nullable
    private final VariableLocation hookTargetLocation;

    /**
     * Range around the location where the fish should be caught.
     */
    @Nullable
    private final VariableNumber rangeVar;

    /**
     * Constructor for the FishObjective.
     *
     * @param instruction        the instruction that created this objective
     * @param targetAmount       the target amount of fish to catch
     * @param log                the logger for this objective
     * @param item               the item to catch
     * @param hookTargetLocation the location where the fish should be caught
     * @param rangeVar           the range around the location where the item should be fished
     * @throws QuestException if there is an error in the instruction
     */
    public FishObjective(final Instruction instruction, final VariableNumber targetAmount, final BetonQuestLogger log,
                         final Item item, @Nullable final VariableLocation hookTargetLocation,
                         @Nullable final VariableNumber rangeVar) throws QuestException {
        super(instruction, targetAmount, "fish_to_catch");
        this.log = log;
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
        try {
            if (isInvalidLocation(event, onlineProfile)) {
                return;
            }
            final ItemStack item = ((org.bukkit.entity.Item) event.getCaught()).getItemStack();
            if (this.item.getItem().matches(item) && checkConditions(onlineProfile)) {
                getCountingData(onlineProfile).progress(item.getAmount());
                completeIfDoneOrNotify(onlineProfile);
            }
        } catch (final QuestException e) {
            log.warn(instruction.getPackage(), "Exception while processing Fish Objective: " + e.getMessage(), e);
        }
    }

    private boolean isInvalidLocation(final PlayerFishEvent event, final Profile profile) throws QuestException {
        if (hookTargetLocation == null || rangeVar == null) {
            return false;
        }

        final Location targetLocation;
        try {
            targetLocation = hookTargetLocation.getValue(profile);
        } catch (final QuestException e) {
            log.warn(e.getMessage(), e);
            return true;
        }
        final double range = rangeVar.getValue(profile).doubleValue();
        final Location hookLocation = event.getHook().getLocation();
        return !hookLocation.getWorld().equals(targetLocation.getWorld()) || targetLocation.distanceSquared(hookLocation) > range * range;
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
