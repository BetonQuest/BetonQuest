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

import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.VariableNumber;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Checks Y height player is at (must be below)
 * 
 * @author BYK
 */
public class HeightCondition extends Condition {

    private final VariableNumber height;

    public HeightCondition(String packName, String instructions)
            throws InstructionParseException {
        super(packName, instructions);
        String[] parts = instructions.split(" ");
        if (parts.length < 2) {
            throw new InstructionParseException("Height not defined");
        }
        if (parts[1].matches("\\-?\\d+\\.?\\d*")) {
            try {
                height = new VariableNumber(packName, parts[1]);
            } catch (NumberFormatException e) {
                throw new InstructionParseException("Could not parse height");
            }
        } else {
            String[] locParts = parts[1].split(";");
            if (locParts.length < 4) {
                throw new InstructionParseException("Could not parse height");
            }
            try {
                height = new VariableNumber(packName, locParts[1]);
            } catch (NumberFormatException e) {
                throw new InstructionParseException("Could not parse height");
            }
        }
    }

    @Override
    public boolean check(String playerID) {
        if (PlayerConverter.getPlayer(playerID).getLocation().getY() < height.getDouble(playerID)) {
            return true;
        }
        return false;
    }

}
