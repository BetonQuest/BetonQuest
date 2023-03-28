package org.betonquest.betonquest.objectives;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.profiles.Profile;
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
public class BlockObjective extends CountingObjective implements Listener {
    private final BlockSelector selector;

    private final boolean exactMatch;

    private final boolean noSafety;

    public BlockObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction);
        selector = instruction.getBlockSelector();
        exactMatch = instruction.hasArgument("exactMatch");
        targetAmount = instruction.getVarNum();
        noSafety = instruction.hasArgument("noSafety");
    }

    @Override
    public String getDefaultDataInstruction(final Profile profile) {
        return String.valueOf(targetAmount.getInt(profile));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockPlace(final BlockPlaceEvent event) {
        final OnlineProfile onlineProfile = PlayerConverter.getID(event.getPlayer());
        if (containsPlayer(onlineProfile) && selector.match(event.getBlock(), exactMatch) && checkConditions(onlineProfile)) {
            if (getCountingData(onlineProfile).getDirectionFactor() < 0 && noSafety) {
                return;
            }
            handleDataChange(onlineProfile, getCountingData(onlineProfile).add());
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockBreak(final BlockBreakEvent event) {
        final OnlineProfile onlineProfile = PlayerConverter.getID(event.getPlayer());
        if (containsPlayer(onlineProfile) && selector.match(event.getBlock(), exactMatch) && checkConditions(onlineProfile)) {
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

}
