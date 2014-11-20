/**
 * 
 */
package pl.betoncraft.betonquest.inout;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.database.UpdateType;

/**
 * 
 * @author Co0sh
 */
public class JoinQuitListener implements Listener {

	/**
	 * Constructor method, this listener loads all objectives for joining player
	 */
	public JoinQuitListener() {
		Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
	}
	
	@EventHandler
	public void playerPreLogin(AsyncPlayerPreLoginEvent event) {
		if (BetonQuest.getInstance().isMySQLUsed()) {
			BetonQuest.getInstance().getDB().openConnection();
			BetonQuest.getInstance().loadAllPlayerData(event.getName());
		}
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		if (!BetonQuest.getInstance().isMySQLUsed()) {
			BetonQuest.getInstance().getDB().openConnection();
			BetonQuest.getInstance().loadAllPlayerData(event.getPlayer().getName());
			BetonQuest.getInstance().getDB().closeConnection();
		}
		BetonQuest.getInstance().loadObjectives(event.getPlayer().getName());
		BetonQuest.getInstance().loadPlayerTags(event.getPlayer().getName());
		BetonQuest.getInstance().loadJournal(event.getPlayer().getName());
		BetonQuest.getInstance().loadPlayerPoints(event.getPlayer().getName());
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		final String playerID = event.getPlayer().getName();
		JournalBook.removeJournal(playerID);
		if (BetonQuest.getInstance().isMySQLUsed()) {
			new BukkitRunnable() {
	            @Override
	            public void run() {
	            	BetonQuest.getInstance().getDB().openConnection();
	        		BetonQuest.getInstance().savePlayerTags(playerID);
	        		BetonQuest.getInstance().saveJournal(playerID);
	        		BetonQuest.getInstance().savePlayerPoints(playerID);
	        		BetonQuest.getInstance().getDB().updateSQL(UpdateType.DELETE_USED_OBJECTIVES, new String[]{playerID});
	            }
	        }.runTaskAsynchronously(BetonQuest.getInstance());
		} else {
			BetonQuest.getInstance().getDB().openConnection();
			BetonQuest.getInstance().savePlayerTags(playerID);
    		BetonQuest.getInstance().saveJournal(playerID);
    		BetonQuest.getInstance().savePlayerPoints(playerID);
    		BetonQuest.getInstance().getDB().updateSQL(UpdateType.DELETE_USED_OBJECTIVES, new String[]{playerID});
    		BetonQuest.getInstance().getDB().closeConnection();
		}
	}
}
