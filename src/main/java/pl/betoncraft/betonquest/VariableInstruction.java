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
package pl.betoncraft.betonquest;

import pl.betoncraft.betonquest.config.ConfigPackage;
import pl.betoncraft.betonquest.id.ID;

public class VariableInstruction extends Instruction {

    public VariableInstruction(final ConfigPackage pack, final ID variableIdentifier, final String instruction) {
        super(pack, variableIdentifier, instruction);
        if (!instruction.startsWith("%") && !instruction.endsWith("%")) {
            throw new IllegalArgumentException("Variable instruction does not start and end with '%' character");
        }
        super.instruction = instruction.substring(1, instruction.length() - 1);
        super.parts = super.instruction.split("\\.");
    }

}
