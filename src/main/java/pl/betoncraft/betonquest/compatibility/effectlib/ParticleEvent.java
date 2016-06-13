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
package pl.betoncraft.betonquest.compatibility.effectlib;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.compatibility.Compatibility;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Displays an effect.
 * 
 * @author Jakub Sapalski
 */
public class ParticleEvent extends QuestEvent {

	private String effectClass;
	private ConfigurationSection parameters;
	private Location loc;

	public ParticleEvent(String packName, String instructions) throws InstructionParseException {
		super(packName, instructions);
		String[] parts = instructions.split(" ");
		if (parts.length < 1) {
			throw new InstructionParseException("Not enough arguments");
		}
		parameters = Config.getPackage(packName).getMain().getConfig().getConfigurationSection("effects." + parts[1]);
		if (parameters == null) {
			throw new InstructionParseException("Effect '" + parts[1] + "' does not exist!");
		}
		effectClass = parameters.getString("class");
		if (effectClass == null) {
			throw new InstructionParseException("Effect '" + parts[1] + "' is incorrectly defined");
		}
		for (String part : parts) {
			if (part.startsWith("loc:")) {
				String[] partsOfLoc = part.substring(4).split(";");
				if (partsOfLoc.length < 6) {
					throw new InstructionParseException("Wrong location format");
				}
				World world = Bukkit.getWorld(partsOfLoc[3]);
				if (world == null) {
					throw new InstructionParseException("World does not exist: " + partsOfLoc[3]);
				}
				double x, y, z;
				float yaw, pitch;
				try {
					x = Double.valueOf(partsOfLoc[0]);
					y = Double.valueOf(partsOfLoc[1]);
					z = Double.valueOf(partsOfLoc[2]);
					yaw = Float.valueOf(partsOfLoc[4]);
					pitch = Float.valueOf(partsOfLoc[5]);
				} catch (NumberFormatException e) {
					throw new InstructionParseException("Could not parse coordinates");
				}
				loc = new Location(world, x, y, z, yaw, pitch);
			}
		}

	}

	@Override
	public void run(String playerID) {
		if (loc == null) {
			Compatibility.getEffectManager().start(effectClass, parameters, PlayerConverter.getPlayer(playerID));
		} else {
			Compatibility.getEffectManager().start(effectClass, parameters, loc);
		}
	}

}
