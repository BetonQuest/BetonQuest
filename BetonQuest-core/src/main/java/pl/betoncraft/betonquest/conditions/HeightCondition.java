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
import pl.betoncraft.betonquest.utils.LocationData;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Checks Y height player is at (must be below)
 *
 * @author BYK
 */
public class HeightCondition extends Condition {

    private final VariableNumber height;

    public HeightCondition(Instruction instruction) throws InstructionParseException {
        super(instruction);
        String string = instruction.next();
        String packName = instruction.getPackage().getName();
        if (string.matches("\\-?\\d+\\.?\\d*")) {
            try {
                height = new VariableNumber(packName, string);
            } catch (NumberFormatException e) {
                throw new InstructionParseException("Could not parse height", e);
            }
        } else {
            try {
                height = new VariableNumber(new LocationData(packName, string).getLocation(null).getY());
            } catch (QuestRuntimeException e) {
                throw new InstructionParseException("Could not parse height", e);
            }
        }
    }

    @Override
    public boolean check(String playerID) throws QuestRuntimeException {
        return PlayerConverter.getPlayer(playerID).getLocation().getY() < height.getDouble(playerID);
    }

}
