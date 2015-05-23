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
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Block place/break objective
 * 
 * @author Co0sh
 */
@SuppressWarnings("deprecation")
public class BlockObjective extends Objective implements Listener {

    private Material material;
    private byte data = -1;
    private int neededAmount;
    private int currentAmount = 0;
    private boolean notify = false;

    /**
     * Constructor method
     * 
     * @param playerID
     * @param instructions
     */
    public BlockObjective(String playerID, String instructions) {
        super(playerID, instructions);
        String[] parts = instructions.split(" ");
        String blockType = parts[1];
        if (blockType.contains(":")) {
            material = Material.matchMaterial(blockType.split(":")[0]);
            data = Byte.valueOf(blockType.split(":")[1]);
        } else {
            material = Material.matchMaterial(parts[1]);
        }
        neededAmount = Integer.valueOf(parts[2]);
        for (String part : parts) {
            if (part.contains("conditions:")) {
                conditions = part;
            }
            if (part.contains("events:")) {
                events = part;
            }
            if (part.equalsIgnoreCase("notify")) {
                notify = true;
            }
        }
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getPlayer().equals(PlayerConverter.getPlayer(playerID)) && !event.isCancelled()
            && event.getBlock().getType().equals(material)
            && (data < 0 || event.getBlock().getData() == data) && checkConditions()) {
            currentAmount++;
            if (currentAmount == neededAmount) {
                HandlerList.unregisterAll(this);
                completeObjective();
            } else if (notify) {
                Player player = PlayerConverter.getPlayer(playerID);
                if (currentAmount > neededAmount) {
                    player.sendMessage(Config.getMessage("blocks_to_break")
                            .replaceAll("%amount%", String.valueOf(currentAmount - neededAmount))
                            .replaceAll("&", "§"));
                } else {
                    player.sendMessage(Config.getMessage("blocks_to_place")
                            .replaceAll("%amount%", String.valueOf(neededAmount - currentAmount))
                            .replaceAll("&", "§"));
                }
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getPlayer().equals(PlayerConverter.getPlayer(playerID)) && !event.isCancelled()
            && event.getBlock().getType().equals(material)
            && (data < 0 || event.getBlock().getData() == data) && checkConditions()) {
            currentAmount--;
            if (currentAmount == neededAmount) {
                HandlerList.unregisterAll(this);
                completeObjective();
            } else if (notify) {
                Player player = PlayerConverter.getPlayer(playerID);
                if (currentAmount > neededAmount) {
                    player.sendMessage(Config.getMessage("blocks_to_break")
                            .replaceAll("%amount%", String.valueOf(currentAmount - neededAmount))
                            .replaceAll("&", "§"));
                } else {
                    player.sendMessage(Config.getMessage("blocks_to_place")
                            .replaceAll("%amount%", String.valueOf(neededAmount - currentAmount))
                            .replaceAll("&", "§"));
                }
            }
        }
    }

    @Override
    public String getInstructions() {
        String instruction = new String("block " + material.toString() + ":" + data + " "
            + String.valueOf(neededAmount - currentAmount) + " " + conditions + " " + events
            + " label:" + tag);
        HandlerList.unregisterAll(this);
        return instruction;
    }

}
