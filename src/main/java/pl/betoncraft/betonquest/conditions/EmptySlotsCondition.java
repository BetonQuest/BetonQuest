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

import org.bukkit.inventory.ItemStack;

import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.utils.Debug;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Checks if the player has required amount of empty slots in his inventory
 * 
 * @author Coosh
 */
public class EmptySlotsCondition extends Condition {
    
    private int needed = 0;
    private int empty = 0;

    public EmptySlotsCondition(String playerID, String packName, String instructions) {
        super(playerID, packName, instructions);
        String[] parts = instructions.split(" ");
        if (parts.length < 2) {
            Debug.error("Empty space amount not defined in empty condition: " + instructions);
            isOk = false;
            return;
        }
        try {
            needed = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            Debug.error("Cannot parse an integer in: " + instructions);
            isOk = false;
            return;
        }
        for (ItemStack item : PlayerConverter.getPlayer(playerID).getInventory().getContents()) {
            if (item == null) empty++;
        }
    }

    @Override
    public boolean isMet() {
        return empty >= needed;
    }

}
