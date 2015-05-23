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

import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.utils.Debug;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Created by Dzejkop
 */
public class LightningEvent extends QuestEvent {

    /**
     * Spawns a lightning in a given location
     *
     * Takes in instructions in format: location(x;y;z;world)
     *
     * @param playerID
     * @param instructions
     */
    public LightningEvent(String playerID, String packName, String instructions) {
        super(playerID, packName, instructions);
        // the event cannot be fired for offline players
        if (playerID != null && PlayerConverter.getPlayer(playerID) == null) {
            Debug.info("Player " + playerID + " is offline, cannot fire event");
            return;
        }

        String[] instr = instructions.split(" ");

        Location loc = decodeLocation(instr[1]);

        loc.getWorld().strikeLightning(loc);
    }

    private Location decodeLocation(String locStr) {

        String[] coords = locStr.split(";");

        Location loc = new Location(Bukkit.getWorld(coords[3]), Double.parseDouble(coords[0]),
                Double.parseDouble(coords[1]), Double.parseDouble(coords[2]));

        return loc;
    }

}
