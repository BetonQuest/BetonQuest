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
package pl.betoncraft.betonquest.compatibility.racesandclasses;

import de.tobiyas.racesandclasses.APIs.LevelAPI;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.VariableNumber;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Adds or removes RaC experience points.
 *
 * @author Jakub Sapalski
 */
public class RaCExpEvent extends QuestEvent {

    private VariableNumber number;

    public RaCExpEvent(Instruction instruction) throws InstructionParseException {
        super(instruction);
        number = instruction.getVarNum();
    }

    @Override
    public void run(String playerID) throws QuestRuntimeException {
        int i = number.getInt(playerID);
        if (i >= 0) {
            LevelAPI.addExp(PlayerConverter.getPlayer(playerID), i);
        } else {
            LevelAPI.removeExp(PlayerConverter.getPlayer(playerID), -i);
        }
    }

}
