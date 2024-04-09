package org.betonquest.betonquest.objectives;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.BlockSelector;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.betonquest.betonquest.utils.location.CompoundLocation;
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

    private final CompoundLocation location;

    private final boolean hasLocation;

    private final boolean ignorecancel;

    public BlockObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction);
        selector = instruction.getBlockSelector();
        exactMatch = instruction.hasArgument("exactMatch");
        targetAmount = instruction.getVarNum();
        noSafety = instruction.hasArgument("noSafety");
        final String stringlocation = instruction.getOptional("loc", "false");
        location = "false".equalsIgnoreCase(stringlocation) ? null : new CompoundLocation(instruction.getPackage(), stringlocation);
        hasLocation = !"false".equalsIgnoreCase(stringlocation);
        ignorecancel = instruction.hasArgument("ignorecancel");

    }

    @Override
    public String getDefaultDataInstruction(final Profile profile) {
        return String.valueOf(targetAmount.getInt(profile));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(final BlockPlaceEvent event) throws QuestRuntimeException {
        final OnlineProfile onlineProfile = PlayerConverter.getID(event.getPlayer());
        if (containsPlayer(onlineProfile) && selector.match(event.getBlock(), exactMatch) && checkConditions(onlineProfile)) {
            if (hasLocation && !event.getBlock().getLocation().equals(location.getLocation(onlineProfile))) {
                return;
            }
            if (getCountingData(onlineProfile).getDirectionFactor() < 0 && noSafety) {
                return;
            }
            handleDataChange(onlineProfile, getCountingData(onlineProfile).add(), event.isCancelled());
        }

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(final BlockBreakEvent event) throws QuestRuntimeException {
        final OnlineProfile onlineProfile = PlayerConverter.getID(event.getPlayer());
        if (containsPlayer(onlineProfile) && selector.match(event.getBlock(), exactMatch) && checkConditions(onlineProfile)) {
            if (hasLocation && !event.getBlock().getLocation().equals(location.getLocation(onlineProfile))) {
                return;
            }
            if (getCountingData(onlineProfile).getDirectionFactor() > 0 && noSafety) {
                return;
            }
            handleDataChange(onlineProfile, getCountingData(onlineProfile).subtract(), event.isCancelled());

        }
    }

    private void handleDataChange(final OnlineProfile onlineProfile, final CountingData data, final boolean cancel) {
        if (cancel && !ignorecancel) {
            return;
        }
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
