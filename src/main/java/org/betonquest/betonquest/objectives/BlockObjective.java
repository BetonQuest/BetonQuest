package org.betonquest.betonquest.objectives;

import lombok.CustomLog;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.utils.BlockSelector;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

/**
 * Player has to break/place specified amount of blocks. Doing opposite thing
 * (breaking when should be placing) will reverse the progress.
 */
@SuppressWarnings({"PMD.CommentRequired", "PMD.AvoidDuplicateLiterals"})
@CustomLog
public class BlockObjective extends CountingObjective implements Listener {

    private final BlockSelector selector;
    private final boolean exactMatch;
    private final boolean noSafety;

    public BlockObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction);
        selector = instruction.getBlockSelector();
        exactMatch = instruction.hasArgument("exactMatch");
        targetAmount = instruction.getInt();
        noSafety = instruction.hasArgument("noSafety");
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockPlace(final BlockPlaceEvent event) {
        final OnlineProfile profile = PlayerConverter.getID(event.getPlayer());
        if (containsPlayer(profile) && selector.match(event.getBlock(), exactMatch) && checkConditions(profile)) {
            if (getCountingData(profile).getDirectionFactor() < 0 && noSafety) {
                return;
            }
            handleDataChange(profile, getCountingData(profile).add());
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockBreak(final BlockBreakEvent event) {
        final OnlineProfile profile = PlayerConverter.getID(event.getPlayer());
        if (containsPlayer(profile) && selector.match(event.getBlock(), exactMatch) && checkConditions(profile)) {
            if (getCountingData(profile).getDirectionFactor() > 0 && noSafety) {
                return;
            }
            handleDataChange(profile, getCountingData(profile).subtract());
        }
    }

    private void handleDataChange(final OnlineProfile profile, final CountingData data) {
        final String message = data.getDirectionFactor() > 0 ? "blocks_to_place" : "blocks_to_break";
        completeIfDoneOrNotify(profile, message);
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
