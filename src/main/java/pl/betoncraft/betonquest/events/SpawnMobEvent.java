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
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.utils.Debug;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Spawns mobs at given location
 * 
 * @author Co0sh
 */
public class SpawnMobEvent extends QuestEvent {

    private Location loc;
    private EntityType type;
    private int amount;
    private String name;

    public SpawnMobEvent(String playerID, String instructions) {
        super(playerID, instructions);
        // the event cannot be fired for offline players
        if (playerID != null && PlayerConverter.getPlayer(playerID) == null) {
            Debug.info("Player " + playerID + " is offline, cannot fire event");
            return;
        }
        String[] parts = instructions.split(" ");
        String[] coords = parts[1].split(";");
        loc = new Location(Bukkit.getWorld(coords[3]), Double.parseDouble(coords[0]),
                Double.parseDouble(coords[1]), Double.parseDouble(coords[2]));
        type = EntityType.valueOf(parts[2]);
        amount = Integer.parseInt(parts[3]);
        if (parts.length == 5) {
            name = parts[4].replaceAll("_", " ");
        }
        for (int i = 0; i < amount; i++) {
            Entity entity = loc.getWorld().spawnEntity(loc, type);
            if (entity instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity) entity;
                livingEntity.setCustomName(name);
            }
        }
    }
}
