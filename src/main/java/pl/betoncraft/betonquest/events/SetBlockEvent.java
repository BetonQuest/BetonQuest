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
import org.bukkit.Material;

import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.QuestRuntimeException;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.utils.LocationData;

/**
 * Sets the block at specified location
 * 
 * @author Jakub Sapalski
 */
public class SetBlockEvent extends QuestEvent {

	private final Material block;
	private final byte data;
	private final LocationData loc;

	public SetBlockEvent(Instruction instruction) throws InstructionParseException {
		super(instruction);
		staticness = true;
		block = instruction.getMaterial(instruction.next());
		loc = instruction.getLocation();
		data = instruction.getByte(instruction.getOptional("data"), (byte) 0);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void run(String playerID) throws QuestRuntimeException {
		Location location = loc.getLocation(playerID);
		location.getBlock().setType(block);
		location.getBlock().setData(data);
	}

}
