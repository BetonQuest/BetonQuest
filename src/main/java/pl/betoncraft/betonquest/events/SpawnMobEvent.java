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
import org.bukkit.entity.EntityType;

import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.utils.Debug;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * 
 * @author Co0sh
 */
public class SpawnMobEvent extends QuestEvent {

    private Location loc;
    private EntityType type;
    private int amount;

    /**
     * Constructor method
     * 
     * @param playerID
     * @param instructions
     */
    public SpawnMobEvent(String playerID, String instructions) {
        super(playerID, instructions);
        // the event cannot be fired for offline players
        if (PlayerConverter.getPlayer(playerID) == null) {
            Debug.info("Player " + playerID + " is offline, cannot fire event");
            return;
        }
        loc = decodeLocation(instructions.split(" ")[1]);
        type = EntityType.valueOf(instructions.split(" ")[2]);
        amount = Integer.parseInt(instructions.split(" ")[3]);
        for (int i = 0; i < amount; i++) {
            loc.getWorld().spawnEntity(loc, type);
        }
    }

    /**
     * @author Dzejkop
     * @param locStr
     * @return
     */
    private Location decodeLocation(String locStr) {

        String[] coords = locStr.split(";");

        Location loc = new Location(Bukkit.getWorld(coords[3]), Double.parseDouble(coords[0]),
                Double.parseDouble(coords[1]), Double.parseDouble(coords[2]));

        return loc;
    }
}
