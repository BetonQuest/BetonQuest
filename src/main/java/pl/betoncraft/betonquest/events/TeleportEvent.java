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
import org.bukkit.entity.Player;

import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * @author Dzejkop
 */
public class TeleportEvent extends QuestEvent {

    /**
     * Constructor method
     * 
     * @param playerID
     * @param instructions
     */
    public TeleportEvent(String playerID, String instructions) {
        super(playerID, instructions);
        Player player = PlayerConverter.getPlayer(playerID);

        // Ignoring the first part of instruction
        String locationString = instructions.substring(instructions.indexOf(" ") + 1);

        // Get the location
        Location loc = decodeLocation(locationString);

        player.teleport(loc);
    }

    /**
     * Parses a location from string
     * 
     * @param str
     * @return
     */
    private Location decodeLocation(String str) {

        String[] locArgs = str.split(";");

        Location loc = null;

        if (locArgs.length == 4) {
            // Location without head alignment
            loc = new Location(Bukkit.getWorld(locArgs[3]), // World
                    Double.parseDouble(locArgs[0]), // X
                    Double.parseDouble(locArgs[1]), // Y
                    Double.parseDouble(locArgs[2]) // Z
            );
        } else {
            // Location with head alignment
            loc = new Location(Bukkit.getWorld(locArgs[3]), // World
                    Double.parseDouble(locArgs[0]), // X
                    Double.parseDouble(locArgs[1]), // Y
                    Double.parseDouble(locArgs[2]), // Z
                    Float.parseFloat(locArgs[4]), // Yaw
                    Float.parseFloat(locArgs[5]) // Pitch
            );
        }

        return loc;

    }

}
