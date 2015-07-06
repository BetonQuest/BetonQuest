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
import pl.betoncraft.betonquest.core.InstructionParseException;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Requires the player to be in specified distance from a location
 * 
 * @author Jakub Sapalski
 */
public class LocationCondition extends Condition {

    private final Location location;
    private final double   distance;

    public LocationCondition(String packName, String instructions)
            throws InstructionParseException {
        super(packName, instructions);
        String[] parts = instructions.split(" ");
        if (parts.length < 2) {
            throw new InstructionParseException("Not enough arguments");
        }
        String[] partsOfLoc = parts[1].split(";");
        if (partsOfLoc.length != 5) {
            throw new InstructionParseException("Wrong location format");
        }
        World world = Bukkit.getWorld(partsOfLoc[3]);
        if (world == null) {
            throw new InstructionParseException("World " + partsOfLoc[3]
                    + " does not exists.");
        }
        double x, y, z;
        try {
            x = Double.parseDouble(partsOfLoc[0]);
            y = Double.parseDouble(partsOfLoc[1]);
            z = Double.parseDouble(partsOfLoc[2]);
        } catch (NumberFormatException e) {
            throw new InstructionParseException(
                    "Could not parse location coordinates");
        }
        location = new Location(world, x, y, z);
        try {
            distance = Double.parseDouble(partsOfLoc[4]);
            if (distance <= 0) {
                throw new InstructionParseException("Distance must be positive");
            }
        } catch (NumberFormatException e) {
            throw new InstructionParseException("Could not parse distance");
        }
    }

    @Override
    public boolean check(String playerID) {
        Player player = PlayerConverter.getPlayer(playerID);
        if (!location.getWorld().equals(player.getWorld())) {
            return false;
        }
        if (player.getLocation().distanceSquared(location) <= distance
                * distance) {
            return true;
        }
        return false;
    }

}
