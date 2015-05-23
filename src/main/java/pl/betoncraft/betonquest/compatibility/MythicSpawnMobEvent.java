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
package pl.betoncraft.betonquest.compatibility;

import net.elseland.xikage.MythicMobs.Mobs.MobSpawner;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import pl.betoncraft.betonquest.api.QuestEvent;

/**
 * @author co0sh
 *
 */
public class MythicSpawnMobEvent extends QuestEvent {

    private Location loc;
    private String mob;
    private int amount;
    private int level;

    /**
     * @param playerID
     * @param instructions
     */
    public MythicSpawnMobEvent(String playerID, String packName, String instructions) {
        super(playerID, packName, instructions);
        String[] parts = instructions.split(" ");
        loc = decodeLocation(parts[1]);
        mob = parts[2].split(":")[0];
        level = Integer.parseInt(parts[2].split(":")[1]);
        amount = Integer.parseInt(parts[3]);
        for (int i = 0; i < amount; i++) {
            MobSpawner.SpawnMythicMob(mob, loc, level);
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
