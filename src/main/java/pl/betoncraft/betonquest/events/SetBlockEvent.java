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
import org.bukkit.Material;
import org.bukkit.World;

import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.utils.Debug;

public class SetBlockEvent extends QuestEvent {

    private Material block;
    private byte data = 0;
    private Location loc;

    @SuppressWarnings("deprecation")
    public SetBlockEvent(String playerID, String instructions) {
        super(playerID, instructions);
        String[] parts = instructions.split(" ");
        if (parts.length < 3) {
            Debug.error("Not enough arguments in setblock event: " + instructions);
            return;
        }
        block = Material.matchMaterial(parts[1]);
        loc = decodeLocation(parts[2]);
        if (block == null) {
            Debug.error("Could not parse block: " + parts[1]);
            return;
        }
        if (loc == null) {
            Debug.error("Could not parse location: " + parts[2]);
            return;
        }
        for (String part : parts) {
            if (part.contains("data:")) {
                try {
                    data = Byte.parseByte(part.substring(5));
                } catch (NumberFormatException e) {
                    Debug.error("Could not parse data value in: " + instructions);
                    return;
                }
            }
        }
        loc.getBlock().setType(block);
        loc.getBlock().setData(data);
    }

    private Location decodeLocation(String locStr) {
        String[] coords = locStr.split(";");
        if (coords.length != 4) {
            return null;
        }
        double x, y, z;
        World world = Bukkit.getWorld(coords[3]);
        if (world == null) {
            return null;
        }
        try {
            x = Double.parseDouble(coords[0]);
            y = Double.parseDouble(coords[1]);
            z = Double.parseDouble(coords[2]);
        } catch (NumberFormatException e) {
            return null;
        }
        Location loc = new Location(world, x, y, z);
        return loc;
    }

}
