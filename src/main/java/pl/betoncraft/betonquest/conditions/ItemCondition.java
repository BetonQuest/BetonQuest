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
import pl.betoncraft.betonquest.utils.PlayerConverter;
import pl.betoncraft.betonquest.utils.Utils;

/**
 * Having item in inventory condition, instrucion string:
 * "item type:DIAMOND_SWORD amount:1 enchants:DAMAGE_ALL:3,KNOCKBACK:1 name:Siekacz --inverted"
 * 
 * @author Co0sh
 */
public class ItemCondition extends Condition {

    private QuestItem questItem;
    private int amount = 1;

    /**
     * Constructor method
     * 
     * @param playerID
     * @param instructions
     */
    public ItemCondition(String playerID, String instructions) {
        super(playerID, instructions);
        String[] parts = instructions.split(" ");
        for (String part : parts) {
            if (part.contains("item:")) {
                questItem = new QuestItem(part.substring(5));
            }
            if (part.contains("amount:")) {
                amount = Integer.valueOf(part.substring(7));
            }
        }
    }

    @Override
    public boolean isMet() {
        ItemStack[] items = PlayerConverter.getPlayer(playerID).getInventory().getContents();
        for (ItemStack item : items) {
            if (item == null) {
                continue;
            }
            if (!Utils.isItemEqual(item, questItem)) {
                continue;
            }
            amount = amount - item.getAmount();
            if (amount <= 0) {
                return true;
            }
        }
        return false;
    }

}
