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
package pl.betoncraft.betonquest.compatibility.legendquest;

import me.sablednah.legendquest.Main;
import org.bukkit.Bukkit;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.api.Variable;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Resolves to player's karma.
 *
 * @author Jakub Sapalski
 */
public class LQKarmaVariable extends Variable {

    private Main lq;
    private Type type;
    private int amount;

    public LQKarmaVariable(Instruction instruction) throws InstructionParseException {
        super(instruction);
        if (instruction.next().equalsIgnoreCase("amount")) {
            type = Type.AMOUNT;
        } else if (instruction.current().toLowerCase().startsWith("left:")) {
            type = Type.LEFT;
            try {
                amount = Integer.parseInt(instruction.current().substring(5));
            } catch (NumberFormatException e) {
                throw new InstructionParseException("Could not parse karmma amount");
            }
        }
        lq = ((Main) Bukkit.getPluginManager().getPlugin("LegendQuest"));
    }

    @Override
    public String getValue(String playerID) {
        long current = lq.getPlayers().getPC(PlayerConverter.getPlayer(playerID)).karma;
        switch (type) {
            case AMOUNT:
                return Long.toString(current);
            case LEFT:
                return Long.toString(amount - current);
            default:
                return "";
        }
    }

    private enum Type {
        AMOUNT, LEFT
    }

}
