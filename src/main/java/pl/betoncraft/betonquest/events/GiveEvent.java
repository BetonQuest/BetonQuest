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

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.config.ConfigHandler;
import pl.betoncraft.betonquest.core.QuestItem;
import pl.betoncraft.betonquest.utils.Debug;
import pl.betoncraft.betonquest.utils.PlayerConverter;
import pl.betoncraft.betonquest.utils.Utils;

/**
 * 
 * @author Co0sh
 */
public class GiveEvent extends QuestEvent {

    private QuestItem questItem;
    private int amount = 1;

    /**
     * Constructor method
     * 
     * @param playerID
     * @param instructions
     */
    public GiveEvent(String playerID, String instructions) {
        super(playerID, instructions);
        // the event cannot be fired for offline players
        if (PlayerConverter.getPlayer(playerID) == null) {
            Debug.info("Player " + playerID + " is offline, cannot fire event");
            return;
        }

        String[] parts = instructions.split(" ");
        String[] items = parts[1].split(",");
        for (String rawItem : items) {
            String itemName = rawItem.split(":")[0];
            if (rawItem.split(":").length > 1) {
                amount = Integer.parseInt(rawItem.split(":")[1]);
            }
            questItem = new QuestItem(ConfigHandler.getString("items." + itemName));
            while (amount > 0) {
                int stackSize;
                if (amount > 64) {
                    stackSize = 64;
                } else {
                    stackSize = amount;
                }
                ItemStack item = Utils.generateItem(questItem, stackSize);
                Player player = PlayerConverter.getPlayer(playerID);
                HashMap<Integer, ItemStack> left = player
                        .getInventory().addItem(item);
                for (Integer leftNumber : left.keySet()) {
                    ItemStack itemStack = left.get(leftNumber);
                    if (Utils.isQuestItem(itemStack)) {
                        BetonQuest.getInstance().getDBHandler(playerID).addItem(itemStack, stackSize);
                    } else {
                        player.getWorld().dropItem(player.getLocation(), itemStack);
                    }
                }
                amount = amount - stackSize;
            }
        }
    }
}
