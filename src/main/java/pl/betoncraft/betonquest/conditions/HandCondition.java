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
import pl.betoncraft.betonquest.core.QuestItem;
import pl.betoncraft.betonquest.utils.Debug;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Holding item in hand condition
 * 
 * @author Co0sh
 */
public class HandCondition extends Condition {

    private QuestItem questItem;
    private String itemName;

    public HandCondition(String playerID, String packName, String instructions) {
        super(playerID, packName, instructions);
        String[] parts = instructions.split(" ");
        if (parts.length < 2) {
            Debug.error("Item name not defined in hand condition: " + instructions);
            isOk = false;
            return;
        }
        itemName = parts[1];
        String itemInstruction = pack.getString("items." + itemName);
        if (itemInstruction == null) {
            Debug.error("Item not defined: " + itemName);
            isOk = false;
            return;
        }
        questItem = new QuestItem(itemInstruction);
    }

    @Override
    public boolean isMet() {
        if (!isOk) {
            Debug.error("There was an error, returning false.");
            return false;
        }
        ItemStack item = PlayerConverter.getPlayer(playerID).getItemInHand();
        if (questItem.equalsI(item)) {
            return true;
        }
        return false;
    }

}
