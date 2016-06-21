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
package pl.betoncraft.betonquest.conditions;

import java.util.ArrayList;

import org.bukkit.block.Block;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.QuestItem;
import pl.betoncraft.betonquest.QuestRuntimeException;
import pl.betoncraft.betonquest.VariableNumber;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.utils.LocationData;

/**
 * Checks if the chest contains specified items.
 * 
 * @author Jakub Sapalski
 */
public class ChestItemCondition extends Condition {

	private final ArrayList<Item> questItems = new ArrayList<>();
	private final LocationData loc;

	public ChestItemCondition(String packName, String instructions) throws InstructionParseException {
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
		String items = parts[2];
		for (String item : items.split(",")) {
			String[] itemParts = item.split(":");
			String name = itemParts[0];
			VariableNumber amount = new VariableNumber(1);
			if (itemParts.length > 1) {
				try {
					amount = new VariableNumber(packName, item.split(":")[1]);
				} catch (NumberFormatException e) {
					throw new InstructionParseException("Cannot parse item amount");
				}
			}
			QuestItem questItem = QuestItem.newQuestItem(packName, name);
			questItems.add(new Item(questItem, amount));
		}
	}

	@Override
	public boolean check(String playerID) throws QuestRuntimeException {
		Block block = loc.getLocation(playerID).getBlock();
		InventoryHolder chest;
		try {
			chest = (InventoryHolder) block.getState();
		} catch (ClassCastException e) {
			throw new QuestRuntimeException("Trying to check items in a chest, but there's no chest! Location: X" + block.getX() + " Y"
					+ block.getY() + " Z" + block.getZ());
		}
		int counter = 0;
		for (Item questItem : questItems) {
			int amount = questItem.getAmount().getInt(playerID);
			ItemStack[] inventoryItems = chest.getInventory().getContents();
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
		}
		if (counter == questItems.size()) {
			return true;
		}
		return false;
	}

	private class Item {

		private QuestItem questItem;
		private VariableNumber amount;

		public Item(QuestItem questItem, VariableNumber amount) {
			this.questItem = questItem;
			this.amount = amount;
		}

		public boolean isItemEqual(ItemStack item) {
			return questItem.equalsI(item);
		}

		public VariableNumber getAmount() {
			return amount;
		}
	}

}
