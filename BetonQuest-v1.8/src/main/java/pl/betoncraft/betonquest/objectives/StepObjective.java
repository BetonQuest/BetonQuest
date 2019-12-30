/*
 * BetonQuest - advanced quests for Bukkit
 * Copyright (C) 2016  Jakub "Co0sh" Sapalski
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
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.utils.LocationData;
import pl.betoncraft.betonquest.utils.LogUtils;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.util.logging.Level;

/**
 * The player must step on the pressure plate
 *
 * @author Jakub Sapalski
 */
public class StepObjective extends Objective implements Listener {

    private final LocationData loc;

    public StepObjective(Instruction instruction) throws InstructionParseException {
        super(instruction);
        template = ObjectiveData.class;
        loc = instruction.getLocation();
    }

    @EventHandler(ignoreCancelled = true)
    public void onStep(PlayerInteractEvent event) {
        if (event.getAction() != Action.PHYSICAL) {
            return;
        }
        if (event.getClickedBlock() == null) {
            return;
        }
        try {
            String playerID = PlayerConverter.getID(event.getPlayer());
            Material type = event.getClickedBlock().getType();
            Block block = loc.getLocation(playerID).getBlock();
            if (!event.getClickedBlock().equals(block)) {
                return;
            }
            if (type != Material.STONE_PLATE && type != Material.WOOD_PLATE && type != Material.GOLD_PLATE
                    && type != Material.IRON_PLATE) {
                return;
            }
            if (!containsPlayer(playerID)) {
                return;
            }
            // player stepped on the pressure plate
            if (checkConditions(playerID))
                completeObjective(playerID);
        } catch (QuestRuntimeException e) {
            LogUtils.getLogger().log(Level.WARNING, "Error while handling '" + instruction.getID() + "' objective: " + e.getMessage());
            LogUtils.logThrowable(e);
        }
    }

    @Override
    public void start() {
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance().getJavaPlugin());
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
            Block block;
            try {
                block = loc.getLocation(playerID).getBlock();
            } catch (QuestRuntimeException e) {
                LogUtils.getLogger().log(Level.WARNING, "Error while getting location property in '" + instruction.getID() + "' objective: "
                        + e.getMessage());
                LogUtils.logThrowable(e);
                return "";
            }
            return "X: " + block.getX() + ", Y: " + block.getY() + ", Z: " + block.getZ();
        }
        return "";
    }
}
