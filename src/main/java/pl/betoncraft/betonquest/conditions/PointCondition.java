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

/**
 * 
 * @author Co0sh
 */
public class PointCondition extends Condition {

    private String category = null;
    private int count = 0;

    /**
     * Constructor method
     * 
     * @param playerID
     * @param instructions
     */
    public PointCondition(String playerID, String instructions) {
        super(playerID, instructions);
        String[] parts = instructions.split(" ");
        for (String part : parts) {
            if (part.contains("category:")) {
                category = part.substring(9);
            }
            if (part.contains("count:")) {
                count = Integer.valueOf(part.substring(6));
            }
        }
        if (category == null) {
            category = "global";
        }
    }

    @Override
    public boolean isMet() {
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
