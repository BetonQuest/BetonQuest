package org.betonquest.betonquest.objectives;

import lombok.CustomLog;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.utils.BlockSelector;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.block.data.Ageable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * Player has to break/place specified amount of blocks. Doing opposite thing
 * (breaking when should be placing) will not count the progress.
 */
@SuppressWarnings({"PMD.CommentRequired", "PMD.AvoidDuplicateLiterals", "PMD.AvoidLiteralsInIfCondition"})
@CustomLog
public class BlockObjective extends CountingObjective implements Listener {

    private final BlockSelector selector;
    private final boolean exactMatch;
    private final boolean blockBreak;

    public BlockObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction);
        selector = instruction.getBlockSelector();
        exactMatch = instruction.hasArgument("exactMatch");
        blockBreak = instruction.hasArgument("break");
        targetAmount = instruction.getInt();

        if (targetAmount < 1){
            throw new InstructionParseException("the amount cannot be less than 1");
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockPlace(final BlockPlaceEvent event) {
        final String playerID = PlayerConverter.getID(event.getPlayer());
        if (containsPlayer(playerID)
                && selector.match(event.getBlock(), exactMatch)
                && checkConditions(playerID)
                && !blockBreak) {
            handleDataChange(playerID);
        }

        if(blockBreak
                && selector.match(event.getBlock(), exactMatch)
                && checkConditions(playerID)
                && !(event.getBlockPlaced().getBlockData() instanceof Ageable)) {
            event.getBlockPlaced().setMetadata("PlayerPlaced", new FixedMetadataValue(BetonQuest.getInstance(), playerID));
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockBreak(final BlockBreakEvent event) {
        final String playerID = PlayerConverter.getID(event.getPlayer());
        if (blockBreak
                && containsPlayer(playerID)
                && selector.match(event.getBlock(), exactMatch)
                && checkConditions(playerID)
                && !event.getBlock().hasMetadata("PlayerPlaced")) {
            handleDataChange(playerID);
        }
    }

    private void handleDataChange(final String playerID) {
        final String message = blockBreak ? "blocks_to_break" : "blocks_to_place";
        getCountingData(playerID).progress();
        completeIfDoneOrNotify(playerID, message);
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
