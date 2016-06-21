/**
 * BetonQuest - advanced quests for Bukkit
 * Copyright (C) 2016  Jakub "Co0sh" Sapalski
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
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.QuestItem;
import pl.betoncraft.betonquest.QuestRuntimeException;
import pl.betoncraft.betonquest.VariableNumber;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.utils.PlayerConverter;
import pl.betoncraft.betonquest.utils.Utils;

/**
 * Gives the player specified items
 * 
 * @author Jakub Sapalski
 */
public class GiveEvent extends QuestEvent {

	private final Item[] questItems;
	private final boolean notify;

	public GiveEvent(String packName, String instructions) throws InstructionParseException {
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
			VariableNumber amount;
			if (itemParts.length == 1) {
				amount = new VariableNumber(1);
			} else {
				try {
					amount = new VariableNumber(packName, itemParts[1]);
				} catch (NumberFormatException e) {
					throw new InstructionParseException("Wrong number format");
				}
			}
			list.add(new Item(QuestItem.newQuestItem(packName, name), amount));
		}
		Item[] tempQuestItems = new Item[list.size()];
		tempQuestItems = list.toArray(tempQuestItems);
		questItems = tempQuestItems;
	}

	@Override
	public void run(String playerID) throws QuestRuntimeException {
		Player player = PlayerConverter.getPlayer(playerID);
		for (Item theItem : questItems) {
			QuestItem questItem = theItem.getItem();
			VariableNumber amount = theItem.getAmount();
			int amountInt = amount.getInt(playerID);
			if (notify) {
				Config.sendMessage(playerID, "items_given",
						new String[] {
								(questItem.getName() != null) ? questItem.getName()
										: questItem.getMaterial().toString().toLowerCase().replace("_", " "),
								String.valueOf(amountInt) });
			}
			while (amountInt > 0) {
				int stackSize;
				if (amountInt > 64) {
					stackSize = 64;
				} else {
					stackSize = amountInt;
				}
				ItemStack item = questItem.generateItem(stackSize);
				HashMap<Integer, ItemStack> left = player.getInventory().addItem(item);
				for (Integer leftNumber : left.keySet()) {
					ItemStack itemStack = left.get(leftNumber);
					if (Utils.isQuestItem(itemStack)) {
						BetonQuest.getInstance().getPlayerData(playerID).addItem(itemStack, stackSize);
					} else {
						player.getWorld().dropItem(player.getLocation(), itemStack);
					}
				}
				amountInt = amountInt - stackSize;
			}
		}
	}

	private class Item {

		private final QuestItem item;
		private final VariableNumber amount;

		public Item(QuestItem item, VariableNumber amount) {
			this.item = item;
			this.amount = amount;
		}

		public QuestItem getItem() {
			return item;
		}

		public VariableNumber getAmount() {
			return amount;
		}
	}
}
