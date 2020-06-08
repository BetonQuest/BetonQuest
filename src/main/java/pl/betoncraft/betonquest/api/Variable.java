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
package pl.betoncraft.betonquest.api;

import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.config.ConfigPackage;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;

/**
 * Represents a variable in conversations.
 *
 * @author Jakub Sapalski
 */
abstract public class Variable {

    /**
     * Stores instruction string for the condition.
     */
    protected Instruction instruction;

    /**
     * ConfigPackage in which this condition is defined
     */
    protected ConfigPackage pack;

    /**
     * Creates new instance of the variable. The variable should parse the
     * instruction string at this point and extract all the data from it. If
     * anything goes wrong, throw {@link InstructionParseException} with an
     * error message describing the problem.
     *
     * @param instruction the Instruction object representing this variable; you need to
     *                    extract all required data from it and throw
     *                    {@link InstructionParseException} if there is anything wrong
     */
    public Variable(Instruction instruction) {
        this.instruction = instruction;
    }

    /**
     * This method should return a resolved value of variable for given player.
     *
     * @param playerID ID of the player
     * @return the value of this variable
     */
    public abstract String getValue(String playerID);

    @Override
    public String toString() {
        return instruction.getInstruction();
    }
}
