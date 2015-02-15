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
package pl.betoncraft.betonquest.objectives;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * 
 * @author Co0sh
 */
public class LocationObjective extends Objective implements Listener {

    private Location location;
    private double distance;

    /**
     * Constructor method
     * 
     * @param playerID
     * @param instructions
     */
    public LocationObjective(String playerID, String instructions) {
        super(playerID, instructions);
        String[] partsOfLoc = instructions.split(" ")[1].split(";");
        location = new Location(Bukkit.getWorld(partsOfLoc[3]), Double.valueOf(partsOfLoc[0]),
                Double.valueOf(partsOfLoc[1]), Double.valueOf(partsOfLoc[2]));
        distance = Double.valueOf(partsOfLoc[4]);
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (event.getPlayer().equals(PlayerConverter.getPlayer(playerID))
            && event.getPlayer().getWorld().equals(location.getWorld())) {
            if (event.getTo().distance(location) < distance && super.checkConditions()) {
                HandlerList.unregisterAll(this);
                super.completeObjective();
            }
        }
    }

    @Override
    public String getInstructions() {
        HandlerList.unregisterAll(this);
        return instructions;
    }

}
