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
package pl.betoncraft.betonquest.inout;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.core.Conversation;

/**
 * 
 * @author Co0sh
 */
public class CubeNPCListener implements Listener {
	
	public CubeNPCListener() {
		Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
	}
	
	@EventHandler
	public void onSignPlace(SignChangeEvent event) {
		if (event.getLine(0).equalsIgnoreCase("[NPC]") && !event.getPlayer().hasPermission("betonquest.admin")) {
			event.setCancelled(true);
			event.getPlayer().sendMessage(ConfigInput.getString("messages." + ConfigInput.getString("config.language") + ".no_permission").replaceAll("&", "§"));
		}
	}
	
	@EventHandler
	public void onNPCClick(PlayerInteractEvent event) {
		if (!event.getPlayer().hasPermission("betonquest.conversation")) {
			return;
		}
		String conversationID = null;
		if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && event.getClickedBlock().getType().equals(Material.SKULL)) {
			Block block = event.getClickedBlock().getLocation().clone().add(0, -1, 0).getBlock();
			if (block.getType().equals(Material.STAINED_CLAY)) {
				Block[] signs = new Block[]{block.getRelative(BlockFace.EAST),block.getRelative(BlockFace.WEST),block.getRelative(BlockFace.NORTH),block.getRelative(BlockFace.SOUTH)};
				Sign theSign = null;
				byte count = 0;
				for (Block sign : signs) {
					if (sign.getType().equals(Material.WALL_SIGN) && sign.getState() instanceof Sign) {
						theSign = (Sign) sign.getState();
						count++;
					}
				}
				if (count == 1 && theSign != null && theSign.getLine(0).equalsIgnoreCase("[NPC]")) {
					conversationID = theSign.getLine(1);
				}
			}
			
		}
		if (conversationID != null && !ConversationContainer.containsPlayer(PlayerConverter.getID(event.getPlayer()))) {
			new Conversation(PlayerConverter.getID(event.getPlayer()), conversationID, new UnifiedLocation(event.getClickedBlock().getLocation().add(0.5, -1, 0.5)));
			ConversationContainer.addPlayer(PlayerConverter.getID(event.getPlayer()));
		}
	}
}
