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
import org.bukkit.event.Listener;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.core.Conversation;
import pl.betoncraft.betonquest.inout.ConfigInput;
import pl.betoncraft.betonquest.inout.ConversationContainer;
import pl.betoncraft.betonquest.inout.PlayerConverter;
import pl.betoncraft.betonquest.inout.UnifiedLocation;

/**
 * 
 * @author Co0sh
 */
public class CitizensListener implements Listener {
	
	public CitizensListener() {
		Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
	}
	
	@EventHandler
	public void onNPCClick(NPCRightClickEvent event) {
		if (ConfigInput.getString("npcs." + String.valueOf(event.getNPC().getId())) != null && !ConversationContainer.containsPlayer(PlayerConverter.getID(event.getClicker())) && event.getClicker().hasPermission("betonquest.conversation")) {
			new Conversation(PlayerConverter.getID(event.getClicker()), ConfigInput.getString("npcs." + String.valueOf(event.getNPC().getId())), new UnifiedLocation(event.getNPC().getEntity().getLocation()));
			ConversationContainer.addPlayer(PlayerConverter.getID(event.getClicker()));
		}
	}
}
