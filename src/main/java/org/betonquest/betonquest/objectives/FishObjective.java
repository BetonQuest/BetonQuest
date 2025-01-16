package org.betonquest.betonquest.objectives;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.argument.VariableArgument;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.instruction.variable.location.VariableLocation;
import org.betonquest.betonquest.utils.BlockSelector;
import org.betonquest.betonquest.utils.PlayerConverter;
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
@SuppressWarnings("PMD.CommentRequired")
public class FishObjective extends CountingObjective implements Listener {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    private final BlockSelector blockSelector;

    @Nullable
    private final VariableLocation hookTargetLocation;

    @Nullable
    private final VariableNumber rangeVar;

    public FishObjective(final Instruction instruction) throws QuestException {
        super(instruction, "fish_to_catch");
        this.log = BetonQuest.getInstance().getLoggerFactory().create(getClass());
        blockSelector = new BlockSelector(instruction.next());
        targetAmount = instruction.get(VariableArgument.NUMBER_NOT_LESS_THAN_ONE);

        final String loc = instruction.getOptional("hookLocation");
        final String range = instruction.getOptional("range");
        if (loc != null && range != null) {
            hookTargetLocation = instruction.get(loc, VariableLocation::new);
            rangeVar = instruction.get(range, VariableNumber::new);
        } else {
            hookTargetLocation = null;
            rangeVar = null;
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onFishCatch(final PlayerFishEvent event) {
        if (event.getState() != State.CAUGHT_FISH) {
            return;
        }
        final OnlineProfile onlineProfile = PlayerConverter.getID(event.getPlayer());
        if (!containsPlayer(onlineProfile) || event.getCaught() == null || event.getCaught().getType() != EntityType.DROPPED_ITEM) {
            return;
        }
        if (isInvalidLocation(event, onlineProfile)) {
            return;
        }
        final ItemStack item = ((Item) event.getCaught()).getItemStack();
        if (blockSelector.match(item.getType()) && checkConditions(onlineProfile)) {
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
