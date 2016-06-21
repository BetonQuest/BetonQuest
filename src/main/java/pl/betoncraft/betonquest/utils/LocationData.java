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
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.QuestRuntimeException;
import pl.betoncraft.betonquest.VariableNumber;

/**
 * This class parses various location strings.
 */
public class LocationData {
	
	private Location loc;
	private Vector vector = null;
	private VariableNumber data;
	private Type type;
	
	private enum Type {
		LOCATION, PLAYER
	}
	
	/**
	 * Parses the location string. The Location and optional data value can
	 * be accessed by this object's methods.
	 * 
	 * @param packName
	 *            name of the package, required for variable resolution
	 * @param string
	 *            string containing raw location, written like
	 *            '100;200;300;world;0;0;10', where the first three numbers
	 *            are x,y,z coordinates, world is name of the world,
	 *            followed by optional yaw and pitch, followed by optional
	 *            data value (i.e. radius around location). This data value
	 *            can be a conversation variable.
	 * @throws InstructionParseException
	 *             when there is an error while parsing the location or
	 *             optional variable
	 */
	public LocationData(String packName, String string) throws InstructionParseException {
		// parse the vector
		String base = null;
		if (string.contains("->")) {
			String[] main = string.split("->");
			if (main.length != 2) {
				throw new InstructionParseException("Incorrect location format");
			}
			String vec = main[1];
			if (!vec.matches("^\\(-?\\d+.?\\d*;-?\\d+.?\\d*;-?\\d+.?\\d*\\)(;.+)?$")) {
				throw new InstructionParseException("Incorrect vector format");
			}
			String end = (vec.matches("^\\(-?\\d+.?\\d*;-?\\d+.?\\d*;-?\\d+.?\\d*\\)$")) ? ""
					: vec.substring(vec.indexOf(')') + 1);
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
			base = main[0] + end;
		} else {
			vector = new Vector(0, 0, 0);
			base = string;
		}
		// parse the base
		if (base.startsWith("player")) {
			type = Type.PLAYER;
			// that's it, additional data will be parsed later
		} else {
			type = Type.LOCATION;
			String[] parts = base.split(";");
			if (parts.length < 4) {
				throw new InstructionParseException("Wrong location format");
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
				if (parts.length >= 6) {
					yaw = Float.parseFloat(parts[4]);
					pitch = Float.parseFloat(parts[5]);
				}
			} catch (NumberFormatException e) {
				throw new InstructionParseException("Could not parse location coordinates");
			}
			loc = new Location(world, x, y, z, yaw, pitch);
			// check if the last argument is data, if not return so it does not get parsed as data
			if (parts.length != 5 && parts.length != 7) {
				return;
			}
		}
		// parse additional data (if base parsing returned before this code, it means there is no data here)
		if (base.contains(";")) {
			try {
				data = new VariableNumber(packName, base.substring(base.lastIndexOf(';') + 1, base.length()));
			} catch (NumberFormatException e) {
				throw new InstructionParseException("Could not parse the location data value: " + e.getMessage());
			}
		}
	}
	
	private Location getBaseLoc(String playerID) throws QuestRuntimeException {
		switch (type) {
		case LOCATION:
			return loc;
		case PLAYER:
			if (playerID == null) {
				throw new QuestRuntimeException("Location in 'player' format cannot be accessed without the player;"
						+ " consider changing it to absolute coordinates");
			}
			Player player = PlayerConverter.getPlayer(playerID);
			if (player == null) {
				throw new QuestRuntimeException("Location cannot be accessed because player is offline;"
						+ " consider changing it to absolute coordinates");
			}
			return player.getLocation();
		default: return null;
		}
	}
	
	/**
	 * @param playerID
	 *            ID of the player, needed for location resolution
	 * @return the location represented by this object
	 * @throws InstructionParseException
	 *             when location is defined for the player but the player cannot
	 *             be accessed
	 */
	public Location getLocation(String playerID) throws QuestRuntimeException {
		return getBaseLoc(playerID).clone().add(vector);
	}
	
	/**
	 * @return the data variable
	 */
	public VariableNumber getData() {
		return data;
	}
	
}