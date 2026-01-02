package org.betonquest.betonquest.quest.objective.block;

import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.type.BlockSelector;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.quest.event.IngameNotificationSender;
import org.bukkit.Location;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.jetbrains.annotations.Nullable;

/**
 * Player has to break/place specified amount of blocks. Doing opposite thing
 * (breaking when should be placing) will reverse the progress.
 */
public class BlockObjective extends CountingObjective {

    /**
     * Block Selector parameter.
     */
    private final Argument<BlockSelector> selector;

    /**
     * Optional exactMatch parameter.
     */
    private final FlagArgument<Boolean> exactMatch;

    /**
     * Optional noSafety parameter.
     */
    private final FlagArgument<Boolean> noSafety;

    /**
     * Optional location parameter.
     */
    @Nullable
    private final Argument<Location> location;

    /**
     * Optional region parameter. Used together with {@link #location} to form a cuboid region.
     */
    @Nullable
    private final Argument<Location> region;

    /**
     * Optional ignorecancel parameter.
     */
    private final FlagArgument<Boolean> ignoreCancel;

    /**
     * Notification sender for block break.
     */
    private final IngameNotificationSender blockBreakSender;

    /**
     * Notification sender for block place.
     */
    private final IngameNotificationSender blockPlaceSender;

    /**
     * Constructor for the BlockObjective.
     *
     * @param instruction      the instruction that created this objective
     * @param targetAmount     the target amount of blocks to break/place
     * @param selector         the block selector to match blocks
     * @param exactMatch       the exact match flag
     * @param noSafety         the no safety flag
     * @param location         the location of the block
     * @param region           the second location defining a region
     * @param ignoreCancel     the ignore cancel flag
     * @param blockBreakSender the notification sender for block break
     * @param blockPlaceSender the notification sender for block place
     * @throws QuestException if there is an error in the instruction
     */
    @SuppressWarnings("PMD.ExcessiveParameterList")
    public BlockObjective(final Instruction instruction, final Argument<Number> targetAmount,
                          final Argument<BlockSelector> selector, final FlagArgument<Boolean> exactMatch,
                          final FlagArgument<Boolean> noSafety, @Nullable final Argument<Location> location,
                          @Nullable final Argument<Location> region, final FlagArgument<Boolean> ignoreCancel,
                          final IngameNotificationSender blockBreakSender, final IngameNotificationSender blockPlaceSender) throws QuestException {
        super(instruction, targetAmount, null);
        this.selector = selector;
        this.exactMatch = exactMatch;
        this.noSafety = noSafety;
        this.location = location;
        this.region = region;
        this.ignoreCancel = ignoreCancel;
        this.blockBreakSender = blockBreakSender;
        this.blockPlaceSender = blockPlaceSender;
    }

    /**
     * Check if the placed block is the right one.
     *
     * @param event         the event that triggered this method
     * @param onlineProfile the profile of the player that placed the block
     */
    public void onBlockPlace(final BlockPlaceEvent event, final OnlineProfile onlineProfile) {
        qeHandler.handle(() -> {
            if (event.isCancelled() && !ignoreCancel.getValue(onlineProfile).orElse(false)) {
                return;
            }
            final BlockSelector blockSelector = selector.getValue(onlineProfile);
            if (containsPlayer(onlineProfile)
                    && blockSelector.match(event.getBlock(), exactMatch.getValue(onlineProfile).orElse(false))
                    && checkConditions(onlineProfile)
                    && checkLocation(event.getBlock().getLocation(), onlineProfile)
                    && (getCountingData(onlineProfile).getDirectionFactor() >= 0 || !noSafety.getValue(onlineProfile).orElse(false))) {
                handleDataChange(onlineProfile, getCountingData(onlineProfile).add());
            }
        });
    }

    /**
     * Check if the broken block is the right one.
     *
     * @param event         the event that triggered this method
     * @param onlineProfile the profile of the player that broke the block
     */
    public void onBlockBreak(final BlockBreakEvent event, final OnlineProfile onlineProfile) {
        qeHandler.handle(() -> {
            if (event.isCancelled() && !ignoreCancel.getValue(onlineProfile).orElse(false)) {
                return;
            }
            final BlockSelector blockSelector = selector.getValue(onlineProfile);
            if (containsPlayer(onlineProfile)
                    && blockSelector.match(event.getBlock(), exactMatch.getValue(onlineProfile).orElse(false))
                    && checkConditions(onlineProfile)
                    && checkLocation(event.getBlock().getLocation(), onlineProfile)
                    && (getCountingData(onlineProfile).getDirectionFactor() <= 0 || !noSafety.getValue(onlineProfile).orElse(false))) {
                handleDataChange(onlineProfile, getCountingData(onlineProfile).subtract());
            }
        });
    }

    private void handleDataChange(final OnlineProfile onlineProfile, final CountingData data) {
        final IngameNotificationSender message = data.getDirectionFactor() > 0 ? blockPlaceSender : blockBreakSender;
        completeIfDoneOrNotify(onlineProfile, message);
    }

    private boolean checkLocation(final Location loc, final Profile profile) throws QuestException {
        if (location != null) {
            if (region != null) {
                return isInRange(loc, profile, location, region);
            }
            return loc.getBlock().getLocation().equals(location.getValue(profile));
        }
        return true;
    }

    private boolean isInRange(final Location loc, final Profile profile, final Argument<Location> location, final Argument<Location> region) throws QuestException {
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
