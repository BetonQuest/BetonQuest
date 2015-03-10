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

import net.citizensnpcs.api.event.NPCRightClickEvent;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.utils.Debug;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Objective for right-clicking on Citizens NPC
 * 
 * @author Coosh
 */
public class NPCInteractObjective extends Objective implements Listener {
    
    private int id = -1;
    private boolean cancel = false;

    public NPCInteractObjective(String playerID, String instructions) {
        super(playerID, instructions);
        String[] parts = instructions.split(" ");
        if (parts.length < 3) {
            Debug.error("Error in objective string: " + instructions);
            return;
        }
        try {
            id = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            Debug.error("Error in objective string: " + instructions);
            return;
        }
        if (parts[2].equalsIgnoreCase("cancel")) {
            cancel = true;
        }
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
    }
    
    @EventHandler(priority=EventPriority.LOWEST)
    public void onNPCClick(NPCRightClickEvent event) {
        if (id < 0 || !PlayerConverter.getID(event.getClicker()).equals(playerID) || event
                .getNPC().getId() != id) {
            return;
        }
        if (checkConditions()) {
            HandlerList.unregisterAll(this);
            if (cancel) {
                event.setCancelled(true);
            }
            completeObjective();
        }
    }

    @Override
    public String getInstructions() {
        HandlerList.unregisterAll(this);
        return instructions;
    }

}
