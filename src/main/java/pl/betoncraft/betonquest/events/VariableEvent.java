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
package pl.betoncraft.betonquest.events;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.id.ObjectiveID;
import pl.betoncraft.betonquest.objectives.VariableObjective;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.util.ArrayList;

public class VariableEvent extends QuestEvent {

    private ObjectiveID id;
    private String key;
    private ArrayList<String> keyVariables;
    private String value;
    private ArrayList<String> valueVariables;

    public VariableEvent(Instruction instruction) throws InstructionParseException {
        super(instruction);
        id = instruction.getObjective();
        key = instruction.next();
        keyVariables = BetonQuest.resolveVariables(key);
        value = instruction.next();
        valueVariables = BetonQuest.resolveVariables(value);
    }

    @Override
    public void run(String playerID) throws QuestRuntimeException {
        Objective obj = BetonQuest.getInstance().getObjective(id);
        if (!(obj instanceof VariableObjective)) {
            throw new QuestRuntimeException(id.getFullID() + " is not a variable objective");
        }
        VariableObjective objective = (VariableObjective) obj;
        String keyReplaced = key;
        for (String v : keyVariables) {
            keyReplaced = keyReplaced.replace(v, BetonQuest.getInstance().getVariableValue(
                    instruction.getPackage().getName(), v, playerID));
        }
        String valueReplaced = value;
        for (String v : valueVariables) {
            valueReplaced = valueReplaced.replace(v, BetonQuest.getInstance().getVariableValue(
                    instruction.getPackage().getName(), v, playerID));
        }
        if (!objective.store(playerID, keyReplaced.replace('_', ' '), valueReplaced.replace('_', ' '))) {
            throw new QuestRuntimeException("Player " + PlayerConverter.getName(playerID) + " does not have '" +
                    id.getFullID() + "' objective, cannot store a variable.");
        }
    }

}
