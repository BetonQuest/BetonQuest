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
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * The player must step on the pressure plate
 * 
 * @author Jakub Sapalski
 */
public class StepObjective extends Objective implements Listener {
    
    private final Block block;

    public StepObjective(String packName, String label, String instructions)
            throws InstructionParseException {
        super(packName, label, instructions);
        template = ObjectiveData.class;
        String[] parts = instructions.split(" ");
        if (parts.length < 2) {
            throw new InstructionParseException("Not enough arguments");
        }
        String[] partsOfLoc = parts[1].split(";");
        if (partsOfLoc.length < 4) {
            throw new InstructionParseException("Wrong location format");
        }
        World world = Bukkit.getWorld(partsOfLoc[3]);
        if (world == null) {
            throw new InstructionParseException("World does not exist: "
                    + partsOfLoc[3]);
        }
        double x, y, z;
        try {
            x = Double.valueOf(partsOfLoc[0]);
            y = Double.valueOf(partsOfLoc[1]);
            z = Double.valueOf(partsOfLoc[2]);
        } catch (NumberFormatException e) {
            throw new InstructionParseException("Could not parse coordinates");
        }
        block = new Location(world, x, y, z).getBlock();
    }
    
    @EventHandler
    public void onStep(PlayerInteractEvent e) {
        if (e.getAction() != Action.PHYSICAL) {
            return;
        }
        if (e.getClickedBlock() == null) {
            return;
        }
        if (!e.getClickedBlock().equals(block)) {
            return;
        }
        Material type = e.getClickedBlock().getType();
        if (type != Material.STONE_PLATE &&
            type != Material.WOOD_PLATE  &&
            type != Material.GOLD_PLATE  &&
            type != Material.IRON_PLATE) {
            return;
        }
        String playerID = PlayerConverter.getID(e.getPlayer());
        if (!containsPlayer(playerID)) {
            return;
        }
        // player stepped on the pressure plate
        if (checkConditions(playerID)) completeObjective(playerID); 
    }

    @Override
    public void start() {
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
    }

    @Override
    public void stop() {
        HandlerList.unregisterAll(this);
    }

    @Override
    public String getDefaultDataInstruction() {
        return "";
    }
    
    @Override
    public String getProperty(String name, String playerID) {
        if (name.equalsIgnoreCase("location")) {
            return "X: " + block.getX() + ", Y: " + block.getY() +
                    ", Z: " + block.getZ();
        }
        return "";
    }
}
