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
package pl.betoncraft.betonquest.compatibility.citizens;

import net.citizensnpcs.api.event.NPCDeathEvent;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Player has to kill an NPC
 * 
 * @author Jakub Sapalski
 */
public class NPCKillObjective extends Objective implements Listener {

	private final int ID;
	private final int amount;

	public NPCKillObjective(String packName, String label, String instruction) throws InstructionParseException {
		super(packName, label, instruction);
		template = NPCData.class;
		String[] parts = instructions.split(" ");
		if (parts.length < 2) {
			throw new InstructionParseException("Not enough arguments");
		}
		try {
			ID = Integer.parseInt(parts[1]);
		} catch (NumberFormatException e) {
			throw new InstructionParseException("Could not parse ID");
		}
		int tempAmount = 1;
		for (String part : parts) {
			if (part.contains("amount:")) {
				tempAmount = Integer.parseInt(part.substring(7));
			}
		}
		amount = tempAmount;
		if (amount < 1) {
			throw new InstructionParseException("Amount cannot be less than 1");
		}
	}

	@EventHandler
	public void onNPCKilling(NPCDeathEvent event) {
		if (event.getNPC().getId() == ID
				&& event.getNPC().getEntity().getLastDamageCause().getCause().equals(DamageCause.ENTITY_ATTACK)) {
			EntityDamageByEntityEvent damage = (EntityDamageByEntityEvent) event.getNPC().getEntity()
					.getLastDamageCause();
			if (damage.getDamager() instanceof Player) {
				String playerID = PlayerConverter.getID((Player) damage.getDamager());
				NPCData playerData = (NPCData) dataMap.get(playerID);
				if (containsPlayer(playerID) && checkConditions(playerID)) {
					playerData.kill();
					if (playerData.killed()) {
						completeObjective(playerID);
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
		return Integer.toString(amount);
	}

	@Override
	public String getProperty(String name, String playerID) {
		if (name.equalsIgnoreCase("left")) {
			return Integer.toString(amount - ((NPCData) dataMap.get(playerID)).getAmount());
		} else if (name.equalsIgnoreCase("amount")) {
			return Integer.toString(((NPCData) dataMap.get(playerID)).getAmount());
		}
		return "";
	}

	public static class NPCData extends ObjectiveData {

		private int amount;

		public NPCData(String instruction, String playerID, String objID) {
			super(instruction, playerID, objID);
			amount = Integer.parseInt(instruction);
		}

		private void kill() {
			amount--;
			update();
		}

		private boolean killed() {
			return amount <= 0;
		}

		private int getAmount() {
			return amount;
		}

	}

}
