package org.betonquest.betonquest.objectives;

import lombok.CustomLog;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
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
public class BlockObjective extends Objective implements Listener {

    private final int neededAmount;
    private final boolean notify;
    private final int notifyInterval;
    private final BlockSelector selector;
    private final boolean exactMatch;

    public BlockObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction);
        template = BlockData.class;
        selector = instruction.getBlockSelector();
        exactMatch = instruction.hasArgument("exactMatch");
        neededAmount = instruction.getInt();
        notifyInterval = instruction.getInt(instruction.getOptional("notify"), 1);
        notify = instruction.hasArgument("notify") || notifyInterval > 1;
    }

    @SuppressWarnings("PMD.CyclomaticComplexity")
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockPlace(final BlockPlaceEvent event) {
        final String playerID = PlayerConverter.getID(event.getPlayer());
        if (containsPlayer(playerID) && selector.match(event.getBlock(), exactMatch) && checkConditions(playerID)) {
            final BlockData playerData = (BlockData) dataMap.get(playerID);
            playerData.add();
            if (playerData.getAmount() == neededAmount) {
                completeObjective(playerID);
            } else if (notify && playerData.getAmount() % notifyInterval == 0) {
                if (playerData.getAmount() > neededAmount) {
                    try {
                        Config.sendNotify(instruction.getPackage().getName(), playerID, "blocks_to_break",
                                new String[]{String.valueOf(playerData.getAmount() - neededAmount)},
                                "blocks_to_break,info");
                    } catch (final QuestRuntimeException exception) {
                        try {
                            LOG.warning(instruction.getPackage(), "The notify system was unable to play a sound for the 'blocks_to_break' category in '" + instruction.getObjective().getFullID() + "'. Error was: '" + exception.getMessage() + "'");
                        } catch (final InstructionParseException e) {
                            LOG.reportException(instruction.getPackage(), e);
                        }
                    }
                } else {
                    try {
                        Config.sendNotify(instruction.getPackage().getName(), playerID, "blocks_to_place",
                                new String[]{String.valueOf(neededAmount - playerData.getAmount())},
                                "blocks_to_place,info");
                    } catch (final QuestRuntimeException exception) {
                        try {
                            LOG.warning(instruction.getPackage(), "The notify system was unable to play a sound for the 'blocks_to_place' category in '" + instruction.getObjective().getFullID() + "'. Error was: '" + exception.getMessage() + "'");
                        } catch (final InstructionParseException e) {
                            LOG.reportException(instruction.getPackage(), e);
                        }
                    }
                }
            }
        }
    }

    @SuppressWarnings("PMD.CyclomaticComplexity")
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockBreak(final BlockBreakEvent event) {
        final String playerID = PlayerConverter.getID(event.getPlayer());
        if (containsPlayer(playerID) && selector.match(event.getBlock(), exactMatch) && checkConditions(playerID)) {
            final BlockData playerData = (BlockData) dataMap.get(playerID);
            playerData.remove();
            if (playerData.getAmount() == neededAmount) {
                completeObjective(playerID);
            } else if (notify && playerData.getAmount() % notifyInterval == 0) {
                if (playerData.getAmount() > neededAmount) {
                    try {
                        Config.sendNotify(instruction.getPackage().getName(), playerID, "blocks_to_break",
                                new String[]{String.valueOf(playerData.getAmount() - neededAmount)},
                                "blocks_to_break,info");
                    } catch (final QuestRuntimeException exception) {
                        try {
                            LOG.warning(instruction.getPackage(), "The notify system was unable to play a sound for the 'blocks_to_break' category in '" + instruction.getObjective().getFullID() + "'. Error was: '" + exception.getMessage() + "'");
                        } catch (final InstructionParseException e) {
                            LOG.reportException(instruction.getPackage(), e);
                        }
                    }
                } else {
                    try {
                        Config.sendNotify(instruction.getPackage().getName(), playerID, "blocks_to_place",
                                new String[]{String.valueOf(neededAmount - playerData.getAmount())},
                                "blocks_to_place,info");
                    } catch (final QuestRuntimeException exception) {
                        try {
                            LOG.warning(instruction.getPackage(), "The notify system was unable to play a sound for the 'blocks_to_place' category in '" + instruction.getObjective().getFullID() + "'. Error was: '" + exception.getMessage() + "'");
                        } catch (final InstructionParseException e) {
                            LOG.reportException(instruction.getPackage(), e);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void start() {
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
    }

    @Override
    public void stop() {
        HandlerList.unregisterAll(this);
    }

    @Override
    public String getDefaultDataInstruction() {
        return "0";
    }

    @Override
    public String getProperty(final String name, final String playerID) {
        if ("left".equalsIgnoreCase(name)) {
            return Integer.toString(neededAmount - ((BlockData) dataMap.get(playerID)).getAmount());
        } else if ("amount".equalsIgnoreCase(name)) {
            return Integer.toString(((BlockData) dataMap.get(playerID)).getAmount());
        }
        return "";
    }

    public static class BlockData extends ObjectiveData {

        private int amount;

        public BlockData(final String instruction, final String playerID, final String objID) {
            super(instruction, playerID, objID);
            amount = Integer.parseInt(instruction);
        }

        private void add() {
            amount++;
            update();
        }

        private void remove() {
            amount--;
            update();
        }

        private int getAmount() {
            return amount;
        }

        @Override
        public String toString() {
            return String.valueOf(amount);
        }
    }
}
