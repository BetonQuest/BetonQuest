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

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.Instruction.Item;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.QuestRuntimeException;
import pl.betoncraft.betonquest.VariableNumber;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.item.QuestItem;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Removes items from player's inventory and/or backpack
 * 
 * @author Jakub Sapalski
 */
public class TakeEvent extends QuestEvent {

	private final Item[] questItems;
	private final boolean notify;

	private int counter;

	public TakeEvent(Instruction instruction) throws InstructionParseException {
		super(instruction);
		questItems = instruction.getItemList();
		notify = instruction.hasArgument("notify");
	}

	@Override
	public void run(String playerID) throws QuestRuntimeException {
		Player player = PlayerConverter.getPlayer(playerID);
		for (Item item : questItems) {
			QuestItem questItem = item.getItem();
			VariableNumber amount = item.getAmount();

			// cache the amount
			counter = amount.getInt(playerID);

			// notify the player
			if (notify) {
				Config.sendNotify(playerID, "items_taken",
						new String[] {
								(questItem.getName() != null) ? questItem.getName()
										: questItem.getMaterial().toString().toLowerCase().replace("_", " "),
								String.valueOf(counter) },
						"items_taken,info");
			}

			// Remove Quest items from player's inventory
			player.getInventory().setContents(removeItems(player.getInventory().getContents(), questItem));

			// Remove Quest items from player's armor slots
			if (counter > 0) {
				player.getInventory()
						.setArmorContents(removeItems(player.getInventory().getArmorContents(), questItem));
			}

			// Remove Quest items from player's backpack
			if (counter > 0) {
				List<ItemStack> backpack = BetonQuest.getInstance().getPlayerData(playerID).getBackpack();
				ItemStack[] array = new ItemStack[] {};
				array = backpack.toArray(array);
				LinkedList<ItemStack> list = new LinkedList<>(Arrays.asList(removeItems(array, questItem)));
				list.removeAll(Collections.singleton(null));
				BetonQuest.getInstance().getPlayerData(playerID).setBackpack(list);
			}
		}
	}

	private ItemStack[] removeItems(ItemStack[] items, QuestItem questItem) {
		for (int i = 0; i < items.length; i++) {
			ItemStack item = items[i];
			if (questItem.compare(item)) {
				if (item.getAmount() - counter <= 0) {
					counter -= item.getAmount();
					items[i] = null;
				} else {
					item.setAmount(item.getAmount() - counter);
					counter = 0;
				}
				if (counter <= 0) {
					break;
				}
			}
		}
		return items;
	}
}
