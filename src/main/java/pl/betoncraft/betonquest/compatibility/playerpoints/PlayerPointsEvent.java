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
package pl.betoncraft.betonquest.compatibility.playerpoints;

import java.util.UUID;

import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.Bukkit;

import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.QuestRuntimeException;
import pl.betoncraft.betonquest.VariableNumber;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Adds/removes/multiplies/divides PlayerPoints points.
 * 
 * @author Jakub Sapalski
 */
public class PlayerPointsEvent extends QuestEvent {

	private VariableNumber count;
	private boolean multi;
	private PlayerPointsAPI api;

	public PlayerPointsEvent(String packName, String instructions) throws InstructionParseException {
		super(packName, instructions);
		String[] parts = instructions.split(" ");
		if (parts.length < 2) {
			throw new InstructionParseException("Not enough arguments");
		}
		if (parts[1].startsWith("*")) {
			multi = true;
			parts[1] = parts[1].replace("*", "");
		} else {
			multi = false;
		}
		try {
			count = new VariableNumber(packName, parts[1]);
		} catch (NumberFormatException e) {
			throw new InstructionParseException("Could not parse point count");
		}
		api = ((PlayerPoints) Bukkit.getPluginManager().getPlugin("PlayerPoints")).getAPI();
	}

	@Override
	public void run(String playerID) throws QuestRuntimeException {
		UUID uuid = PlayerConverter.getPlayer(playerID).getUniqueId();
		if (multi) {
			api.set(uuid, (int) Math.floor(api.look(uuid) * count.getDouble(playerID)));
		} else {
			double i = count.getDouble(playerID);
			if (i < 0) {
				api.take(uuid, (int) Math.floor(-i));
			} else {
				api.give(uuid, (int) Math.floor(i));
			}
		}
	}

}
