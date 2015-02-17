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
package pl.betoncraft.betonquest.events;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.core.QuestItem;
import pl.betoncraft.betonquest.utils.PlayerConverter;
import pl.betoncraft.betonquest.utils.Utils;

/**
 * 
 * @author Co0sh
 */
public class TakeEvent extends QuestEvent {

    private QuestItem questItem;
    private int amount = 1;

    /**
     * Constructor method
     * 
     * @param playerID
     * @param instructions
     */
    public TakeEvent(String playerID, String instructions) {
        super(playerID, instructions);

        String[] parts = instructions.split(" ");
        String[] itemsToRemove = parts[1].split(",");
        for (String rawItem : itemsToRemove) {
            String itemName = rawItem.split(":")[0];
            if (rawItem.split(":").length > 1) {
                amount = Integer.parseInt(rawItem.split(":")[1]);
            }
            questItem = new QuestItem(itemName);
            ItemStack[] items = PlayerConverter.getPlayer(playerID).getInventory().getContents();
            for (ItemStack item : items) {
                if (Utils.isItemEqual(item, questItem)) {
                    if (item.getAmount() - amount <= 0) {
                        amount = amount - item.getAmount();
                        item.setType(Material.AIR);
                    } else {
                        item.setAmount(item.getAmount() - amount);
                        amount = 0;
                    }
                    if (amount <= 0) {
                        break;
                    }
                }
            }
            PlayerConverter.getPlayer(playerID).getInventory().setContents(items);
        }
    }
}
