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
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * 
 * @author Co0sh
 */
public class ActionObjective extends Objective implements Listener {

    private String action;
    private Material type;
    private byte data = -1;
    private String rawLoc;
    private Location loc = null;
    double range = 0;

    public ActionObjective(String playerID, String instructions) {
        super(playerID, instructions);
        String[] parts = instructions.split(" ");
        action = parts[1];
        if (parts[2].equalsIgnoreCase("any")) {
            type = Material.AIR;
        } else {
            if (parts[2].contains(":")) {
                type = Material.matchMaterial(parts[2].split(":")[0]);
                data = Byte.valueOf(parts[2].split(":")[1]);
            } else {
                type = Material.matchMaterial(parts[2]);
            }
        }
        for (String part : parts) {
            if (part.contains("loc:")) {
                rawLoc = part;
                String[] coords = part.substring(4).split(";");
                loc = new Location(Bukkit.getWorld(coords[3]), Double.parseDouble(coords[0]),
                        Double.parseDouble(coords[1]), Double.parseDouble(coords[2]));
                range = Double.parseDouble(coords[4]);
            }
        }
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (!event.getPlayer().equals(PlayerConverter.getPlayer(playerID))) {
            return;
        }
        if (type == Material.AIR) {
            switch (action) {
                case "right":
                    if ((event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction()
                            .equals(Action.RIGHT_CLICK_BLOCK)) && checkConditions()) {
                        completeObjective();
                        HandlerList.unregisterAll(this);
                    }
                    break;
                case "left":
                    if ((event.getAction().equals(Action.LEFT_CLICK_AIR) || event.getAction()
                            .equals(Action.LEFT_CLICK_BLOCK)) && checkConditions()) {
                        completeObjective();
                        HandlerList.unregisterAll(this);
                    }
                    break;
                default:
                    if ((event.getAction().equals(Action.LEFT_CLICK_AIR)
                        || event.getAction().equals(Action.LEFT_CLICK_BLOCK)
                        || event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction()
                            .equals(Action.RIGHT_CLICK_BLOCK)) && checkConditions()) {
                        completeObjective();
                        HandlerList.unregisterAll(this);
                    }
                    break;
            }
        } else {
            Action actionEnum;
            switch (action) {
                case "right":
                    actionEnum = Action.RIGHT_CLICK_BLOCK;
                    break;
                case "left":
                    actionEnum = Action.LEFT_CLICK_BLOCK;
                    break;
                default:
                    actionEnum = null;
                    break;
            }
            if (((actionEnum == null && (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event
                    .getAction().equals(Action.LEFT_CLICK_BLOCK))) || event.getAction().equals(
                    actionEnum))
                && (event.getClickedBlock() != null && event.getClickedBlock().getType()
                        .equals(type))
                && (data < 0 || event.getClickedBlock().getData() == data)
                && (loc == null || event.getClickedBlock().getLocation().distance(loc) <= range)
                && checkConditions()) {
                completeObjective();
                HandlerList.unregisterAll(this);
            }
        }
    }

    @Override
    public String getInstruction() {
        return "action " + action + " " + type + ":" + data + " " + rawLoc + " " + conditions + " "
            + events + " label:" + tag;
    }

    @Override
    public void delete() {
        HandlerList.unregisterAll(this);
    }

}
