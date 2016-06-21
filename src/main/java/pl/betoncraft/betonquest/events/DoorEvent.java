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

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Openable;

import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.QuestRuntimeException;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.utils.LocationData;

public class DoorEvent extends QuestEvent {

	private LocationData loc;
	private ToggleType type;
	
	private enum ToggleType {
		ON, OFF, TOGGLE
	}

	public DoorEvent(String packName, String instructions) throws InstructionParseException {
		super(packName, instructions);
		staticness = true;
		persistent = true;
		String[] parts = instructions.split(" ");
		if (parts.length < 3) {
			throw new InstructionParseException("Not enough arguments");
		}
		loc = new LocationData(packName, parts[1]);
		try {
			type = ToggleType.valueOf(parts[2].toUpperCase());
		} catch (IllegalArgumentException e) {
			type = ToggleType.TOGGLE;
		}
	}

	@Override
	public void run(String playerID) throws QuestRuntimeException {
		Block block = loc.getLocation(playerID).getBlock();
		BlockState state = block.getState();
		MaterialData data = state.getData();
		if (data instanceof Openable) {
			Openable openable = (Openable) data;
			switch (type) {
			case ON:
				openable.setOpen(true);
				break;
			case OFF:
				openable.setOpen(false);
				break;
			case TOGGLE:
				openable.setOpen(!openable.isOpen());
				break;
			}
			state.setData((MaterialData) openable);
			state.update();
		}
	}

}
