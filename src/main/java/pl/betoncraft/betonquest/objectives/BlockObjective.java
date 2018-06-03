/**
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
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Player has to break/place specified amount of blocks. Doing opposite thing
 * (breaking when should be placing) will reverse the progress.
 * 
 * @author Jakub Sapalski
 */
@SuppressWarnings("deprecation")
public class BlockObjective extends Objective implements Listener {

	private final Material material;
	private final byte data;
	private final int neededAmount;
	private final boolean notify;
	private final int notifyInterval;

	public BlockObjective(Instruction instruction) throws InstructionParseException {
		super(instruction);
		template = BlockData.class;
		String[] string = instruction.next().split(":");
		material = instruction.getMaterial(string[0]);
		data = string.length > 1 ? instruction.getByte(string[1], (byte) -1) : -1;
		neededAmount = instruction.getInt();
		notifyInterval = instruction.getInt(instruction.getOptional("notify"), 1);
		notify = instruction.hasArgument("notify") || notifyInterval > 1;
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		String playerID = PlayerConverter.getID(event.getPlayer());
		// if the player has this objective, the event isn't canceled,
		// the block is correct and conditions are met
		if (containsPlayer(playerID) && !event.isCancelled() && event.getBlock().getType().equals(material)
				&& (data < 0 || event.getBlock().getData() == data) && checkConditions(playerID)) {
			// add the block to the total amount
			BlockData playerData = (BlockData) dataMap.get(playerID);
			playerData.add();
			// complete the objective
			if (playerData.getAmount() == neededAmount) {
				completeObjective(playerID);
			} else if (notify && playerData.getAmount() % notifyInterval == 0) {
				// or maybe display a notification
				if (playerData.getAmount() > neededAmount) {
					Config.sendMessage(playerID, "blocks_to_break",
							new String[] { String.valueOf(playerData.getAmount() - neededAmount) });
				} else {
					Config.sendMessage(playerID, "blocks_to_place",
							new String[] { String.valueOf(neededAmount - playerData.getAmount()) });
				}
			}
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		String playerID = PlayerConverter.getID(event.getPlayer());
		// if the player has this objective, the event isn't canceled,
		// the block is correct and conditions are met
		if (containsPlayer(playerID) && !event.isCancelled() && event.getBlock().getType().equals(material)
				&& (data < 0 || event.getBlock().getData() == data) && checkConditions(playerID)) {
			// remove the block from the total amount
			BlockData playerData = (BlockData) dataMap.get(playerID);
			playerData.remove();
			// complete the objective
			if (playerData.getAmount() == neededAmount) {
				completeObjective(playerID);
			} else if (notify && playerData.getAmount() % notifyInterval == 0) {
				// or maybe display a notification
				if (playerData.getAmount() > neededAmount) {
					Config.sendMessage(playerID, "blocks_to_break",
							new String[] { String.valueOf(playerData.getAmount() - neededAmount) });
				} else {
					Config.sendMessage(playerID, "blocks_to_place",
							new String[] { String.valueOf(neededAmount - playerData.getAmount()) });
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
	public String getProperty(String name, String playerID) {
		if (name.equalsIgnoreCase("left")) {
			return Integer.toString(neededAmount - ((BlockData) dataMap.get(playerID)).getAmount());
		} else if (name.equalsIgnoreCase("amount")) {
			return Integer.toString(((BlockData) dataMap.get(playerID)).getAmount());
		}
		return "";
	}

	public static class BlockData extends ObjectiveData {

		private int amount;

		public BlockData(String instruction, String playerID, String objID) {
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
