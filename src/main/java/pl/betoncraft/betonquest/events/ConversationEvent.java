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
import pl.betoncraft.betonquest.conversation.Conversation;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Fires the conversation for the player
 * 
 * @author Jakub Sapalski
 */
public class ConversationEvent extends QuestEvent {

	private final String pack;
	private final String conv;

	public ConversationEvent(String packName, String instructions) throws InstructionParseException {
		super(packName, instructions);
		String[] parts = instructions.split(" ");
		if (parts.length < 2) {
			throw new InstructionParseException("Conversation not defined");
		}
		String convID = parts[1];
		if (convID.contains(".")) {
			String[] parts2 = convID.split("\\.");
			pack = parts2[0];
			conv = parts2[1];
		} else {
			pack = super.pack.getName();
			conv = convID;
		}
	}

	@Override
	public void run(String playerID) {
		new Conversation(playerID, pack, conv, PlayerConverter.getPlayer(playerID).getLocation());
	}
}
