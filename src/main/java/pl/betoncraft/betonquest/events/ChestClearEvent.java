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
package pl.betoncraft.betonquest.events;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.inventory.InventoryHolder;

import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.utils.Debug;

/**
 * Clears a specified chest from all items inside.
 * 
 * @author Jakub Sapalski
 */
public class ChestClearEvent extends QuestEvent {

	private final Block block;

	public ChestClearEvent(String packName, String instructions) throws InstructionParseException {
		super(packName, instructions);
		staticness = true;
		persistent = true;
		String[] parts = instructions.split(" ");
		if (parts.length < 2) {
			throw new InstructionParseException("Not eoungh arguments");
		}
		// extract location
		String[] location = parts[1].split(";");
		if (location.length < 4) {
			throw new InstructionParseException("Wrong location format");
		}
		World world = Bukkit.getWorld(location[3]);
		if (world == null) {
			throw new InstructionParseException("World does not exists");
		}
		int x, y, z;
		try {
			x = Integer.parseInt(location[0]);
			y = Integer.parseInt(location[1]);
			z = Integer.parseInt(location[2]);
		} catch (NumberFormatException e) {
			throw new InstructionParseException("Could not parse coordinates");
		}
		block = new Location(world, x, y, z).getBlock();
	}

	@Override
	public void run(String playerID) {
		InventoryHolder chest;
		try {
			chest = (InventoryHolder) block.getState();
		} catch (ClassCastException e) {
			Debug.error("Trying to clears items in a chest, but there's no chest! Location: X" + block.getX() + " Y"
					+ block.getY() + " Z" + block.getZ());
			return;
		}
		chest.getInventory().clear();
	}

}
