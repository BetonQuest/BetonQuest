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
package pl.betoncraft.betonquest.core;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.inout.ConfigInput;

import com.google.common.collect.Lists;

/**
 * Represents player's journal
 * @author Co0sh
 */
public class Journal {
	
	private List<Pointer> pointers = new ArrayList<Pointer>();
	private List<String> texts = new ArrayList<String>();
	
	public Journal(String playerID) {
		JournalRes res = BetonQuest.getInstance().getJournalRes().get(playerID);
		while (res.next()) {
			pointers.add(new Pointer(res.getRow().getPointer(), res.getRow().getTimestamp()));
		}
		BetonQuest.getInstance().getJournalRes().remove(playerID);
		generateTexts();
	}
	
	public List<Pointer> getPointers() {
		return pointers;
	}
	
	public void addPointer(Pointer pointer) {
		pointers.add(pointer);
		generateTexts();
	}
	
	public List<String> getText() {
		return Lists.reverse(texts);
	}
	
	private void generateTexts() {
		texts.clear();
		for (Pointer pointer : pointers) {
			String date = new SimpleDateFormat("dd.MM.yyyy HH:mm").format(pointer.getTimestamp());
			String day = "§" + ConfigInput.getString("config.journal_colors.date.day") + date.split(" ")[0];
			String hour = "§" + ConfigInput.getString("config.journal_colors.date.hour") + date.split(" ")[1];
			texts.add(day + " " + hour + "§" + ConfigInput.getString("config.journal_colors.text") + "\n" + ConfigInput.getString("journal." + pointer.getPointer()));
		}
	}

	public void clear() {
		texts.clear();
		pointers.clear();
	}
}
