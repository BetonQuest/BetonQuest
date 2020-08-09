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

package pl.betoncraft.betonquest.conditions;

import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.VariableNumber;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;

import java.util.Random;

/**
 * The condition that is met randomly
 *
 * @author BYK
 */
public class RandomCondition extends Condition {

    private final VariableNumber valueMax;
    private final VariableNumber rangeOfRandom;

    public RandomCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, false);
        staticness = true;
        persistent = true;
        final String[] values = instruction.next().split("-");
        if (values.length != 2) {
            throw new InstructionParseException("Wrong randomness format");
        }
        final String packName = instruction.getPackage().getName();
        try {
            valueMax = new VariableNumber(packName, values[0]);
            rangeOfRandom = new VariableNumber(packName, values[1]);
        } catch (NumberFormatException e) {
            throw new InstructionParseException("Cannot parse randomness values", e);
        }
    }

    @Override
    protected Boolean execute(final String playerID) throws QuestRuntimeException {
        final Random generator = new Random();
        final int temp = generator.nextInt(rangeOfRandom.getInt(playerID)) + 1;
        return temp <= valueMax.getInt(playerID);
    }

}
