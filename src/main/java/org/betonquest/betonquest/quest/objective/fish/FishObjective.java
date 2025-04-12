package org.betonquest.betonquest.quest.objective.fish;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.instruction.variable.location.VariableLocation;
import org.betonquest.betonquest.item.QuestItem;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
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
     * Quest item to catch.
     */
    private final QuestItem questItem;

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
     * @param log                the logger for this objective
     * @param questItem          the quest item to catch
     * @param targetAmount       the target amount of fish to catch
     * @param hookTargetLocation the location where the fish should be caught
     * @param rangeVar           the range around the location where the item should be fished
     * @throws QuestException if there is an error in the instruction
     */
    public FishObjective(final Instruction instruction, final BetonQuestLogger log, final QuestItem questItem,
                         final VariableNumber targetAmount, @Nullable final VariableLocation hookTargetLocation,
                         @Nullable final VariableNumber rangeVar) throws QuestException {
        super(instruction, "fish_to_catch");
        this.log = log;
        this.questItem = questItem;
        this.targetAmount = targetAmount;
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
        if (isInvalidLocation(event, onlineProfile)) {
            return;
        }
        final ItemStack item = ((Item) event.getCaught()).getItemStack();
        if (questItem.matches(item) && checkConditions(onlineProfile)) {
            getCountingData(onlineProfile).progress(item.getAmount());
            completeIfDoneOrNotify(onlineProfile);
        }
    }

    private boolean isInvalidLocation(final PlayerFishEvent event, final Profile profile) {
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
        final int range = rangeVar.getInt(profile);
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
