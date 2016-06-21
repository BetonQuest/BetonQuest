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
 * Puts items in a specified chest.
 * 
 * @author Jakub Sapalski
 */
public class ChestGiveEvent extends QuestEvent {

	private final Item[] questItems;
	private final LocationData loc;

	public ChestGiveEvent(String packName, String instructions) throws InstructionParseException {
		super(packName, instructions);
		staticness = true;
		persistent = true;
		String[] parts = instructions.split(" ");
		if (parts.length < 3) {
			throw new InstructionParseException("Not enough arguments");
		}
		// extract location
		loc = new LocationData(packName, parts[1]);
		// extract items
		String[] items = parts[2].split(",");
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
		Block block = loc.getLocation(playerID).getBlock();
		InventoryHolder chest;
		try {
			chest = (InventoryHolder) block.getState();
		} catch (ClassCastException e) {
			throw new QuestRuntimeException("Trying to put items in chest, but there's no chest! Location: X"
					+ block.getX() + " Y" + block.getY() + " Z" + block.getZ());
		}
		for (Item theItem : questItems) {
			QuestItem questItem = theItem.getItem();
			int amount = theItem.getAmount().getInt(playerID);
			while (amount > 0) {
				int stackSize;
				if (amount > 64) {
					stackSize = 64;
				} else {
					stackSize = amount;
				}
				ItemStack item = questItem.generateItem(stackSize);
				HashMap<Integer, ItemStack> left = chest.getInventory().addItem(item);
				for (Integer leftNumber : left.keySet()) {
					ItemStack itemStack = left.get(leftNumber);
					block.getWorld().dropItem(block.getLocation(), itemStack);
				}
				amount = amount - stackSize;
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
