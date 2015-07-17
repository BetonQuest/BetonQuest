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

import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.api.QuestEvent;

/**
 * Sets the block at specified location
 * 
 * @author Jakub Sapalski
 */
public class SetBlockEvent extends QuestEvent {

    private final Material block;
    private final byte     data;
    private final Location loc;

    public SetBlockEvent(String packName, String instructions)
            throws InstructionParseException {
        super(packName, instructions);
        staticness = true;
        String[] parts = instructions.split(" ");
        if (parts.length < 3) {
            throw new InstructionParseException("Not enough arguments");
        }
        // match material
        block = Material.matchMaterial(parts[1]);
        if (block == null) {
            throw new InstructionParseException("Block type " + parts[1]
                    + " does not exist");
        }
        // parse location
        String[] coords = parts[2].split(";");
        if (coords.length != 4) {
            throw new InstructionParseException("Wrong locatio format");
        }
        World world = Bukkit.getWorld(coords[3]);
        if (world == null) {
            throw new InstructionParseException("World "
                    + coords[3] + " does not exist");
        }
        double x, y, z;
        try {
            x = Double.parseDouble(coords[0]);
            y = Double.parseDouble(coords[1]);
            z = Double.parseDouble(coords[2]);
        } catch (NumberFormatException e) {
            throw new InstructionParseException("Could not parse coordinates");
        }
        loc = new Location(world, x, y, z);
        // get data value
        byte tempData = 0;
        for (String part : parts) {
            if (part.contains("data:")) {
                try {
                    tempData = Byte.parseByte(part.substring(5));
                } catch (NumberFormatException e) {
                    throw new InstructionParseException(
                            "Could not parse data value");
                }
            }
        }
        data = tempData;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void run(String playerID) {
        loc.getBlock().setType(block);
        loc.getBlock().setData(data);
    }

}
