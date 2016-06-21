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
package pl.betoncraft.betonquest.conditions;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.Point;
import pl.betoncraft.betonquest.QuestRuntimeException;
import pl.betoncraft.betonquest.VariableNumber;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.utils.Utils;

/**
 * Requires the player to have specified amount of points (or more) in specified
 * category
 * 
 * @author Jakub Sapalski
 */
public class PointCondition extends Condition {

	private final String category;
	private final VariableNumber count;

	public PointCondition(String packName, String instructions) throws InstructionParseException {
		super(packName, instructions);
		String[] parts = instructions.split(" ");
		if (parts.length < 3) {
			throw new InstructionParseException("Not enough arguments");
		}
		category = Utils.addPackage(packName, parts[1]);
		try {
			count = new VariableNumber(packName, parts[2]);
		} catch (NumberFormatException e) {
			throw new InstructionParseException("Could not parse point amount");
		}
	}

	@Override
	public boolean check(String playerID) throws QuestRuntimeException {
		int c = count.getInt(playerID);
		for (Point point : BetonQuest.getInstance().getPlayerData(playerID).getPoints()) {
			if (point.getCategory().equalsIgnoreCase(category)) {
				return point.getCount() >= c;
			}
		}
		return false;
	}

}
