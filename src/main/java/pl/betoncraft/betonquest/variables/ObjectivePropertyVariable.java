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
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.api.Variable;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.id.ObjectiveID;

/**
 * Resolves to a specified property of an objective.
 *
 * @author Jakub Sapalski
 */
public class ObjectivePropertyVariable extends Variable {

    private ObjectiveID objective;
    private String propertyName;

    public ObjectivePropertyVariable(final Instruction instruction) throws InstructionParseException {
        super(instruction);
        objective = instruction.getObjective();
        propertyName = instruction.next();
    }

    @Override
    public String getValue(final String playerID) {
        final Objective objective = BetonQuest.getInstance().getObjective(this.objective);
        // existence of an objective is checked now because it may not exist yet
        // when variable is created (in case of "message" event)
        if (objective == null) {
            return "";
        }
        return objective.containsPlayer(playerID) ? objective.getProperty(propertyName, playerID) : "";
    }

}
