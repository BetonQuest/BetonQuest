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
package pl.betoncraft.betonquest.compatibility.vault;

import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.Variable;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Resolves to amount of money.
 *
 * @author Jakub Sapalski
 */
public class MoneyVariable extends Variable {

    private Type type;
    private int amount;

    public MoneyVariable(Instruction instruction) throws InstructionParseException {
        super(instruction);
        if (instruction.next().equalsIgnoreCase("amount")) {
            type = Type.AMOUNT;
        } else if (instruction.current().toLowerCase().startsWith("left:")) {
            type = Type.LEFT;
            try {
                amount = Integer.parseInt(instruction.current().substring(5));
            } catch (NumberFormatException e) {
                throw new InstructionParseException("Could not parse money amount", e);
            }
        }
    }

    @Override
    public String getValue(String playerID) {
        switch (type) {
            case AMOUNT:
                return String.valueOf(VaultIntegrator.getEconomy().getBalance(PlayerConverter.getPlayer(playerID)));
            case LEFT:
                return String.valueOf(amount - VaultIntegrator.getEconomy().getBalance(PlayerConverter.getPlayer(playerID)));
            default:
                return "";
        }
    }

    private enum Type {
        AMOUNT, LEFT
    }

}
