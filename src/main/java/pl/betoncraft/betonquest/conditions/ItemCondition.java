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

import java.util.ArrayList;
import java.util.List;

import org.bukkit.inventory.ItemStack;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.config.ConfigHandler;
import pl.betoncraft.betonquest.core.QuestItem;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Having item in inventory condition, instrucion string:
 * "item type:DIAMOND_SWORD amount:1 enchants:DAMAGE_ALL:3,KNOCKBACK:1 name:Siekacz --inverted"
 * 
 * @author Co0sh
 */
public class ItemCondition extends Condition {

    private List<Item> questItems = new ArrayList<>();

    /**
     * Constructor method
     * 
     * @param playerID
     * @param instructions
     */
    public ItemCondition(String playerID, String instructions) {
        super(playerID, instructions);
        String items = instructions.split(" ")[1];
        for (String item : items.split(",")) {
            String name = item.split(":")[0];
            int amount = 1;
            if (item.split(":").length > 1 && item.split(":")[1].matches("\\d+")) {
                amount = Integer.parseInt(item.split(":")[1]);
            }
            QuestItem questItem = new QuestItem(ConfigHandler.getString("items." + name));
            questItems.add(new Item(questItem, amount));
        }
    }

    @Override
    public boolean isMet() {
        int counter = 0;
        for (Item questItem : questItems) {
            int amount = questItem.getAmount();
            ItemStack[] inventoryItems = PlayerConverter.getPlayer(playerID).getInventory().getContents();
            for (ItemStack item : inventoryItems) {
                if (item == null) {
                    continue;
                }
                if (!questItem.isItemEqual(item)) {
                    continue;
                }
                amount -= item.getAmount();
                if (amount <= 0) {
                    counter++;
                    break;
                }
            }
            List<ItemStack> backpackItems = BetonQuest.getInstance().getDBHandler(playerID).getBackpack();
            for (ItemStack item : backpackItems) {
                if (item == null) {
                    continue;
                }
                if (!questItem.isItemEqual(item)) {
                    continue;
                }
                amount -= item.getAmount();
                if (amount <= 0) {
                    counter++;
                    break;
                }
            }
        }
        if (counter == questItems.size()) {
            return true;
        }
        return false;
    }
    
    private class Item {
        
        private QuestItem questItem;
        private int amount = 1;
        
        public Item(QuestItem questItem, int amount) {
            this.questItem = questItem;
            this.amount = amount;
        }
        
        public boolean isItemEqual(ItemStack item) {
            return questItem.equalsI(item);
        }

        public int getAmount() {
            return amount;
        }
    }
}
