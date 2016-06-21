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

import org.bukkit.block.Block;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.QuestItem;
import pl.betoncraft.betonquest.QuestRuntimeException;
import pl.betoncraft.betonquest.VariableNumber;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.utils.LocationData;

/**
 * Removes items from a chest.
 * 
 * @author Jakub Sapalski
 */
public class ChestTakeEvent extends QuestEvent {

	private Item[] questItems;
	private LocationData loc;

	public ChestTakeEvent(String packName, String instructions) throws InstructionParseException {
		super(packName, instructions);
		staticness = true;
		persistent = true;
		String[] parts = instructions.split(" ");
		if (parts.length < 3) {
			throw new InstructionParseException("Not eoungh arguments");
		}
		// extract location
		loc = new LocationData(packName, parts[1]);
		// extract items
		String[] itemsToRemove = parts[2].split(",");
		ArrayList<Item> list = new ArrayList<>();
		for (String rawItem : itemsToRemove) {
			String[] rawItemParts = rawItem.split(":");
			String itemName = rawItemParts[0];
			VariableNumber amount = new VariableNumber(1);
			if (rawItemParts.length > 1) {
				try {
					amount = new VariableNumber(packName, rawItemParts[1]);
				} catch (NumberFormatException e) {
					throw new InstructionParseException("Could not parse item amount");
				}
			}
			QuestItem questItem = QuestItem.newQuestItem(packName, itemName);
			list.add(new Item(questItem, amount));
		}
		Item[] tempQuestItems = new Item[list.size()];
		tempQuestItems = list.toArray(tempQuestItems);
		questItems = tempQuestItems;
	}

	@Override
	public void run(String playerID) throws QuestRuntimeException {
		Block block = loc.getLocation(playerID).getBlock();
		InventoryHolder chest;
		try {
			chest = (InventoryHolder) block.getState();
		} catch (ClassCastException e) {
			throw new QuestRuntimeException("Trying to take items from chest, but there's no chest! Location: X"
					+ block.getX() + " Y" + block.getY() + " Z" + block.getZ());	
		}
		for (Item item : questItems) {
			QuestItem questItem = item.getItem();
			int amount = item.getAmount().getInt(playerID);
			// Remove Quest items from player's inventory
			chest.getInventory().setContents(removeItems(chest.getInventory().getContents(), questItem, amount));
		}
	}

	private ItemStack[] removeItems(ItemStack[] items, QuestItem questItem, int amount) {
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
