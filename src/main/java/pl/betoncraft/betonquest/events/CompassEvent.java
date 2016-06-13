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

import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.api.QuestEvent;

/**
 * Adds a compass specific tag to the player.
 * 
 * @author Jakub Sapalski
 */
public class CompassEvent extends QuestEvent {

	private TagEvent tag;

	public CompassEvent(String packName, String instructions) throws InstructionParseException {
		super(packName, instructions);
		persistent = true;
		String[] parts = instructions.split(" ");
		if (parts.length < 3) {
			throw new InstructionParseException("Not enough arguments");
		}
		String action = (parts[1].equalsIgnoreCase("add")) ? "add" : "del";
		String compass = "compass-" + parts[2];
		tag = new TagEvent(packName, "tag " + action + " " + compass);
	}

	@Override
	public void run(String playerID) {
		tag.run(playerID);
	}

}
