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
package pl.betoncraft.betonquest.variables;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.api.Variable;

/**
 * Resolves to a specified property of an objective.
 * 
 * @author Jakub Sapalski
 */
public class ObjectivePropertyVariable extends Variable {
    
    private Objective objective;
    private String propertyName;

    public ObjectivePropertyVariable(String packName, String instruction)
            throws InstructionParseException {
        super(packName, instruction);
        String[] parts = instruction.replace("%", "").split("\\.");
        if (parts.length != 3) {
            throw new InstructionParseException("Incorrect number of arguments");
        }
        String objectiveID;
        if (parts[1].contains(".")) {
            objectiveID = parts[1];
        } else {
            objectiveID = packName + "." + parts[1];
        }
        objective = BetonQuest.getInstance().getObjective(objectiveID);
        propertyName = parts[2];
    }

    @Override
    public String getValue(String playerID) {
        return (objective.containsPlayer(playerID) ? objective.getProperty(propertyName, playerID) : "");
    }

}
