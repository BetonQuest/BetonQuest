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
import org.bukkit.Material;

import pl.betoncraft.betonquest.core.QuestEvent;

public class SetBlockEvent extends QuestEvent {
	
	private Material block;
	private byte data = 0;
	private Location loc;

	@SuppressWarnings("deprecation")
	public SetBlockEvent(String playerID, String instructions) {
		super(playerID, instructions);
		String[] parts = instructions.split(" ");
		for (String part : parts) {
			if (part.contains("block:")) {
				block = Material.matchMaterial(part.substring(6));
			}
			if (part.contains("data:")) {
				data = Byte.parseByte(part.substring(5));
			}
			if (part.contains("loc:")) {
				loc = decodeLocation(part.substring(4));
			}
		}
		if (block != null && loc != null) {
			loc.getBlock().setType(block);
			loc.getBlock().setData(data);
		}
	}

    private Location decodeLocation(String locStr) {

        String [] coords = locStr.split(";");

        Location loc = new Location(
                Bukkit.getWorld(coords[3]),
                Double.parseDouble(coords[0]),
                Double.parseDouble(coords[1]),
                Double.parseDouble(coords[2]));

        return loc;
    }

}
