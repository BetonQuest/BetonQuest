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

import java.util.List;

import org.bukkit.inventory.ItemStack;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.Instruction.Item;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.QuestRuntimeException;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Requires the player to have specified amount of items in the inventory
 * 
 * @author Jakub Sapalski
 */
public class ItemCondition extends Condition {

	private final Item[] questItems;

	public ItemCondition(Instruction instruction) throws InstructionParseException {
		super(instruction);
		questItems = instruction.getItemList();
	}

	@Override
	public boolean check(String playerID) throws QuestRuntimeException {
		int counter = 0;
		for (Item questItem : questItems) {
			int amount = questItem.getAmount().getInt(playerID);
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
			List<ItemStack> backpackItems = BetonQuest.getInstance().getPlayerData(playerID).getBackpack();
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
		if (counter == questItems.length) {
			return true;
		}
		return false;
	}
}
