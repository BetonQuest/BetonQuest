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

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.inventory.ItemStack;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.config.ConfigHandler;
import pl.betoncraft.betonquest.core.QuestItem;
import pl.betoncraft.betonquest.utils.Debug;
import pl.betoncraft.betonquest.utils.PlayerConverter;

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
        // the event cannot be fired for offline players
        if (PlayerConverter.getPlayer(playerID) == null) {
            Debug.info("Player " + playerID + " is offline, cannot fire event");
            return;
        }

        String[] parts = instructions.split(" ");
        String[] itemsToRemove = parts[1].split(",");
        for (String rawItem : itemsToRemove) {
            String itemName = rawItem.split(":")[0];
            if (rawItem.split(":").length > 1) {
                amount = Integer.parseInt(rawItem.split(":")[1]);
            }
            questItem = new QuestItem(ConfigHandler.getString("items." + itemName));
            PlayerConverter.getPlayer(playerID).getInventory().setContents(removeItems(
                    PlayerConverter.getPlayer(playerID).getInventory().getContents()));
            if (amount > 0) {
                List<ItemStack> backpack = BetonQuest.getInstance().getDBHandler(playerID).getBackpack();
                ItemStack[] array = new ItemStack[]{};
                array = backpack.toArray(array);
                LinkedList<ItemStack> list = new LinkedList<>(Arrays.asList(removeItems(array)));
                while (list.remove(null))
                BetonQuest.getInstance().getDBHandler(playerID).setBackpack(list);
            }     
        }
    }

    /**
     * @param items
     */
    private ItemStack[] removeItems(ItemStack[] items) {
        for (int i = 0; i < items.length; i++) {
            ItemStack item = items[i];
            if (questItem.equalsI(item)) {
                if (item.getAmount() - amount <= 0) {
                    amount = amount - item.getAmount();
                    items[i] = null;
                } else {
                    item.setAmount(item.getAmount() - amount);
                    amount = 0;
                }
                if (amount <= 0) {
                    break;
                }
            }
        }
        return items;
    }
}
