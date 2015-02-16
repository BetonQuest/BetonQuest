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

import java.util.Random;

import pl.betoncraft.betonquest.api.Condition;

/**
 * Check that generate number match to the condition in the instruction.
 * 
 * @author BYK
 *
 */
public class RandomCondition extends Condition {
    /**
     * private fields: valueMax-keep the last number match to the condition.
     * rangeOfRandom-keep the range of integer number which draw one number.
     */
    private int valueMax = 0;
    private int rangeOfRandom = 0;

    /**
     * Constructor method
     * 
     * @param playerID
     * @param instructions
     */
    public RandomCondition(String playerID, String instructions) {
        super(playerID, instructions);
        String value = null;
        String[] parts = instructions.split(" ");
        for (String part : parts) {
            if (part.contains("random:")) {
                value = part.substring(7);
            }
        }
        parts = value.split("-");
        valueMax = Integer.parseInt(parts[0]);
        rangeOfRandom = Integer.parseInt(parts[1]);
    }

    @Override
    /**
     * Method check that the condition is met-return true
     * else if is not met return false.
     */
    public boolean isMet() {

        Random generator = new Random();
        int temp = 0;
        temp = generator.nextInt(rangeOfRandom) + 1;
        if (temp <= valueMax) {
            return true;
        } else {
            return false;
        }
    }

}
