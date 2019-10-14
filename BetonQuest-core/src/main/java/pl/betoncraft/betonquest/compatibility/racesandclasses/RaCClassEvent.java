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

import de.tobiyas.racesandclasses.APIs.ClassAPI;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Sets RaC class.
 *
 * @author Jakub Sapalski
 */
public class RaCClassEvent extends QuestEvent {

    private String className;

    public RaCClassEvent(Instruction instruction) throws InstructionParseException {
        super(instruction);
        className = instruction.next();
        if (!ClassAPI.getAllClassNames().contains(className)) {
            throw new InstructionParseException("No such class: " + className);
        }
    }

    @Override
    public void run(String playerID) {
        ClassAPI.addPlayerToClass(PlayerConverter.getPlayer(playerID), className);
    }

}
