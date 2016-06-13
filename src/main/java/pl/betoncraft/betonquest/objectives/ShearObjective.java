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
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerShearEntityEvent;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Requires the player to shear a sheep.
 * 
 * @author Jakub Sapalski
 */
public class ShearObjective extends Objective implements Listener {

	private final String color;
	private final String name;
	private final int amount;
	private final boolean notify;

	public ShearObjective(String packName, String label, String instructions) throws InstructionParseException {
		super(packName, label, instructions);
		template = SheepData.class;
		String[] parts = instructions.split(" ");
		if (parts.length < 2) {
			throw new InstructionParseException("Not enough arguments");
		}
		try {
			amount = Integer.parseInt(parts[1]);
		} catch (NumberFormatException e) {
			throw new InstructionParseException("Could not parse amount");
		}
		String tempName = null, tempColor = null;
		boolean tempNotify = false;
		for (String part : parts) {
			if (part.startsWith("color:")) {
				tempColor = part.substring(6);
			} else if (part.startsWith("name:")) {
				tempName = part.substring(5);
			} else if (part.equalsIgnoreCase("notify"))
				tempNotify = true;
		}
		name = tempName;
		color = tempColor;
		notify = tempNotify;
	}

	@EventHandler
	public void onShear(PlayerShearEntityEvent event) {
		if (event.getEntity().getType() != EntityType.SHEEP)
			return;
		String playerID = PlayerConverter.getID(event.getPlayer());
		if (!containsPlayer(playerID))
			return;
		if (name != null
				&& (event.getEntity().getCustomName() == null || !event.getEntity().getCustomName().equals(name)))
			return;
		if (color != null && !((Sheep) event.getEntity()).getColor().toString().equalsIgnoreCase(color))
			return;
		SheepData data = (SheepData) dataMap.get(playerID);
		if (checkConditions(playerID))
			data.shearSheep();
		if (data.getAmount() <= 0)
			completeObjective(playerID);
		else if (notify)
			Config.sendMessage(playerID, "sheep_to_shear", new String[] { String.valueOf(data.getAmount()) });
	}

	@Override
	public String getProperty(String name, String playerID) {
		if (name.equalsIgnoreCase("left")) {
			return Integer.toString(((SheepData) dataMap.get(playerID)).getAmount());
		} else if (name.equalsIgnoreCase("amount")) {
			return Integer.toString(amount - ((SheepData) dataMap.get(playerID)).getAmount());
		}
		return "";
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
		return Integer.toString(amount);
	}

	public static class SheepData extends ObjectiveData {

		private int amount;

		public SheepData(String instruction, String playerID, String objID) {
			super(instruction, playerID, objID);
			amount = Integer.parseInt(instruction);
		}

		public void shearSheep() {
			amount--;
			update();
		}

		public int getAmount() {
			return amount;
		}

		@Override
		public String toString() {
			return String.valueOf(amount);
		}

	}

}
