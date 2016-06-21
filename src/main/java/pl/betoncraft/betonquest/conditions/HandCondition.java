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

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.QuestItem;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Holding item in hand condition
 * 
 * @author Jakub Sapalski
 */
public class HandCondition extends Condition {

	private final QuestItem questItem;
	private boolean offhand = false;

	public HandCondition(String packName, String instructions) throws InstructionParseException {
		super(packName, instructions);
		String[] parts = instructions.split(" ");
		if (parts.length < 2) {
			throw new InstructionParseException("Item name not defined");
		}
		questItem = QuestItem.newQuestItem(packName, parts[1]);
		for (String part : parts) {
			if (part.equalsIgnoreCase("offhand")) {
				offhand = true;
				break;
			}
		}
	}

	@Override
	public boolean check(String playerID) {
		PlayerInventory inv = PlayerConverter.getPlayer(playerID).getInventory();
		ItemStack item = (!offhand) ? inv.getItemInMainHand() : inv.getItemInOffHand();
		if (questItem.equalsI(item)) {
			return true;
		}
		return false;
	}

}
