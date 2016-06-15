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

import de.tobiyas.racesandclasses.APIs.ClassAPI;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.classes.ClassContainer;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Checks RaC class.
 * 
 * @author Jakub Sapalski
 */
public class RaCClassCondition extends Condition {
	
	private String className;
	private boolean none = false;

	public RaCClassCondition(String packName, String instructions) throws InstructionParseException {
		super(packName, instructions);
		String[] parts = instructions.split(" ");
		if (parts.length < 2) {
			throw new InstructionParseException("Not enough arguments");
		}
		if (parts[1].equalsIgnoreCase("none")) {
			none = true;
		} else {
			if (!ClassAPI.getAllClassNames().contains(parts[1])) {
				throw new InstructionParseException("No such class: " + parts[1]);
			}
			className = parts[1];
		}
	}

	@Override
	public boolean check(String playerID) {
		ClassContainer classC = ClassAPI.getClassOfPlayer(PlayerConverter.getPlayer(playerID));
		return (classC == null && none) || (classC != null && !none && classC.getConfigNodeName()
				.equalsIgnoreCase(className));
	}

}
