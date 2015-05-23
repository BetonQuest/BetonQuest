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
import pl.betoncraft.betonquest.utils.Debug;

/**
 * One of specified conditions has to be true
 * 
 * @author Co0sh
 */
public class AlternativeCondition extends Condition {

    private String[] conditions;

    public AlternativeCondition(String playerID, String pack, String instructions) {
        super(playerID, pack, instructions);
        String[] parts = instructions.split(" ");
        if (parts.length < 2) {
            Debug.error("Conditions not defined in: " + instructions);
            isOk = false;
            return;
        }
        conditions = parts[1].split(",");
    }

    @Override
    public boolean isMet() {
        if (!isOk) {
            Debug.error("There was an error, returning false.");
            return false;
        }
        for (String condition : conditions) {
            String condName;
            String packName;
            if (condition.contains(".")) {
                String[] parts = condition.split("\\.");
                condName = parts[1];
                packName = parts[0];
            } else {
                condName = condition;
                packName = super.packName;
            }
            if (BetonQuest.condition(playerID, packName, condName)) {
                return true;
            }
        }
        return false;
    }
}
