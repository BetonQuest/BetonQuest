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
package pl.betoncraft.betonquest.inout;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.core.GlobalLocation;

/**
 * 
 * @author Co0sh
 */
public class GlobalLocations extends BukkitRunnable {
	
	private List<GlobalLocation> locations = new ArrayList<GlobalLocation>();
	private final List<GlobalLocation> finalLocations;

	public GlobalLocations() {
		String rawGlobalLocations = ConfigInput.getString("config.global_locations");
		if (rawGlobalLocations.equals("")) {
			finalLocations = null;
			return;
		}
		String[] parts = rawGlobalLocations.split(",");
		for (String event : parts) {
			locations.add(new GlobalLocation(event));
		}
		finalLocations = locations;
	}
	
	@Override
	public void run() {
		// do nothing if there is no defined locations
		if (finalLocations == null) {
			this.cancel();
			return;
		}
		// loop all online players
		Player[] players = Bukkit.getOnlinePlayers();
		for (Player player : players) {
			// for each player loop all available locations
			locations:
			for (GlobalLocation location : finalLocations) {
				// if location is not set, stop everything, there is an error in config
				if (location.getLocation() == null) {
					continue locations;
				}
				// if player is inside location, do stuff
				if (player.getLocation().getWorld().getName().equals(location.getLocation().getWorld()) && player.getLocation().distance(new Location(Bukkit.getWorld(location.getLocation().getWorld()), location.getLocation().getX(), location.getLocation().getY(), location.getLocation().getZ())) < location.getDistance()) {
					// check if player has already triggered this location
					if (BetonQuest.getInstance().havePlayerTag(PlayerConverter.getID(player), "global_" + location.getTag())) {
						continue locations;
					}
					// check all conditions
					if (location.getConditions() != null) {
						for (String condition : location.getConditions()) {
							if (!BetonQuest.condition(PlayerConverter.getID(player), condition)) {
								// if some conditions are not met, skip to next location
								continue locations;
							}
						}
					}
					// set the tag, player has triggered this location
					BetonQuest.getInstance().putPlayerTag(PlayerConverter.getID(player), "global_" + location.getTag());
					// fire all events for the location
					for (String event : location.getEvents()) {
						BetonQuest.event(PlayerConverter.getID(player), event);
					}
				}
			}
		}
	}
}
