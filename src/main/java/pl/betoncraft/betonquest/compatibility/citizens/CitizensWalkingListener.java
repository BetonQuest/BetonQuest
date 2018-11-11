/**
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
package pl.betoncraft.betonquest.compatibility.citizens;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import net.citizensnpcs.api.ai.Navigator;
import net.citizensnpcs.api.npc.NPC;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.api.PlayerConversationEndEvent;
import pl.betoncraft.betonquest.api.PlayerConversationStartEvent;

/**
 * Prevents Citizens NPCs from walking around when in conversation with the
 * player
 * 
 * @author Jakub Sapalski
 */
public class CitizensWalkingListener implements Listener {

	private static CitizensWalkingListener instance = null;

	private HashMap<NPC, Integer> npcs = new HashMap<>();
	private HashMap<NPC, Location> locs = new HashMap<>();

	/**
	 * Creates new listener which prevents Citizens NPCs from walking around
	 * when in conversation
	 */
	public CitizensWalkingListener() {
		instance = this;
		Bukkit.getServer().getPluginManager().registerEvents(this, BetonQuest.getInstance());
	}

    @EventHandler
    public void onConversationStart(final PlayerConversationStartEvent event) {
        if (event.getConversation() instanceof CitizensConversation) {
            new BukkitRunnable() {
                
                @Override
                public void run() {
                    CitizensConversation conv = (CitizensConversation) event.getConversation();
                    NPC npc = conv.getNPC();
                    if (!npcs.containsKey(npc)) {
                        Navigator nav = npc.getNavigator();
                        npcs.put(npc, 1);
                        locs.put(npc, nav.getTargetAsLocation());
                        nav.setPaused(true);
                        nav.cancelNavigation();
                        nav.setTarget(conv.getNPC().getEntity().getLocation());
                        nav.setPaused(true);
                        nav.cancelNavigation();
                    } else {
                        npcs.put(npc, npcs.get(npc) + 1);
                    }
                }
            }.runTask(BetonQuest.getInstance());
        }
    }

	@EventHandler
	public void onConversationEnd(final PlayerConversationEndEvent event) {
		if (event.getConversation() instanceof CitizensConversation) {
			new BukkitRunnable() {
			    
			    @Override
				public void run() {
					CitizensConversation conv = (CitizensConversation) event.getConversation();
					NPC npc = conv.getNPC();
					Integer i = npcs.get(npc);
					i--;
					if (i == 0) {
						npcs.remove(npc);
						if (npc.isSpawned()) {
							Navigator nav = npc.getNavigator();
							nav.setPaused(false);
							nav.setTarget(locs.remove(npc));
						}
					} else {
						npcs.put(npc, i);
					}
				}
			}.runTask(BetonQuest.getInstance());
		}
	}

	/**
	 * @return the currently active WalkingListener or null if citizens isn't hooked
	 */
	public static CitizensWalkingListener getInstance() {
		return instance;
	}

	/**
	 * @param npc a npc to check for
	 * @return true if the movement of the npc is paused because of a player talking with the npc
	 */
	public boolean isMovementPaused(NPC npc) {
		return npcs.containsKey(npc);
	}

	/**
	 * Sets a new target location to which the npc should move when the conversations end
	 *
	 * Check {@link #isMovementPaused(NPC)} before to make sure the npcs movement is currently paused
	 * @param npc a npc
	 * @param location the location to which the npc should move
	 */
	public void setNewTargetLocation(NPC npc, Location location) {
		locs.put(npc, location);
	}

}
