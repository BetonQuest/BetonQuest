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
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.utils.Debug;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Target shooting objective
 * 
 * @author Coosh
 */
public class ArrowShootObjective extends Objective implements Listener {
    
    /**
     * Location the player needs to hit with the arrow
     */
    private Location location = null;
    /**
     * The max distance from location, where the arrow can land
     */
    private double precision = 0;

    public ArrowShootObjective(String playerID, String instructions) {
        super(playerID, instructions);
        // extract data from instruction
        String[] parts = instructions.split(" ");
        if (parts.length < 2) {
            Debug.error("Incorrect amount of arguments in " + tag + " objective!");
            return;
        }
        String[] partsOfLoc = parts[1].split(";");
        if (partsOfLoc.length < 5) {
            Debug.error("Incorrect amount of arguments in location definition in " + tag
                    + " objective!");
        }
        if (Bukkit.getWorld(partsOfLoc[3]) == null) {
            Debug.error("There is no such world in " + tag + " objective!");
            return;
        }
        try {
            location = new Location(Bukkit.getWorld(partsOfLoc[3]), Double.valueOf(partsOfLoc[0]),
                    Double.valueOf(partsOfLoc[1]), Double.valueOf(partsOfLoc[2]));
            precision = Double.valueOf(partsOfLoc[4]);
        } catch (NumberFormatException e) {
            Debug.error("Numbers are incorrect in location definition in " + tag + " objective!");
        }
        // if the objective was instanciated incorrectly, ignore event
        if (location != null && precision != 0) {
            Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
        }
    }
    
    @EventHandler
    public void onArrowHit(ProjectileHitEvent event) {
        // check if it's the arrow shot by the player
        final Projectile arrow = event.getEntity();
        if (arrow.getType() != EntityType.ARROW) {
            return;
        }
        if (!(arrow.getShooter() instanceof Player)) {
            return;
        }
        final Player player = (Player) arrow.getShooter();
        if (!PlayerConverter.getID(player).equals(playerID)) {
            return;
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                // check if the arrow is in the right place in the next tick, let the arrow land
                Location arrowLocation = arrow.getLocation();
                if (arrowLocation.getWorld().equals(location.getWorld())
                    && arrowLocation.distance(location) < precision && checkConditions()) {
                    completeObjective();
                }
            }
        }.runTask(BetonQuest.getInstance());
    }

    @Override
    public String getInstruction() {
        // the instructions don't change over time in this objective
        return instructions;
    }

    @Override
    public void delete() {
        HandlerList.unregisterAll(this);
    }

}
