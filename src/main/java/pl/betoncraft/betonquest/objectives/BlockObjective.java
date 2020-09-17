package pl.betoncraft.betonquest.objectives;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.utils.BlockSelector;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Player has to break/place specified amount of blocks. Doing opposite thing
 * (breaking when should be placing) will reverse the progress.
 */
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
        notify = instruction.hasArgument("notify") || notifyInterval > 0;
    }

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
                    Config.sendNotify(playerID, "blocks_to_break",
                            new String[]{String.valueOf(playerData.getAmount() - neededAmount)},
                            "blocks_to_break,info");
                } else {
                    Config.sendNotify(playerID, "blocks_to_place",
                            new String[]{String.valueOf(neededAmount - playerData.getAmount())},
                            "blocks_to_place,info");
                }
            }
        }
    }

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
                    Config.sendNotify(playerID, "blocks_to_break",
                            new String[]{String.valueOf(playerData.getAmount() - neededAmount)},
                            "blocks_to_break,info");
                } else {
                    Config.sendNotify(playerID, "blocks_to_place",
                            new String[]{String.valueOf(neededAmount - playerData.getAmount())},
                            "blocks_to_place,info");
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
        if (name.equalsIgnoreCase("left")) {
            return Integer.toString(neededAmount - ((BlockData) dataMap.get(playerID)).getAmount());
        } else if (name.equalsIgnoreCase("amount")) {
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
