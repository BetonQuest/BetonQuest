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
package pl.betoncraft.betonquest.conditions;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.utils.Debug;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Requires the player to be in specified distance from a location
 * 
 * @author Co0sh
 */
public class LocationCondition extends Condition {

    private Location location;
    private double distance;

    public LocationCondition(String playerID, String instructions) {
        super(playerID, instructions);
        String[] parts = instructions.split(" ");
        if (parts.length < 2) {
            Debug.error("Not enough arguments in: " + instructions);
            isOk = false;
            return;
        }
        String[] partsOfLoc = parts[1].split(",");
        if (partsOfLoc.length != 5) {
            Debug.error("Wrong location format in: " + instructions);
            isOk = false;
            return;
        }
        World world = Bukkit.getWorld(partsOfLoc[3]);
        if (world == null) {
            Debug.error("World " + partsOfLoc[3] + " does not exists.");
            isOk = false;
            return;
        }
        double x,y,z;
        try {
            x = Integer.parseInt(partsOfLoc[0]);
            y = Integer.parseInt(partsOfLoc[1]);
            z = Integer.parseInt(partsOfLoc[2]);
            distance = Integer.parseInt(partsOfLoc[4]);
        } catch (NumberFormatException e) {
            Debug.error("Could not parse location coordinates");
            isOk = false;
            return;
        }
        location = new Location(world, x, y, z);
    }

    @Override
    public boolean isMet() {
        if (!isOk) {
            Debug.error("There was an error, returning false.");
            return false;
        }
        Player player = PlayerConverter.getPlayer(playerID);
        if (!location.getWorld().equals(player.getWorld())) {
            return false;
        }
        if (player.getLocation().distance(location) <= distance) {
            return true;
        }
        return false;
    }

}
