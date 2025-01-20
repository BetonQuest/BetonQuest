package org.betonquest.betonquest.objective;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.instruction.variable.location.VariableLocation;
import org.betonquest.betonquest.util.BlockSelector;
import org.betonquest.betonquest.util.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.jetbrains.annotations.Nullable;

/**
 * Player has to break/place specified amount of blocks. Doing opposite thing
 * (breaking when should be placing) will reverse the progress.
 */
@SuppressWarnings({"PMD.CommentRequired", "PMD.AvoidDuplicateLiterals"})
public class BlockObjective extends CountingObjective implements Listener {
    /**
     * Blockselector parameter.
     */
    private final BlockSelector selector;

    /**
     * Optional exactMatch parameter.
     */
    private final boolean exactMatch;

    /**
     * Optional noSafety parameter.
     */
    private final boolean noSafety;

    /**
     * Optional location parameter.
     */
    @Nullable
    private final VariableLocation location;

    /**
     * Optional region parameter. Used together with {@link #location} to form a cuboid region.
     */
    @Nullable
    private final VariableLocation region;

    /**
     * Optional ignorecancel parameter.
     */
    private final boolean ignorecancel;

    /**
     * Logger for exception handling.
     */
    private final BetonQuestLogger logger;

    public BlockObjective(final Instruction instruction) throws QuestException {
        super(instruction);
        logger = BetonQuest.getInstance().getLoggerFactory().create(BetonQuest.getInstance());
        selector = instruction.get(BlockSelector::new);
        exactMatch = instruction.hasArgument("exactMatch");
        targetAmount = instruction.get(VariableNumber::new);
        noSafety = instruction.hasArgument("noSafety");
        location = instruction.get(instruction.getOptional("loc"), VariableLocation::new);
        region = instruction.get(instruction.getOptional("region"), VariableLocation::new);
        ignorecancel = instruction.hasArgument("ignorecancel");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(final BlockPlaceEvent event) {
        if (event.isCancelled() && !ignorecancel) {
            return;
        }
        final OnlineProfile onlineProfile = PlayerConverter.getID(event.getPlayer());
        if (containsPlayer(onlineProfile) && selector.match(event.getBlock(), exactMatch) && checkConditions(onlineProfile)) {
            if (!checkLocation(event.getBlock().getLocation(), onlineProfile)) {
                return;
            }
            if (getCountingData(onlineProfile).getDirectionFactor() < 0 && noSafety) {
                return;
            }
            handleDataChange(onlineProfile, getCountingData(onlineProfile).add());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(final BlockBreakEvent event) {
        if (event.isCancelled() && !ignorecancel) {
            return;
        }
        final OnlineProfile onlineProfile = PlayerConverter.getID(event.getPlayer());
        if (containsPlayer(onlineProfile) && selector.match(event.getBlock(), exactMatch) && checkConditions(onlineProfile)) {
            if (!checkLocation(event.getBlock().getLocation(), onlineProfile)) {
                return;
            }
            if (getCountingData(onlineProfile).getDirectionFactor() > 0 && noSafety) {
                return;
            }
            handleDataChange(onlineProfile, getCountingData(onlineProfile).subtract());
        }
    }

    private void handleDataChange(final OnlineProfile onlineProfile, final CountingData data) {
        final String message = data.getDirectionFactor() > 0 ? "blocks_to_place" : "blocks_to_break";
        completeIfDoneOrNotify(onlineProfile, message);
    }

    @Override
    public void start() {
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
    }

    @Override
    public void stop() {
        HandlerList.unregisterAll(this);
    }

    private boolean checkLocation(final Location loc, final Profile profile) {
        try {
            if (location != null) {
                if (region != null) {
                    return isInRange(loc, profile, location, region);
                }
                return loc.getBlock().getLocation().equals(location.getValue(profile));
            }
        } catch (final QuestException e) {
            logger.error(instruction.getPackage(), e.getMessage());
            return false;
        }
        return true;
    }

    private boolean isInRange(final Location loc, final Profile profile, final VariableLocation location, final VariableLocation region) throws QuestException {
        final Location loc1 = location.getValue(profile);
        final Location loc2 = region.getValue(profile);
        return inBetween(loc1, loc2, loc);
    }

    private boolean inBetween(final Location range1, final Location range2, final Location pos) {
        return inWorld(range1, range2, pos)
                && betweenCoordinates(range1.getBlockY(), range2.getBlockY(), pos.getBlockY())
                && betweenCoordinates(range1.getBlockZ(), range2.getBlockZ(), pos.getBlockZ())
                && betweenCoordinates(range1.getBlockX(), range2.getBlockX(), pos.getBlockX());
    }

    private boolean betweenCoordinates(final int range1, final int range2, final int pos) {
        return Integer.min(range1, range2) <= pos && pos <= Integer.max(range1, range2);
    }

    private boolean inWorld(final Location range1, final Location range2, final Location pos) {
        return range1.getWorld().equals(range2.getWorld()) && range2.getWorld().equals(pos.getWorld());
    }
}
