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
package pl.betoncraft.betonquest.conversation;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Base of all chat conversation outputs
 * 
 * @author Jakub Sapalski
 */
public abstract class ChatConvIO implements ConversationIO, Listener {

	protected int i; // counts options
	protected HashMap<Integer, String> options;
	protected String npcText;
	protected String npcName;

	protected final Conversation conv;
	protected final String name;
	protected final Player player;
	protected final HashMap<String, ChatColor[]> colors;

	protected String answerFormat;
	protected String textFormat;

	public ChatConvIO(Conversation conv, String playerID) {
		this.options = new HashMap<>();
		this.conv = conv;
		this.player = PlayerConverter.getPlayer(playerID);
		this.name = player.getName();
		this.colors = ConversationColors.getColors();
		StringBuilder string = new StringBuilder();
		for (ChatColor color : colors.get("npc")) {
			string.append(color);
		}
		string.append("%npc%" + ChatColor.RESET + ": ");
		for (ChatColor color : colors.get("text")) {
			string.append(color);
		}
		textFormat = string.toString();
		string = new StringBuilder();
		for (ChatColor color : colors.get("player")) {
			string.append(color);
		}
		string.append(name + ChatColor.RESET + ": ");
		for (ChatColor color : colors.get("answer")) {
			string.append(color);
		}
		answerFormat = string.toString();
		Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
	}

	@EventHandler
	public void onWalkAway(PlayerMoveEvent event) {
		// return if it's someone else
		if (!event.getPlayer().equals(player)) {
			return;
		}
		// if player passes max distance
		if (!event.getTo().getWorld().equals(conv.getLocation().getWorld()) || event.getTo()
				.distance(conv.getLocation()) > Integer.valueOf(Config.getString("config.max_npc_distance"))) {
			// we can stop the player or end conversation
			if (conv.isMovementBlock()) {
				moveBack(event);
			} else {
				conv.endConversation();
			}
		}
		return;
	}

	/**
	 * Moves the player back a few blocks in the conversation's center
	 * direction.
	 * 
	 * @param event
	 *            PlayerMoveEvent event, for extracting the necessary data
	 */
	private void moveBack(PlayerMoveEvent event) {
		// if the player is in other world (he teleported himself), teleport him
		// back to the center of the conversation
		if (!event.getTo().getWorld().equals(conv.getLocation().getWorld()) || event.getTo()
				.distance(conv.getLocation()) > Integer.valueOf(Config.getString("config.max_npc_distance")) * 2) {
			event.getPlayer().teleport(conv.getLocation());
			return;
		}
		// if not, then calculate the vector
		float yaw = event.getTo().getYaw();
		float pitch = event.getTo().getPitch();
		Vector vector = new Vector(conv.getLocation().getX() - event.getTo().getX(),
				conv.getLocation().getY() - event.getTo().getY(), conv.getLocation().getZ() - event.getTo().getZ());
		vector = vector.multiply(1 / vector.length());
		// and teleport him back using this vector
		Location newLocation = event.getTo().clone();
		newLocation.add(vector);
		newLocation.setPitch(pitch);
		newLocation.setYaw(yaw);
		event.getPlayer().teleport(newLocation);
		if (Config.getString("config.notify_pullback").equalsIgnoreCase("true")) {
			Config.sendMessage(PlayerConverter.getID(event.getPlayer()), "pullback");
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onReply(AsyncPlayerChatEvent event) {
		if (event.isCancelled())
			return;
		if (!event.getPlayer().equals(player))
			return;
		String message = event.getMessage().trim();
		for (int i : options.keySet()) {
			if (message.equals(Integer.toString(i))) {
				player.sendMessage(answerFormat + options.get(i));
				conv.passPlayerAnswer(i);
				event.setCancelled(true);
				return;
			}
		}
		// redisplay the conversation after player's message so he can see it
		new BukkitRunnable() {
			@Override
			public void run() {
				display();
			}
		}.runTask(BetonQuest.getInstance());
	}

	@Override
	public void setNpcResponse(String npcName, String response) {
		this.npcName = npcName;
		this.npcText = response.replace('&', '§');
	}

	@Override
	public void addPlayerOption(String option) {
		i++;
		options.put(i, option.replace('&', '§'));
	}

	@Override
	public void display() {
		if (npcText == null && options.isEmpty()) {
			end();
			return;
		}
		player.sendMessage(textFormat.replace("%npc%", npcName) + npcText);
	}

	@Override
	public void clear() {
		i = 0;
		options.clear();
		npcText = null;
	}

	@Override
	public void end() {
		HandlerList.unregisterAll(this);
	}
}
