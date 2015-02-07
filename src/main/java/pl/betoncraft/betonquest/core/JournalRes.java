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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Converts ResultSet to readable and persistent data object. Works exactly the same as ResultSet
 * @author Co0sh
 */
public class JournalRes {
	
	private List<JournalResRow> rows = new ArrayList<JournalResRow>();
	private int iterator;
	
	public JournalRes(ResultSet res) {
		try {
			while (res.next()) {
				rows.add(new JournalResRow(res.getTimestamp("date"), res.getString("pointer")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		iterator = -1;
	}
	
	/**
	 * Moves cursor to next place and returns if there is anything there
	 * @return
	 */
	public boolean next() {
		iterator++;
		if (iterator < rows.size()) {
			return true;
		}
		return false;
	}
	
	/**
	 * Returns row representing one journal entry
	 * @return
	 */
	public JournalResRow getRow() {
		return rows.get(iterator);
	}

}
