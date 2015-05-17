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
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.utils.Debug;

/**
 * Checks block at specified location against specified Material
 * 
 * @author Coosh
 */
public class TestForBlockCondition extends Condition {

    private Block block;
    private Material material;
    
    public TestForBlockCondition(String playerID, String instructions) {
        super(playerID, instructions);
        String[] parts = instructions.split(" ");
        if (parts.length < 3) {
            Debug.error("Error in instruction string in: " + instructions);
            isOk = false;
            return;
        }
        String[] location = parts[1].split(";");
        if (location.length != 4) {
            Debug.error("Error in location in: " + instructions);
            isOk = false;
            return;
        }
        double y = 0, x = 0, z = 0;
        try {
            x = Double.parseDouble(location[0]);
            y = Double.parseDouble(location[1]);
            z = Double.parseDouble(location[2]);
        } catch (NumberFormatException e) {
            Debug.error("Wrong number format in: " + instructions);
            isOk = false;
            return;
        }
        World world = Bukkit.getWorld(location[3]);
        if (world == null) {
            Debug.error("World does not exist in: " + instructions);
            isOk = false;
            return;
        }
        block = new Location(world, x, y, z).getBlock();
        if (block == null) {
            Debug.error("Error with block in: " + instructions);
            isOk = false;
            return;
        }
        material = Material.matchMaterial(parts[2]);
        if (material == null) {
            Debug.error("Undefined material type in: " + instructions);
            isOk = false;
            return;
        }
    }

    @Override
    public boolean isMet() {
        if (!isOk) {
            Debug.error("There was an error, returning false.");
            return false;
        }
        if (material == null || block == null) {
            return false;
        }
        return block.getType().equals(material);
    }

}
