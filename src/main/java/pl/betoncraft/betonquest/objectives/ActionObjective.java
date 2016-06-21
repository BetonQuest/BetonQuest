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
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.QuestRuntimeException;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.utils.Debug;
import pl.betoncraft.betonquest.utils.LocationData;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Player has to click on block (or air). Left click, right click and any one of
 * them is supported.
 * 
 * @author Jakub Sapalski
 */
public class ActionObjective extends Objective implements Listener {

	private String action;
	private Material type;
	private byte data;
	private LocationData loc;
	private boolean cancel = false;

	public ActionObjective(String packName, String label, String instruction) throws InstructionParseException {
		super(packName, label, instruction);
		template = ObjectiveData.class;
		String[] parts = instructions.split(" ");
		if (parts.length < 3) {
			throw new InstructionParseException("Not enough arguments");
		}
		if (parts[1].equalsIgnoreCase("right") || parts[1].equalsIgnoreCase("left")) {
			action = parts[1].toLowerCase();
		} else {
			action = "any";
		}
		if (parts[2].equalsIgnoreCase("any")) {
			type = Material.AIR;
			data = -1;
		} else {
			if (parts[2].contains(":")) {
				String[] materialParts = parts[2].split(":");
				type = Material.matchMaterial(materialParts[0]);
				if (materialParts.length > 1) {
					try {
						data = Byte.valueOf(materialParts[1]);
					} catch (NumberFormatException e) {
						throw new InstructionParseException("Could not parse data value");
					}
				} else {
					data = -1;
				}
			} else {
				type = Material.matchMaterial(parts[2]);
				data = -1;
			}
		}
		if (type == null) {
			throw new InstructionParseException("Unknown material type");
		}
		for (String part : parts) {
			if (part.contains("loc:")) {
				loc = new LocationData(packName, part.substring(4));
			}
			if (part.equalsIgnoreCase("cancel")) {
				cancel = true;
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		String playerID = PlayerConverter.getID(event.getPlayer());
		if (!containsPlayer(playerID)) {
			return;
		}
		if (type == Material.AIR) {
			switch (action) {
			case "right":
				if ((event.getAction().equals(Action.RIGHT_CLICK_AIR)
						|| event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) && checkConditions(playerID)) {
					if (cancel)
						event.setCancelled(true);
					completeObjective(playerID);
				}
				break;
			case "left":
				if ((event.getAction().equals(Action.LEFT_CLICK_AIR)
						|| event.getAction().equals(Action.LEFT_CLICK_BLOCK)) && checkConditions(playerID)) {
					if (cancel)
						event.setCancelled(true);
					completeObjective(playerID);
				}
				break;
			default:
				if ((event.getAction().equals(Action.LEFT_CLICK_AIR)
						|| event.getAction().equals(Action.LEFT_CLICK_BLOCK)
						|| event.getAction().equals(Action.RIGHT_CLICK_AIR)
						|| event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) && checkConditions(playerID)) {
					if (cancel)
						event.setCancelled(true);
					completeObjective(playerID);
				}
				break;
			}
		} else {
			Action actionEnum;
			switch (action) {
			case "right":
				actionEnum = Action.RIGHT_CLICK_BLOCK;
				break;
			case "left":
				actionEnum = Action.LEFT_CLICK_BLOCK;
				break;
			default:
				actionEnum = null;
				break;
			}
			try {
				Location location = loc.getLocation(playerID);
				double range = loc.getData().getDouble(playerID);
				if (((actionEnum == null && (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)
						|| event.getAction().equals(Action.LEFT_CLICK_BLOCK))) || event.getAction().equals(actionEnum))
						&& (event.getClickedBlock() != null && ((type == Material.FIRE
						&& event.getClickedBlock().getRelative(event.getBlockFace()).getType() == type) 
						|| event.getClickedBlock().getType().equals(type)))
						&& (data < 0 || event.getClickedBlock().getData() == data)
						&& (loc == null || (event.getClickedBlock().getWorld().equals(location.getWorld())
						&& event.getClickedBlock().getLocation().distance(location) <= range))
						&& checkConditions(playerID)) {
					if (cancel)
						event.setCancelled(true);
					completeObjective(playerID);
				}
			} catch (QuestRuntimeException e) {
				Debug.error("Error while handling '" + pack.getName() + "." + getLabel() + "' objective: " + e.getMessage());
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
		return "";
	}

	@Override
	public String getProperty(String name, String playerID) {
		if (name.equalsIgnoreCase("location")) {
			if (loc == null) {
				return "";
			}
			Location location;
			try {
				location = loc.getLocation(playerID);
			} catch (QuestRuntimeException e) {
				Debug.error("Error while getting location property in '" + pack.getName() + "." + getLabel() + "' objective: "
						+ e.getMessage());
				return "";
			}
			return "X: " + location.getBlockX() + ", Y: " + location.getBlockY() + ", Z: " + location.getBlockZ();
		}
		return "";
	}

}
