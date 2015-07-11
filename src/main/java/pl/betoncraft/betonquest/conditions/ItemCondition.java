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
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.QuestItem;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Requires the player to have specified amount of items in the inventory
 * 
 * @author Jakub Sapalski
 */
public class ItemCondition extends Condition {

    private final List<Item> questItems = new ArrayList<>();

    public ItemCondition(String packName, String instructions)
            throws InstructionParseException {
        super(packName, instructions);
        String[] parts = instructions.split(" ");
        if (parts.length < 2) {
            throw new InstructionParseException("Items not defined");
        }
        String items = parts[1];
        for (String item : items.split(",")) {
            String[] itemParts = item.split(":");
            String name = itemParts[0];
            int amount = 1;
            if (itemParts.length > 1 && itemParts[1].matches("\\d+")) {
                try {
                    amount = Integer.parseInt(item.split(":")[1]);
                } catch (NumberFormatException e) {
                    throw new InstructionParseException(
                            "Cannot parse item amount");
                }
            }
            String itemInstruction = pack.getString("items." + name);
            if (itemInstruction == null) {
                throw new InstructionParseException("Item not defined: " + name);
            }
            QuestItem questItem = new QuestItem(itemInstruction);
            questItems.add(new Item(questItem, amount));
        }
    }

    @Override
    public boolean check(String playerID) {
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
