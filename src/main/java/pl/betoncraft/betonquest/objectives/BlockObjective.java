/*
 * BetonQuest - advanced quests for Bukkit
 * Copyright (C) 2016  Jakub "Co0sh" Sapalski
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
 *
 * @author Jakub Sapalski
 */
public class BlockObjective extends Objective implements Listener {

    private final int neededAmount;
    private final boolean notify;
    private final int notifyInterval;
    private final BlockSelector selector;

    public BlockObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction);
        template = BlockData.class;
        selector = instruction.getBlockSelector();
        neededAmount = instruction.getInt();
        notifyInterval = instruction.getInt(instruction.getOptional("notify"), 1);
        notify = instruction.hasArgument("notify") || notifyInterval > 0;

        if (selector != null && !selector.isValid()) {
            throw new InstructionParseException("Invalid selector: " + selector.toString());
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockPlace(final BlockPlaceEvent event) {
        final String playerID = PlayerConverter.getID(event.getPlayer());
        // if the player has this objective, the event isn't canceled,
        // the block is correct and conditions are met
        if (containsPlayer(playerID) && selector.match(event.getBlock()) && checkConditions(playerID)) {
            // add the block to the total amount
            final BlockData playerData = (BlockData) dataMap.get(playerID);
            playerData.add();
            // complete the objective
            if (playerData.getAmount() == neededAmount) {
                completeObjective(playerID);
            } else if (notify && playerData.getAmount() % notifyInterval == 0) {
                // or maybe display a notification
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
        // if the player has this objective, the event isn't canceled,
        // the block is correct and conditions are met
        if (containsPlayer(playerID) && selector.match(event.getBlock()) && checkConditions(playerID)) {
            // remove the block from the total amount
            final BlockData playerData = (BlockData) dataMap.get(playerID);
            playerData.remove();
            // complete the objective
            if (playerData.getAmount() == neededAmount) {
                completeObjective(playerID);
            } else if (notify && playerData.getAmount() % notifyInterval == 0) {
                // or maybe display a notification
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
