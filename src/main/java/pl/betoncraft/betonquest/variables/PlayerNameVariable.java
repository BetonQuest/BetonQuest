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
package pl.betoncraft.betonquest.variables;

import org.bukkit.entity.Player;

import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.api.Variable;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * This variable resolves into the player's name. It can has optional "display"
 * argument, which will resolve it to the display name.
 * 
 * @author Jakub Sapalski
 */
public class PlayerNameVariable extends Variable {

	private boolean display = false;

	public PlayerNameVariable(String packName, String instruction) throws InstructionParseException {
		super(packName, instruction);
		String[] parts = instruction.replace("%", "").split("\\.");
		if (parts.length > 1 && parts[1].equalsIgnoreCase("display"))
			display = true;
	}

	@Override
	public String getValue(String playerID) {
		Player player = PlayerConverter.getPlayer(playerID);
		return (display) ? player.getDisplayName() : player.getName();
	}

}
