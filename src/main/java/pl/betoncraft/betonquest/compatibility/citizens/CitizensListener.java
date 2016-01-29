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
package pl.betoncraft.betonquest.compatibility.citizens;

import net.citizensnpcs.api.event.NPCRightClickEvent;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.conversation.CombatTagger;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Starts new conversations with NPCs
 * 
 * @author Jakub Sapalski
 */
public class CitizensListener implements Listener {

    /**
     * Initializes the listener
     */
    public CitizensListener() {
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onNPCClick(final NPCRightClickEvent event) {
        if (event.isCancelled()) {
            return;
        }
        final String playerID = PlayerConverter.getID(event.getClicker());
        if (CombatTagger.isTagged(playerID)) {
            Config.sendMessage(playerID, "busy");
            return;
        }
        String id = String.valueOf(event.getNPC().getId());
        String assignment = Config.getNpc(id);
        if (assignment != null) {
            String[] parts = assignment.split("\\.");
            final String convName = parts[1];
            final String packName = parts[0];
            event.setCancelled(true);
            new BukkitRunnable() {
                @Override
                public void run() {
                    new CitizensConversation(playerID, packName,
                            convName, event.getNPC().getEntity().getLocation(), event.getNPC());
                }
            }.runTaskAsynchronously(BetonQuest.getInstance());
        }
    }
}
