/**
 * BetonQuest - advanced quests for Bukkit
 * Copyright (C) 2016  Jakub "Co0sh" Sapalski
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package pl.betoncraft.betonquest.conversation;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Listener which starts conversation on clicking on blocks with specific locations
 *
 * @author Jonas Blocher
 */
public class LocationNPCListener implements Listener {

	/**
	 * Creates new instance of the default NPC listener
	 */
	public LocationNPCListener() {
		Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
	}

	/**
	 * This checks if the player clicked on a block which is a valid NPC and starts the conversation
	 *
	 * @param event PlayerInteractEntityEvent
	 */
	@EventHandler
	public void onNPCClick(final PlayerInteractEvent event) {
		if (event.isCancelled())
			return;

		if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
			return;

		try {
			// Only fire the event for the main hand to avoid that the event is triggered two times.
			if (event.getHand() == EquipmentSlot.OFF_HAND && event.getHand() != null) {
				return; // off hand packet, ignore.
			}
		} catch (LinkageError e) {
			// it's fine, 1.8 doesn't trigger this event twice
		}

		// check if the player has required permission
		if (!event.getPlayer().hasPermission("betonquest.conversation")) {
			return;
		}

		//check if a conversation is assigned
		Location l = event.getClickedBlock().getLocation();
		String conversationID = l.getBlockX() + ";" + l.getBlockY() + ";" + l.getBlockZ() + ";" + l.getWorld().getName();
		String assignment = Config.getNpc(conversationID);
		if (assignment == null)
			return;

		//check if player is in combat
		if (CombatTagger.isTagged(PlayerConverter.getID(event.getPlayer()))) {
			Config.sendMessage(PlayerConverter.getID(event.getPlayer()), "busy");
			return;
		}

		event.setCancelled(true);
		new Conversation(PlayerConverter.getID(event.getPlayer()), assignment,
				event.getClickedBlock().getLocation().add(0.5, 0, 0.5));
	}
}
