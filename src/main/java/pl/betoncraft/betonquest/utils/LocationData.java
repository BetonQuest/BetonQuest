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
package pl.betoncraft.betonquest.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.QuestRuntimeException;
import pl.betoncraft.betonquest.api.Variable;
import pl.betoncraft.betonquest.config.Config;

/**
 * This class parses various location strings.
 */
public class LocationData {

	private Location loc;
	private Variable variable;
	private Vector vector = null;
	private Type type;
	
	private enum Type {
		LOCATION, VARIABLE
	}
	
	/**
	 * Parses the location string.
	 * 
	 * @param packName
	 *            name of the package, required for variable resolution
	 * @param string
	 *            string containing raw location, written like
	 *            '100;200;300;world;0;0', where the first three numbers
	 *            are x,y,z coordinates, world is name of the world,
	 *            followed by optional yaw and pitch.
	 * @throws InstructionParseException
	 *             when there is an error while parsing the location
	 */
	public LocationData(String packName, String string) throws InstructionParseException {
		// parse the vector
		String base = null;
		if (string.contains("->")) {
			String[] main = string.split("->");
			if (main.length != 2) {
				throw new InstructionParseException("Incorrect vector format (" + base + ")");
			}
			String vec = main[1];
			if (!vec.matches("^\\(-?\\d+.?\\d*;-?\\d+.?\\d*;-?\\d+.?\\d*\\)(;.+)?$")) {
				throw new InstructionParseException("Incorrect vector format");
			}
			String[] numbers = vec.substring(1, vec.indexOf(')')).split(";");
			double x, y, z;
			try {
				x = Double.parseDouble(numbers[0]);
				y = Double.parseDouble(numbers[1]);
				z = Double.parseDouble(numbers[2]);
			} catch (NumberFormatException e) {
				throw new InstructionParseException("Could not parse vector numbers");
			}
			vector = new Vector(x, y, z);
			base = main[0];
		} else {
			vector = new Vector(0, 0, 0);
			base = string;
		}
		// special keyword "player" is the same as %location% variable
		// it's used for backwards compatibility
		if (base.toLowerCase().equals("player")) {
			base = "%location%";
		}
		// parse the base
		if (base.startsWith("%") && base.endsWith("%")) {
			type = Type.VARIABLE;
			variable = BetonQuest.createVariable(Config.getPackages().get(packName), base);
		} else {
			type = Type.LOCATION;
			loc = parseAbsoluteFormat(base);
		}
	}
	
	private Location parseAbsoluteFormat(String abs) throws InstructionParseException {
		String[] parts = abs.split(";");
		if (parts.length < 4) {
			throw new InstructionParseException("Wrong location format (" + abs + ")");
		}
		World world = Bukkit.getWorld(parts[3]);
		if (world == null) {
			throw new InstructionParseException("World " + parts[3] + " does not exists.");
		}
		double x, y, z;
		float yaw = 0, pitch = 0;
		try {
			x = Double.parseDouble(parts[0]);
			y = Double.parseDouble(parts[1]);
			z = Double.parseDouble(parts[2]);
			if (parts.length == 6) {
				yaw = Float.parseFloat(parts[4]);
				pitch = Float.parseFloat(parts[5]);
			}
		} catch (NumberFormatException e) {
			throw new InstructionParseException("Could not parse location coordinates");
		}
		loc = new Location(world, x, y, z, yaw, pitch);
		return loc;
	}
	
	private Location getBaseLoc(String playerID) throws QuestRuntimeException {
		switch (type) {
		case LOCATION:
			return loc;
		case VARIABLE:
			if (playerID == null) {
				throw new QuestRuntimeException("Variable location cannot accessed without the player;"
						+ " consider changing it to absolute coordinates");
			}
			String value = variable.getValue(playerID);
			try {
				return loc = parseAbsoluteFormat(value);
			} catch (InstructionParseException e) {
				throw new QuestRuntimeException("Could not resolve a variable to location format: " + e.getMessage());
			}
		default: return null;
		}
	}
	
	/**
	 * @param playerID
	 *            ID of the player, needed for location resolution
	 * @return the location represented by this object
	 * @throws QuestRuntimeException
	 *             when location is defined for the player but the player cannot
	 *             be accessed
	 */
	public Location getLocation(String playerID) throws QuestRuntimeException {
		return getBaseLoc(playerID).clone().add(vector);
	}
	
}