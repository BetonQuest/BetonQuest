/**
 * 
 */
package pl.betoncraft.betonquest.inout;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.core.Conversation;

/**
 * 
 * @author Co0sh
 */
public class CubePeopleListener implements Listener {

	private static List<String> conversations = new ArrayList<String>();
	
	public CubePeopleListener() {
		Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
	}
	
	public static void removePlayerConversation(String playerID) {
		conversations.remove(playerID);
	}
	
	@EventHandler
	public void onNPCClick(PlayerInteractEvent event) {
		BetonQuest.getInstance().getLogger().info("interaction");
		String conversationID = null;
		if (event.getClickedBlock().getType().equals(Material.SKULL)) {
			BetonQuest.getInstance().getLogger().info("with head");
			Block block = event.getClickedBlock().getLocation().clone().add(0, -1, 0).getBlock();
			if (block.getType().equals(Material.STAINED_CLAY)) {
				BetonQuest.getInstance().getLogger().info("which has clay underneath");
				Block[] signs = new Block[]{block.getRelative(BlockFace.EAST),block.getRelative(BlockFace.WEST),block.getRelative(BlockFace.NORTH),block.getRelative(BlockFace.SOUTH)};
				org.bukkit.block.Sign theSign = null;
				byte count = 0;
				for (Block sign : signs) {
					BetonQuest.getInstance().getLogger().info("one of the block is of " + sign.getType());
					if (sign.getType().equals(Material.WALL_SIGN) && sign.getState() instanceof org.bukkit.block.Sign) {
						BetonQuest.getInstance().getLogger().info("block counted");
						theSign = (org.bukkit.block.Sign) sign.getState();
						count++;
					}
				}
				if (count == 1 && theSign != null) {
					BetonQuest.getInstance().getLogger().info("found one sign");
					conversationID = theSign.getLines()[1];
				}
			}
			
		}
		if (conversationID != null) {
			BetonQuest.getInstance().getLogger().info("starting conversation " + conversationID);
			new Conversation(PlayerConverter.getID(event.getPlayer()), conversationID, new UnifiedLocation(event.getClickedBlock().getLocation()));
			conversations.add(PlayerConverter.getID(event.getPlayer()));
		}
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		if (conversations.contains(PlayerConverter.getID(event.getPlayer()))) {
			removePlayerConversation(PlayerConverter.getID(event.getPlayer()));
		}
	}
}
