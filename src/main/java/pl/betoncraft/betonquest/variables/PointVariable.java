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
package pl.betoncraft.betonquest.variables;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.Point;
import pl.betoncraft.betonquest.api.Variable;
import pl.betoncraft.betonquest.utils.Utils;

/**
 * Allows you to display total amount of points or amount of points remaining to
 * some other amount.
 * 
 * @author Jakub Sapalski
 */
public class PointVariable extends Variable {

	private String category;
	private Type type;
	private int amount;

	public PointVariable(String packName, String instruction) throws InstructionParseException {
		super(packName, instruction);
		String[] parts = instruction.replace("%", "").split("\\.");
		if (parts.length != 3) {
			throw new InstructionParseException("Incorrect number of arguments");
		}
		category = Utils.addPackage(packName, parts[1]);
		if (parts[2].equalsIgnoreCase("amount")) {
			type = Type.AMOUNT;
		} else if (parts[2].toLowerCase().startsWith("left:")) {
			type = Type.LEFT;
			try {
				amount = Integer.parseInt(parts[2].substring(5));
			} catch (NumberFormatException e) {
				throw new InstructionParseException("Could not parse point amount");
			}
		}
	}

	@Override
	public String getValue(String playerID) {
		Point point = null;
		for (Point p : BetonQuest.getInstance().getPlayerData(playerID).getPoints()) {
			if (p.getCategory().equalsIgnoreCase(category)) {
				point = p;
				break;
			}
		}
		int count = 0;
		if (point != null)
			count = point.getCount();
		switch (type) {
		case AMOUNT:
			return Integer.toString(count);
		case LEFT:
			return Integer.toString(amount - count);
		default:
			return "";
		}
	}

	private enum Type {
		AMOUNT, LEFT
	}

}
