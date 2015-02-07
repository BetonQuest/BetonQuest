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

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.inout.ConfigInput;
import pl.betoncraft.betonquest.inout.UnifiedLocation;

/**
 * 
 * @author Co0sh
 */
public class GlobalLocation {

	private UnifiedLocation location;
	private String[] conditions;
	private String[] events;
	private int distance;
	private String tag;

	public GlobalLocation(String event) {
		String instructions = ConfigInput.getString("events." + event);
		if (instructions == null) {
			BetonQuest.getInstance().getLogger().severe("Global location not found: " + event);
			return;
		}
		String[] parts = instructions.split(" ");
		String[] rawLocation = parts[2].split(";");
		location = new UnifiedLocation(Double.parseDouble(rawLocation[0]), Double.parseDouble(rawLocation[1]), Double.parseDouble(rawLocation[2]), rawLocation[3]);
		for (String part : parts) {
			if (part.contains("conditions:")) {
				conditions = part.substring(11).split(",");
			}
			if (part.contains("events:")) {
				events = part.substring(7).split(",");
			}
			if (part.contains("tag:")) {
				tag = part.substring(4);
			}
		}
		distance = Integer.parseInt(rawLocation[4]);
	}

	/**
	 * @return the location
	 */
	public UnifiedLocation getLocation() {
		return location;
	}

	/**
	 * @return the conditions
	 */
	public String[] getConditions() {
		return conditions;
	}

	/**
	 * @return the events
	 */
	public String[] getEvents() {
		return events;
	}

	/**
	 * @return the distance
	 */
	public int getDistance() {
		return distance;
	}
	
	/**
	 * @return the tag
	 */
	public String getTag() {
		return tag;
	}
}
