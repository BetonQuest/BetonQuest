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

import java.util.ArrayList;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.QuestRuntimeException;
import pl.betoncraft.betonquest.VariableNumber;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.utils.Utils;

/**
 * Fires specified events for every player in the party
 * 
 * @author Jakub Sapalski
 */
public class PartyEvent extends QuestEvent {

	private String[] conditions;
	private String[] events;
	private VariableNumber range;

	public PartyEvent(String packName, String instructions) throws InstructionParseException {
		super(packName, instructions);
		String[] parts = instructions.split(" ");
		if (parts.length < 4) {
			throw new InstructionParseException("Not enough arguments");
		}
		// load conditions and events
		conditions = parts[2].split(",");
		for (int i = 0; i < conditions.length; i++) {
			conditions[i] = Utils.addPackage(packName, conditions[i]);
		}
		events = parts[3].split(",");
		for (int i = 0; i < events.length; i++) {
			events[i] = Utils.addPackage(packName, events[i]);
		}
		// load the range
		try {
			range = new VariableNumber(packName, parts[1]);
		} catch (NumberFormatException e) {
			throw new InstructionParseException("Cannot parse range");
		}
	}

	@Override
	public void run(String playerID) throws QuestRuntimeException {
		ArrayList<String> members = Utils.getParty(playerID, range.getDouble(playerID), pack.getName(), conditions);
		for (String memberID : members) {
			for (String event : events) {
				BetonQuest.event(memberID, event);
			}
		}
	}

}
