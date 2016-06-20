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
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.utils.PlayerConverter;
import pl.betoncraft.betonquest.utils.Utils;

public class KillPlayerObjective extends Objective implements Listener {

	private int amount = 1;
	private String name = null;
	private String[] required = new String[0];
	private boolean notify = false;
	
	public KillPlayerObjective(String packName, String label, String instructions) throws InstructionParseException {
		super(packName, label, instructions);
		template = KillData.class;
		String[] parts = instructions.split(" ");
		if (parts.length < 2) {
			throw new InstructionParseException("Not enough arguments");
		}
		try {
			amount = Integer.parseInt(parts[1]);
		} catch (NumberFormatException e) {
			throw new InstructionParseException("Could not parse amount");
		}
		for (String part : parts) {
			if (part.toLowerCase().startsWith("name:")) {
				name = part.substring(5);
			} else if (part.toLowerCase().startsWith("required:")) {
				required = part.substring(9).split(",");
				for (int i = 0; i < required.length; i++) {
					required[i] = Utils.addPackage(packName, required[i]);
				}
			} else if (part.equalsIgnoreCase("notify")) {
				notify = true;
			}
		}
	}
	
	@EventHandler
	public void onKill(PlayerDeathEvent event) {
		if (event.getEntity().getKiller() == null) {
			return;
		}
		String victim = PlayerConverter.getID(event.getEntity());
		String killer = PlayerConverter.getID(event.getEntity().getKiller());
		if (containsPlayer(killer)) {
			if (name != null && !event.getEntity().getName().equalsIgnoreCase(name)) {
				return;
			}
			for (String condition : required) {
				if (!BetonQuest.condition(victim, condition)) {
					return;
				}
			}
			if (!checkConditions(killer)) {
				return;
			}
			KillData data = (KillData) dataMap.get(killer);
			data.kill();
			if (data.getLeft() <= 0) {
				completeObjective(killer);
			} else if (notify) {
				Config.sendMessage(killer, "players_to_kill", new String[] { String.valueOf(data.getLeft()) });
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
		return String.valueOf(amount);
	}
	
	public static class KillData extends ObjectiveData {
		
		private int amount;
		
		public KillData(String instruction, String playerID, String objID) {
			super(instruction, playerID, objID);
			amount = Integer.parseInt(instruction);
		}
		
		public void kill() {
			amount--;
			update();
		}
		
		public int getLeft() {
			return amount;
		}
		
		@Override
		public String toString() {
			return String.valueOf(amount);
		}
		
	}

}
