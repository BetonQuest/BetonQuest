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

import org.bukkit.inventory.ItemStack;

import pl.betoncraft.betonquest.core.Condition;
import pl.betoncraft.betonquest.core.QuestItem;
import pl.betoncraft.betonquest.events.TakeEvent;
import pl.betoncraft.betonquest.inout.PlayerConverter;

/**
 * Having item in inventory condition, instrucion string: "hand type:DIAMOND_SWORD enchants:DAMAGE_ALL:3,KNOCKBACK:1 name:Siekacz"
 * @author Co0sh
 */
public class HandCondition extends Condition {
	
	private QuestItem questItem;

	/**
	 * Constructor method
	 * @param playerID
	 * @param instructions
	 */
	public HandCondition(String playerID, String instructions) {
		super(playerID, instructions);
		String[] parts = instructions.split(" ");
		for (String part : parts) {
			if (part.contains("item:")) {
				questItem = new QuestItem(part.substring(5));
			}
		}
	}

	@Override
	public boolean isMet() {
		ItemStack item = PlayerConverter.getPlayer(playerID).getItemInHand();
		if (TakeEvent.isItemEqual(item, questItem)) {
			return true;
		}
		return false;
	}

}
