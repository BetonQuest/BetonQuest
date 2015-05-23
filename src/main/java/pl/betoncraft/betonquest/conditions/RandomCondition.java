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
import pl.betoncraft.betonquest.utils.Debug;

/**
 * The condition that is met randomly
 * 
 * @author BYK
 */
public class RandomCondition extends Condition {

    private int valueMax = 0;
    private int rangeOfRandom = 0;

    public RandomCondition(String playerID, String packName, String instructions) {
        super(playerID, packName, instructions);
        String[] values = null;
        String[] parts = instructions.split(" ");
        if (parts.length < 2) {
            Debug.error("Randomness not defined in random condition: " + instructions);
            isOk = false;
            return;
        }
        values = parts[1].split("-");
        if (values.length != 2) {
            Debug.error("Wrong randomness format in: " + instructions);
            isOk = false;
            return;
        }
        try {
            valueMax = Integer.parseInt(values[0]);
            rangeOfRandom = Integer.parseInt(values[1]);
        } catch (NumberFormatException e) {
            Debug.error("Cannot parse randomness values in: " + instructions);
            isOk = false;
            return;
        }
    }

    @Override
    public boolean isMet() {
        Random generator = new Random();
        int temp = generator.nextInt(rangeOfRandom) + 1;
        if (temp <= valueMax) {
            return true;
        } else {
            return false;
        }
    }

}
