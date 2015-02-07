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
package pl.betoncraft.betonquest.compatibility.vault;

import org.bukkit.entity.Player;

import pl.betoncraft.betonquest.compatibility.Compatibility;
import pl.betoncraft.betonquest.core.QuestEvent;
import pl.betoncraft.betonquest.inout.PlayerConverter;

/**
 * @author co0sh
 *
 */
public class MoneyEvent extends QuestEvent {

	/**
	 * @param playerID
	 * @param instructions
	 */
	@SuppressWarnings("deprecation")
	public MoneyEvent(String playerID, String instructions) {
		super(playerID, instructions);
		double amount = Double.parseDouble(instructions.split(" ")[1]);
		Player player = PlayerConverter.getPlayer(playerID);
		if (amount > 0) {
			Compatibility.getEconomy().depositPlayer(player.getName(), amount);
		} else if (amount < 0) {
			amount = -amount;
			Compatibility.getEconomy().withdrawPlayer(player.getName(), amount);
		}
	}

}
