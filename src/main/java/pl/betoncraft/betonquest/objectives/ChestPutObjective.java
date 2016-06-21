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
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryHolder;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.QuestRuntimeException;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.conditions.ChestItemCondition;
import pl.betoncraft.betonquest.events.ChestTakeEvent;
import pl.betoncraft.betonquest.utils.Debug;
import pl.betoncraft.betonquest.utils.LocationData;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Requires the player to put items in the chest. Items can optionally NOT
 * disappear once the chest is closed.
 * 
 * @author Jakub Sapalski
 */
public class ChestPutObjective extends Objective implements Listener {

	private final Condition chestItemCondition;
	private final QuestEvent chestTakeEvent;
	private final LocationData loc;

	public ChestPutObjective(String packName, String label, String instructions) throws InstructionParseException {
		super(packName, label, instructions);
		template = ObjectiveData.class;
		String[] parts = instructions.split(" ");
		if (parts.length < 3) {
			throw new InstructionParseException("Not enough arguments");
		}
		// extract location
		loc = new LocationData(packName, parts[1]);
		try {
			chestItemCondition = new ChestItemCondition(packName, "chestitem " + parts[1] + " " + parts[2]);
		} catch (InstructionParseException e) {
			throw new InstructionParseException("Could not create inner chest item condition: " + e.getMessage());
		}
		if (parts.length > 3 && parts[3].equalsIgnoreCase("items-stay")) {
			chestTakeEvent = null;
		} else {
			chestTakeEvent = new ChestTakeEvent(packName, "chesttake " + parts[1] + " " + parts[2]);
		}

	}

	@EventHandler
	public void onChestClose(InventoryCloseEvent event) {
		if (!(event.getPlayer() instanceof Player))
			return;
		String playerID = PlayerConverter.getID((Player) event.getPlayer());
		if (!containsPlayer(playerID))
			return;
		try {
			Block block = loc.getLocation(playerID).getBlock();
			InventoryHolder chest;
			try {
				chest = (InventoryHolder) block.getState();
			} catch (ClassCastException e) {
				return;
			}
			if (event.getInventory() == null || event.getInventory().getHolder() == null)
				return;
			if (!event.getInventory().getHolder().equals(chest))
				return;
			if (chestItemCondition.check(playerID) && checkConditions(playerID)) {
				completeObjective(playerID);
				if (chestTakeEvent != null)
					chestTakeEvent.run(playerID);
			}
		} catch (QuestRuntimeException e) {
			Debug.error("Error while handling '" + pack.getName() + "." + getLabel() + "' objective: " + e.getMessage());
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
		return "";
	}

}
