/**
 * BetonQuest - advanced quests for Bukkit
 * Copyright (C) 2015  Jakub "Co0sh" Sapalski
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
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.core.Point;
import pl.betoncraft.betonquest.utils.Debug;

/**
 * Requires the player to have specified amount of points (or more)
 * in specified category
 * 
 * @author Co0sh
 */
public class PointCondition extends Condition {

    private String category = null;
    private int count = 0;

    public PointCondition(String playerID, String packName, String instructions) {
        super(playerID, packName, instructions);
        String[] parts = instructions.split(" ");
        if (parts.length < 3) {
            Debug.error("Not enough arguments for a point condition in: " + instructions);
            isOk = false;
            return;
        }
        category = parts[1];
        try {
            count = Integer.parseInt(parts[2]);
        } catch (NumberFormatException e) {
            Debug.error("Could not parse point amount in: " + instructions);
            isOk = false;
            return;
        }
    }

    @Override
    public boolean isMet() {
        if (!isOk) {
            Debug.error("There was an error, returning false.");
            return false;
        }
        int points = 0;
        for (Point point : BetonQuest.getInstance().getDBHandler(playerID).getPoints()) {
            if (point.getCategory().equalsIgnoreCase(category)) {
                points = point.getCount();
            }
        }
        if (points >= count) {
            return true;
        }
        return false;
    }

}
