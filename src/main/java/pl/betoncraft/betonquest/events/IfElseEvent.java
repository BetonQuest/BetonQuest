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

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.utils.Utils;

/**
 * Runs one or another event, depending of the condition outcome.
 * 
 * @author Jakub Sapalski
 */
public class IfElseEvent extends QuestEvent {
	
	private String condition;
	private String event;
	private String elseEvent;

	public IfElseEvent(String packName, String instructions) throws InstructionParseException {
		super(packName, instructions);
		String[] parts = instructions.split(" ");
		if (parts.length < 5) {
			throw new InstructionParseException("Not enough arguments");
		}
		condition = Utils.addPackage(packName, parts[1]);
		event = Utils.addPackage(packName, parts[2]);
		if (!parts[3].equalsIgnoreCase("else")) {
			throw new InstructionParseException("Third argument should be 'else'");
		}
		elseEvent = Utils.addPackage(packName, parts[4]);
	}

	@Override
	public void run(String playerID) {
		if (BetonQuest.condition(playerID, condition)) {
			BetonQuest.event(playerID, event);
		} else {
			BetonQuest.event(playerID, elseEvent);
		}
	}

}
