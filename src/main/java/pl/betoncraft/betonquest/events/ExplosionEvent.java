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
package pl.betoncraft.betonquest.events;

import org.bukkit.Location;

import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.QuestRuntimeException;
import pl.betoncraft.betonquest.VariableNumber;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.utils.LocationData;

/**
 * Spawns an explosion in a given location and with given flags
 * 
 * @author Dzejkop
 */
public class ExplosionEvent extends QuestEvent {

	private final boolean setsFire;
	private final boolean breaksBlocks;
	private final VariableNumber power;
	private final LocationData loc;

	public ExplosionEvent(String packName, String instructions) throws InstructionParseException {
		super(packName, instructions);
		staticness = true;
		String[] parts = instructions.split(" ");
		if (parts.length < 5) {
			throw new InstructionParseException("Not enough arguments");
		}
		setsFire = parts[1].equals("1") ? true : false;
		breaksBlocks = parts[2].equals("1") ? true : false;
		try {
			power = new VariableNumber(packName, parts[3]);
		} catch (NumberFormatException e) {
			throw new InstructionParseException("Could not parse power");
		}
		loc = new LocationData(packName, parts[4]);

	}

	@Override
	public void run(String playerID) throws QuestRuntimeException {
		Location location = loc.getLocation(playerID);
		location.getWorld().createExplosion(location.getX(), location.getY(), location.getZ(),
				(float) power.getDouble(playerID), setsFire, breaksBlocks);
	}
}
