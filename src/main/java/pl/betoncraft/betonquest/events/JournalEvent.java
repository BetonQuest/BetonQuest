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
package pl.betoncraft.betonquest.events;

import java.sql.Timestamp;
import java.util.Date;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.core.Pointer;
import pl.betoncraft.betonquest.core.QuestEvent;
import pl.betoncraft.betonquest.inout.ConfigInput;
import pl.betoncraft.betonquest.inout.JournalBook;
import pl.betoncraft.betonquest.inout.SimpleTextOutput;

/**
 * 
 * @author Co0sh
 */
public class JournalEvent extends QuestEvent {

	/**
	 * Constructor method
	 * @param playerID
	 * @param instructions
	 */
	public JournalEvent(String playerID, String instructions) {
		super(playerID, instructions);
		BetonQuest.getInstance().getJournal(playerID).addPointer(new Pointer(instructions.split(" ")[1], new Timestamp(new Date().getTime())));
		JournalBook.updateJournal(playerID);
		SimpleTextOutput.sendSystemMessage(playerID, ConfigInput.getString("messages." + ConfigInput.getString("config.language") + ".new_journal_entry"), ConfigInput.getString("config.sounds.journal"));
	}
	
}
