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

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.core.InstructionParseException;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Teleports the player to specified location
 * 
 * @author Jakub Sapalski
 */
public class TeleportEvent extends QuestEvent {

    private final Location loc;
    
    public TeleportEvent(String packName, String instructions)
            throws InstructionParseException {
        super(packName, instructions);
        String[] parts = instructions.split(" ");
        if (parts.length < 2) {
            throw new InstructionParseException("Location not specified");
        }
        String[] location = parts[1].split(";");
        if (location.length < 4) {
            throw new InstructionParseException("Wrong location format");
        }
        World world = Bukkit.getWorld(location[3]);
        if (world == null) {
            throw new InstructionParseException("World does not exists");
        }
        double x, y, z;
        float yaw   = 0,
              pitch = 0;
        try {
            x = Double.parseDouble(location[0]);
            y = Double.parseDouble(location[1]);
            z = Double.parseDouble(location[2]);
            if (location.length == 6) {
        	yaw = Float.parseFloat(location[4]);
        	pitch = Float.parseFloat(location[5]);
            }
        } catch (NumberFormatException e) {
            throw new InstructionParseException("Could not parse coordinates");
        }
        loc = new Location(world, x, y, z, yaw, pitch);
    }

    public void run(String playerID) {
	PlayerConverter.getPlayer(playerID).teleport(loc);
    }
}
