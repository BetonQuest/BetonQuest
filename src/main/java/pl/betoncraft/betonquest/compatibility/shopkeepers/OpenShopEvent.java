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
package pl.betoncraft.betonquest.compatibility.shopkeepers;

import java.util.UUID;

import com.nisovin.shopkeepers.Shopkeeper;
import com.nisovin.shopkeepers.ShopkeepersPlugin;

import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * This event opens Shopkeeper trade window.
 * 
 * @author Jakub Sapalski
 */
public class OpenShopEvent extends QuestEvent {

	private Shopkeeper shopkeeper;

	public OpenShopEvent(Instruction instruction) throws InstructionParseException {
		super(instruction);
		String string = instruction.next();
		try {
			shopkeeper = ShopkeepersPlugin.getInstance().getShopkeeper(UUID.fromString(string));
		} catch (IllegalArgumentException e) {
			throw new InstructionParseException("Could not parse UUID: '" + string + "'");
		}
		if (shopkeeper == null) {
			throw new InstructionParseException("Shopkeeper with this UUID does not exist: '" + string + "'");
		}
	}

	@Override
	public void run(String playerID) {
		shopkeeper.openTradingWindow(PlayerConverter.getPlayer(playerID));
	}

}
