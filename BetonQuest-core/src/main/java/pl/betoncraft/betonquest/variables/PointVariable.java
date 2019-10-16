/*
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
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.Point;
import pl.betoncraft.betonquest.api.Variable;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;

import java.util.List;

/**
 * Allows you to display total amount of points or amount of points remaining to
 * some other amount.
 *
 * @author Jakub Sapalski
 */
public class PointVariable extends Variable {

    protected String category;
    protected Type type;
    protected int amount;

    public PointVariable(Instruction instruction) throws InstructionParseException {
        super(instruction);
        category = instruction.next();
        if (category.contains("*")) {
            category = category.replace('*', '.');
        } else {
            category = instruction.getPackage().getName() + "." + category;
        }
        if (instruction.next().equalsIgnoreCase("amount")) {
            type = Type.AMOUNT;
        } else if (instruction.current().toLowerCase().startsWith("left:")) {
            type = Type.LEFT;
            try {
                amount = Integer.parseInt(instruction.current().substring(5));
            } catch (NumberFormatException e) {
                throw new InstructionParseException("Could not parse point amount", e);
            }
        } else {
            throw new InstructionParseException(String.format("Unknown variable type: '%s'",
                    instruction.current()));
        }
    }

    @Override
    public String getValue(String playerID) {
        return getValue(BetonQuest.getInstance().getPlayerData(playerID).getPoints());
    }

    protected String getValue(List<Point> points) {
        Point point = null;
        for (Point p : points) {
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

    protected enum Type {
        AMOUNT, LEFT
    }

}
