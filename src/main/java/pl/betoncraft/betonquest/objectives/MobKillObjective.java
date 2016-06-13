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
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.api.MobKillNotifier.MobKilledEvent;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Player has to kill specified amount of specified mobs. It can also require
 * the player to kill specifically named mobs and notify them about the required
 * amount.
 * 
 * @author Jakub Sapalski
 */
public class MobKillObjective extends Objective implements Listener {

	protected final EntityType mobType;
	protected final int amount;
	protected final String name;
	protected final boolean notify;

	public MobKillObjective(String packName, String label, String instruction) throws InstructionParseException {
		super(packName, label, instruction);
		template = MobData.class;
		String[] parts = instructions.split(" ");
		if (parts.length < 3) {
			throw new InstructionParseException("Not enough arguments");
		}
		try {
			mobType = EntityType.valueOf(parts[1].toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new InstructionParseException("Unknown entity type: " + parts[1]);
		}
		try {
			amount = Integer.valueOf(parts[2]);
		} catch (NumberFormatException e) {
			throw new InstructionParseException("Could not parse amount");
		}
		if (amount < 1) {
			throw new InstructionParseException("Amount cannot be less than 1");
		}
		String tempName = null;
		boolean tempNotify = false;
		for (String part : parts) {
			if (part.startsWith("name:")) {
				tempName = part.substring(5).replace("_", " ");
			} else if (part.equalsIgnoreCase("notify")) {
				tempNotify = true;
			}
		}
		name = tempName;
		notify = tempNotify;
	}

	@EventHandler
	public void onMobKill(MobKilledEvent event) {
		// check if it's the right entity type
		if (!event.getEntity().getType().equals(mobType)) {
			return;
		}
		// if the entity should have a name and it does not match, return
		if (name != null
				&& (event.getEntity().getCustomName() == null || !event.getEntity().getCustomName().equals(name))) {
			return;
		}
		// check if the player has this objective
		String playerID = PlayerConverter.getID(event.getPlayer());
		if (containsPlayer(playerID) && checkConditions(playerID)) {
			// the right mob was killed, handle data update
			MobData playerData = (MobData) dataMap.get(playerID);
			playerData.subtract();
			if (playerData.isZero()) {
				completeObjective(playerID);
			} else if (notify) {
				// send a notification
				Config.sendMessage(playerID, "mobs_to_kill", new String[] { String.valueOf(playerData.getAmount()) });
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
		return Integer.toString(amount);
	}

	@Override
	public String getProperty(String name, String playerID) {
		if (name.equalsIgnoreCase("left")) {
			return Integer.toString(amount - ((MobData) dataMap.get(playerID)).getAmount());
		} else if (name.equalsIgnoreCase("amount")) {
			return Integer.toString(((MobData) dataMap.get(playerID)).getAmount());
		}
		return "";
	}

	public static class MobData extends ObjectiveData {

		private int amount;

		public MobData(String instruction, String playerID, String objID) {
			super(instruction, playerID, objID);
			amount = Integer.parseInt(instruction);
		}

		public int getAmount() {
			return amount;
		}

		public void subtract() {
			amount--;
			update();
		}

		public boolean isZero() {
			return amount <= 0;
		}

		@Override
		public String toString() {
			return Integer.toString(amount);
		}

	}
}
