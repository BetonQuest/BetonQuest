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
package pl.betoncraft.betonquest.compatibility.vault;

import org.bukkit.entity.Player;

import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.compatibility.Compatibility;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Modifies player's balance
 * 
 * @author Jakub Sapalski
 */
public class MoneyEvent extends QuestEvent {
    
    private final double amount;

    public MoneyEvent(String packName, String instructions)
            throws InstructionParseException {
        super(packName, instructions);
        String[] parts = instructions.split(" ");
        if (parts.length < 2) {
            throw new InstructionParseException("Not enough arguments");
        }
        try {
            amount = Double.parseDouble(parts[1]);
        } catch (NumberFormatException e) {
            throw new InstructionParseException("Could not parse money amount");
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void run(String playerID) {
        Player player = PlayerConverter.getPlayer(playerID);
        if (amount > 0) {
            Compatibility.getEconomy().depositPlayer(player.getName(), amount);
        } else if (amount < 0) {
            Compatibility.getEconomy().withdrawPlayer(player.getName(), -amount);
        }
    }
}
