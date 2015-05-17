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
package pl.betoncraft.betonquest.compatibility;

import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.utils.Debug;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Checks if the player has specified amount of Vault money
 * 
 * @author Co0sh
 */
public class MoneyCondition extends Condition {

    private double amount = 0;

    public MoneyCondition(String playerID, String instructions) {
        super(playerID, instructions);
        String[] parts = instructions.split(" ");
        if (parts.length < 2) {
            Debug.error("Money amount not specified in: " + instructions);
            isOk = false;
            return;
        }
        try {
            amount = Double.parseDouble(parts[1]);
            if (amount < 0) {
                amount = 0;
            }
        } catch (NumberFormatException e) {
            Debug.error("Could not parse money amount in: " + instructions);
            isOk = false;
            return;
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isMet() {
        if (!isOk) {
            Debug.error("There was an error, returning false.");
            return false;
        }
        return Compatibility.getEconomy()
                .has(PlayerConverter.getPlayer(playerID).getName(), amount);
    }

}
