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

import java.util.Date;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.Journal;
import pl.betoncraft.betonquest.Pointer;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.config.Config;

/**
 * Adds the entry to player's journal
 * 
 * @author Jakub Sapalski
 */
public class JournalEvent extends QuestEvent {

	private final String name;
	private final boolean add;

	public JournalEvent(String packName, String instructions) throws InstructionParseException {
		super(packName, instructions);
		String[] parts = instructions.split(" ");
		if (parts.length < 2) {
			throw new InstructionParseException("Not enough arguments");
		}
		if (parts.length < 3) {
			add = false;
			name = null;
		} else {
			add = parts[1].equalsIgnoreCase("add");
			name = packName + "." + parts[2];
		}
	}

	@Override
	public void run(String playerID) {
		Journal journal = BetonQuest.getInstance().getPlayerData(playerID).getJournal();
		if (add) {
			journal.addPointer(new Pointer(name, new Date().getTime()));
			Config.sendMessage(playerID, "new_journal_entry", null, "journal");
		} else if (name != null) {
			journal.removePointer(name);
		}
		journal.update();
	}

}
