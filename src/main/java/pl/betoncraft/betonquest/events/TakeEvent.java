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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.core.InstructionParseException;
import pl.betoncraft.betonquest.core.QuestItem;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Removes items from player's inventory and/or backpack
 * 
 * @author Jakub Sapalski
 */
public class TakeEvent extends QuestEvent {

    private final Item[]  questItems;
    private final boolean notify;

    public TakeEvent(String packName, String instructions)
            throws InstructionParseException {
        super(packName, instructions);
        String[] parts = instructions.split(" ");
        if (parts.length < 2) {
            throw new InstructionParseException("Not eoungh arguments");
        }
        notify = parts.length >= 3 && parts[2].equalsIgnoreCase("notify");
        String[] itemsToRemove = parts[1].split(",");
        ArrayList<Item> list = new ArrayList<>();
        for (String rawItem : itemsToRemove) {
            String[] rawItemParts = rawItem.split(":");
            String itemName = rawItemParts[0];
            int amount = 1;
            if (rawItemParts.length > 1) {
                try {
                    amount = Integer.parseInt(rawItemParts[1]);
                } catch (NumberFormatException e) {
                    throw new InstructionParseException(
                            "Could not parse item amount");
                }
            }
            String itemInstruction = pack.getString("items." + itemName);
            if (itemInstruction == null) {
                throw new InstructionParseException("Item not defined");
            }
            QuestItem questItem = new QuestItem(itemInstruction);
            list.add(new Item(questItem, amount));
        }
        Item[] tempQuestItems = new Item[list.size()];
        tempQuestItems = list.toArray(tempQuestItems);
        questItems = tempQuestItems;
    }

    @Override
    public void run(String playerID) {
        Player player = PlayerConverter.getPlayer(playerID);
        for (Item item : questItems) {
            QuestItem questItem = item.getItem();
            int amount = item.getAmount();
            if (notify) {
                player.sendMessage(Config.getMessage("items_taken").replaceAll(
                        "%name%",
                        (questItem.getName() != null) ? questItem.getName()
                                : questItem.getMaterial().toString())
                        .replaceAll("%amount%", String.valueOf(amount))
                        .replaceAll("&", "§"));
            }

            //Remove Quest items from player's inventory
            player.getInventory().setContents(
                    removeItems(player.getInventory().getContents(), questItem,
                            amount));

            //Remove Quest items from player's armor slots
            player.getInventory().setArmorContents(
                    removeItems(player.getInventory().getArmorContents(),
                            questItem, amount));

            //Remove Quest items from player's backpack
            if (amount > 0) {
                List<ItemStack> backpack =
                        BetonQuest.getInstance().getDBHandler(playerID)
                                .getBackpack();
                ItemStack[] array = new ItemStack[] {};
                array = backpack.toArray(array);
                LinkedList<ItemStack> list =
                        new LinkedList<>(Arrays.asList(removeItems(array,
                                questItem, amount)));
                while (list.remove(null))
                    BetonQuest.getInstance().getDBHandler(playerID)
                            .setBackpack(list);
            }
        }
    }

    private ItemStack[] removeItems(ItemStack[] items, QuestItem questItem,
            int amount) {
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

    private class Item {

        private final QuestItem item;
        private final int       amount;

        public Item(QuestItem item, int amount) {
            this.item = item;
            this.amount = amount;
        }

        public QuestItem getItem() {
            return item;
        }

        public int getAmount() {
            return amount;
        }
    }
}
