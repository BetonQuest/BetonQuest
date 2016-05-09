/**
 * BetonQuest - advanced quests for Bukkit
 * Copyright (C) 2015  Jakub "Co0sh" Sapalski
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
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Player has to reach certain radius around the specified location
 * 
 * @author Jakub Sapalski
 */
public class LocationObjective extends Objective implements Listener {

	private final Location location;
	private final double distance;

	public LocationObjective(String packName, String label, String instruction) throws InstructionParseException {
		super(packName, label, instruction);
		template = ObjectiveData.class;
		String[] parts = instructions.split(" ");
		if (parts.length < 2) {
			throw new InstructionParseException("Not enough arguments");
		}
		String[] partsOfLoc = parts[1].split(";");
		if (partsOfLoc.length < 5) {
			throw new InstructionParseException("Wrong location format");
		}
		World world = Bukkit.getWorld(partsOfLoc[3]);
		if (world == null) {
			throw new InstructionParseException("World does not exist: " + partsOfLoc[3]);
		}
		double x, y, z;
		try {
			x = Double.valueOf(partsOfLoc[0]);
			y = Double.valueOf(partsOfLoc[1]);
			z = Double.valueOf(partsOfLoc[2]);
		} catch (NumberFormatException e) {
			throw new InstructionParseException("Could not parse coordinates");
		}
		location = new Location(world, x, y, z);
		try {
			distance = Double.valueOf(partsOfLoc[4]);
			if (distance <= 0) {
				throw new InstructionParseException("Distance must be positive");
			}
		} catch (NumberFormatException e) {
			throw new InstructionParseException("Could not parse distance");
		}
	}

	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		String playerID = PlayerConverter.getID(event.getPlayer());
		if (containsPlayer(playerID) && event.getPlayer().getWorld().equals(location.getWorld())) {
			if (event.getTo().distanceSquared(location) <= distance * distance && super.checkConditions(playerID)) {
				completeObjective(playerID);
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
			return "X: " + location.getBlockX() + ", Y: " + location.getBlockY() + ", Z: " + location.getBlockZ();
		}
		return "";
	}

}
