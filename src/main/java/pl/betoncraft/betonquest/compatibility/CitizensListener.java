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
import org.bukkit.event.Listener;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.config.ConfigHandler;
import pl.betoncraft.betonquest.core.CombatTagger;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Starts new conversations with NPCs
 * 
 * @author Co0sh
 */
public class CitizensListener implements Listener {

    /**
     * Initializes the listener
     */
    public CitizensListener() {
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onNPCClick(NPCRightClickEvent event) {
        String id = ConfigHandler.getString("npcs." + event.getNPC().getId());
        if (event.isCancelled() || id == null) {
            return;
        }
        if (CombatTagger.isTagged(PlayerConverter.getID(event.getClicker()))) {
            event.getClicker().sendMessage(ConfigHandler.getString("messages." + ConfigHandler
                    .getString("config.language") + ".busy").replaceAll("&", "§"));
            return;
        }
        new CitizensConversation(PlayerConverter.getID(event.getClicker()), id,
                event.getNPC().getEntity().getLocation(), event.getNPC());
    }
}
