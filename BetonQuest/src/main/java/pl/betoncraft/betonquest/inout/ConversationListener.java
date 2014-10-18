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
	
	public ConversationListener(String playerID, NPCLocation location, Conversation conversation) {
		// set fields for later use
		player = Bukkit.getServer().getPlayer(playerID);
		this.location = new Location(Bukkit.getWorld(location.getWorld()), location.getX(), location.getY(), location.getZ());
		this.conversation = conversation;
		// register this listener
		BetonQuest.getInstance().getServer().getPluginManager().registerEvents(this, BetonQuest.getInstance());
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
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
		if (event.getPlayer() != player) {
			return;
		}
		// end conversation if player moved away from npc more than value defined in config
		if (event.getTo().distance(location) > Integer.valueOf(ConfigInput.getString("config.max_npc_distance"))) {
			conversation.endConversation();
		}
		return;
	}
	
	public void unregisterListener() {
		HandlerList.unregisterAll(this);
	}

}
