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

import java.util.HashMap;

import net.citizensnpcs.api.npc.NPC;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.core.PlayerConversationEndEvent;
import pl.betoncraft.betonquest.core.PlayerConversationStartEvent;
import pl.betoncraft.betonquest.utils.Debug;

/**
 * Prevents Citizens NPCs from walking around when in conversation with the player
 * 
 * @author Coosh
 */
public class CitizensWalkingListener implements Listener {
    
    private HashMap<NPC, Integer> npcs = new HashMap<>();

    /**
     * Creates new listener which prevents Citizens NPCs from walking around when in conversation
     */
    public CitizensWalkingListener() {
        Bukkit.getServer().getPluginManager().registerEvents(this, BetonQuest.getInstance());
    }
    
    @EventHandler
    public void onConversationStart(final PlayerConversationStartEvent event) {
        if (event.getConversation() instanceof CitizensConversation) {
            new BukkitRunnable() {
                public void run() {
                    CitizensConversation conv = (CitizensConversation) event.getConversation();
                    NPC npc = conv.getNPC();
                    if (!npcs.containsKey(npc)) {
                        npcs.put(npc, new Integer(1));
                        npc.getNavigator().setPaused(true);
                        Debug.info("Stopping the NPC");
                    } else {
                        npcs.put(npc, npcs.get(npc) + 1);
                        Debug.info("NPC is already stopped");
                    }
                }
            }.runTask(BetonQuest.getInstance());
        }
    }
    
    @EventHandler
    public void onConversationEnd(final PlayerConversationEndEvent event) {
        if (event.getConversation() instanceof CitizensConversation) {
            new BukkitRunnable() {
                public void run() {
                    CitizensConversation conv = (CitizensConversation) event.getConversation();
                    NPC npc = conv.getNPC();
                    Integer i = npcs.get(npc);
                    i--;
                    if (i == 0) {
                        npcs.remove(npc);
                        npc.getNavigator().setPaused(false);
                        Debug.info("Resuming NPC");
                    } else {
                        npcs.put(npc, i);
                        Debug.info("NPC should wait");
                    }
                }
            }.runTask(BetonQuest.getInstance());
        }
    }
}
