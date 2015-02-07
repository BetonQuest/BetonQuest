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

import java.sql.Timestamp;

/**
 * 
 * @author Co0sh
 */
public class Pointer {

	private final String pointer;
	private final Timestamp timestamp;
	
	public Pointer(String pointer, Timestamp timestamp) {
		this.pointer = pointer;
		this.timestamp = timestamp;
	}

	/**
	 * @return the pointer
	 */
	public String getPointer() {
		return pointer;
	}

	/**
	 * @return the timestamp
	 */
	public Timestamp getTimestamp() {
		return timestamp;
	}
}
