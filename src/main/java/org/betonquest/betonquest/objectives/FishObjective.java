package org.betonquest.betonquest.objectives;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.BetonQuestLogger;
import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.BlockSelector;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.betonquest.betonquest.utils.location.CompoundLocation;
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

/**
 * Requires the player to catch the fish.
 */
@SuppressWarnings("PMD.CommentRequired")
public class FishObjective extends CountingObjective implements Listener {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private static final BetonQuestLogger LOG = BetonQuestLogger.create();

    private final BlockSelector blockSelector;

    private final CompoundLocation hookTargetLocation;

    private final VariableNumber rangeVar;

    public FishObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction, "fish_to_catch");
        blockSelector = new BlockSelector(instruction.next());
        targetAmount = instruction.getInt();

        final QuestPackage pack = instruction.getPackage();
        final String loc = instruction.getOptional("hookLocation");
        final String range = instruction.getOptional("range");
        if (loc != null && range != null) {
            hookTargetLocation = new CompoundLocation(pack, loc);
            rangeVar = new VariableNumber(pack, range);
        } else {
            hookTargetLocation = null;
            rangeVar = null;
        }

        if (targetAmount <= 0) {
            throw new InstructionParseException("Fish amount cannot be less than 0");
        }
    }

    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
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
            targetLocation = hookTargetLocation.getLocation(profile);
        } catch (final QuestRuntimeException e) {
            LOG.warn(e.getMessage(), e);
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
