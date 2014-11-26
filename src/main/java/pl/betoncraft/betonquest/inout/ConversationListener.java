/**
 * 
 */
package pl.betoncraft.betonquest.inout;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.util.Vector;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.core.Conversation;

/**
 * This listener listens for player's replies and ends conversation when player is away from npc
 * @author Co0sh
 */
public class ConversationListener implements Listener {
	
	private Player player;
	private Location location;
	private Conversation conversation;
	
	public ConversationListener(String playerID, UnifiedLocation location, Conversation conversation) {
		// set fields for later use
		player = PlayerConverter.getPlayer(playerID);
		this.location = new Location(Bukkit.getWorld(location.getWorld()), location.getX(), location.getY(), location.getZ());
		this.conversation = conversation;
		// register this listener
		BetonQuest.getInstance().getServer().getPluginManager().registerEvents(this, BetonQuest.getInstance());
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onReply(AsyncPlayerChatEvent event) {
		// return if it's someone else
		if (event.getPlayer() != player) {
			return;
		}
		event.setCancelled(true);
		conversation.passPlayerAnswer(event.getMessage());
	}
	
	@EventHandler
	public void onWalkAway(PlayerMoveEvent event) {
		// return if it's someone else
		if (!event.getPlayer().equals(player)) {
			return;
		}
		// if player passes max distance
		if (event.getTo().distance(location) > Integer.valueOf(ConfigInput.getString("config.max_npc_distance"))) {
			// we can stop the player or end conversation
			if (conversation.isMovementBlock()) {
				float yaw = event.getTo().getYaw();
				float pitch = event.getTo().getPitch();
				Vector vector = new Vector(location.getX() - event.getTo().getX(), location.getY() - event.getTo().getY(), location.getZ() - event.getTo().getZ());
				vector = vector.multiply(1 / vector.length());
				Location newLocation = event.getTo().clone();
				newLocation.add(vector);
				newLocation.setPitch(pitch);
				newLocation.setYaw(yaw);
				event.getPlayer().teleport(newLocation);
			} else {
				conversation.endConversation();
			}
		}
		return;
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		// if player quits, end conversation (why keep listeners running?)
		if (event.getPlayer().equals(player)) {
			conversation.endConversation();
		}
	}
	
	public void unregisterListener() {
		HandlerList.unregisterAll(this);
	}

}
