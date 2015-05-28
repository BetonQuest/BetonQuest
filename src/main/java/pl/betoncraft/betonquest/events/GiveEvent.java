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
import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.core.InstructionParseException;
import pl.betoncraft.betonquest.core.QuestItem;
import pl.betoncraft.betonquest.utils.PlayerConverter;
import pl.betoncraft.betonquest.utils.Utils;

/**
 * Gives the player specified items
 * 
 * @author Jakub Sapalski
 */
public class GiveEvent extends QuestEvent {

    private final Item[]  questItems;
    private final boolean notify;

    public GiveEvent(String packName, String instructions)
            throws InstructionParseException {
        super(packName, instructions);
        String[] parts = instructions.split(" ");
        if (parts.length < 2) {
            throw new InstructionParseException("Not enough arguments");
        }
        String[] items = parts[1].split(",");
        notify = parts.length >= 3 && parts[2].equalsIgnoreCase("notify");
        ArrayList<Item> list = new ArrayList<>();
        for (int i = 0; i < items.length; i++) {
            String rawItem = items[i];
            String[] itemParts = rawItem.split(":");
            String name = itemParts[0];
            int amount;
            if (itemParts.length == 1) {
                amount = 1;
            } else {
                try {
                    amount = Integer.parseInt(itemParts[1]);
                } catch (NumberFormatException e) {
                    throw new InstructionParseException("Wrong number format");
                }
            }
            String itemInstruction = pack.getString("items." + name);
            if (itemInstruction == null) {
                throw new InstructionParseException("Item not defined: " + name);
            }
            list.add(new Item(new QuestItem(itemInstruction), amount));
        }
        Item[] tempQuestItems = new Item[list.size()];
        tempQuestItems = list.toArray(tempQuestItems);
        questItems = tempQuestItems;
    }

    @Override
    public void run(String playerID) {
        Player player = PlayerConverter.getPlayer(playerID);
        for (Item theItem : questItems) {
            QuestItem questItem = theItem.getItem();
            int amount = theItem.getAmount();
            if (notify) {
                player.sendMessage(Config.getMessage("items_given").replaceAll(
                        "%name%",
                        (questItem.getName() != null) ? questItem.getName()
                                : questItem.getMaterial().toString())
                        .replaceAll("%amount%", String.valueOf(amount))
                        .replaceAll("&", "§"));
            }
            while (amount > 0) {
                int stackSize;
                if (amount > 64) {
                    stackSize = 64;
                } else {
                    stackSize = amount;
                }
                ItemStack item = questItem.generateItem(stackSize);
                HashMap<Integer, ItemStack> left =
                        player.getInventory().addItem(item);
                for (Integer leftNumber : left.keySet()) {
                    ItemStack itemStack = left.get(leftNumber);
                    if (Utils.isQuestItem(itemStack)) {
                        BetonQuest.getInstance().getDBHandler(playerID)
                                .addItem(itemStack, stackSize);
                    } else {
                        player.getWorld().dropItem(player.getLocation(),
                                itemStack);
                    }
                }
                amount = amount - stackSize;
            }
        }
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
