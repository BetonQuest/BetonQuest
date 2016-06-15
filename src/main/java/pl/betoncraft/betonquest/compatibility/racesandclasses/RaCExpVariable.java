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
package pl.betoncraft.betonquest.compatibility.racesandclasses;

import de.tobiyas.racesandclasses.APIs.LevelAPI;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.api.Variable;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Prints RaC experience points.
 * 
 * @author Jakub Sapalski
 */
public class RaCExpVariable extends Variable {
	
	private Type type;
	private int amount;

	public RaCExpVariable(String packName, String instruction) throws InstructionParseException {
		super(packName, instruction);
		String[] parts = instruction.replace("%", "").split("\\.");
		if (parts.length != 2) {
			throw new InstructionParseException("Incorrect number of arguments");
		}
		if (parts[1].equalsIgnoreCase("amount")) {
			type = Type.AMOUNT;
		} else if (parts[1].toLowerCase().startsWith("left:")) {
			type = Type.LEFT;
			try {
				amount = Integer.parseInt(parts[1].substring(5));
			} catch (NumberFormatException e) {
				throw new InstructionParseException("Could not parse experience amount");
			}
		}
	}

	@Override
	public String getValue(String playerID) {
		int current = LevelAPI.getCurrentExpOfLevel(PlayerConverter.getPlayer(playerID));
		switch (type) {
		case AMOUNT:
			return Integer.toString(current);
		case LEFT:
			return Integer.toString(amount - current);
		default:
			return "";
		}
	}

	private enum Type {
		AMOUNT, LEFT
	}

}
