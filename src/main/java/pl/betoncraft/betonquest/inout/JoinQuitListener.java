/**
 * 
 */
package pl.betoncraft.betonquest.inout;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.database.UpdateType;
import pl.betoncraft.betonquest.inout.PlayerConverter.PlayerConversionType;

/**
 * 
 * @author Co0sh
 */
public class JoinQuitListener implements Listener {
	
	private BetonQuest instance = BetonQuest.getInstance();

	/**
	 * Constructor method, this listener loads all objectives for joining player
	 */
	public JoinQuitListener() {
		Bukkit.getPluginManager().registerEvents(this, instance);
	}
	
	@EventHandler
	public void playerPreLogin(AsyncPlayerPreLoginEvent event) {
		if (instance.isMySQLUsed()) {
			instance.getDB().openConnection();
			if (PlayerConverter.getType() == PlayerConversionType.UUID) {
				instance.loadAllPlayerData(event.getUniqueId().toString());
			} else if (PlayerConverter.getType() == PlayerConversionType.NAME) {
				instance.loadAllPlayerData(event.getName());
			}
		}
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		String playerID = PlayerConverter.getID(event.getPlayer());
		if (!instance.isMySQLUsed()) {
			instance.getDB().openConnection();
			instance.loadAllPlayerData(playerID);
			instance.getDB().closeConnection();
		}
		instance.loadObjectives(playerID);
		instance.loadPlayerTags(playerID);
		instance.loadJournal(playerID);
		instance.loadPlayerPoints(playerID);
		
		// display changelog message to the admins
		if (event.getPlayer().hasPermission("betonquest.admin") && new File(BetonQuest.getInstance().getDataFolder(), "changelog.txt").exists()) {
			SimpleTextOutput.sendSystemMessage(PlayerConverter.getID(event.getPlayer()), ConfigInput.getString("messages." + ConfigInput.getString("config.language") + ".changelog"));
		}
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		final String playerID = PlayerConverter.getID(event.getPlayer());
		JournalBook.removeJournal(playerID);
		if (instance.isMySQLUsed()) {
			new BukkitRunnable() {
	            @Override
	            public void run() {
	            	instance.getDB().openConnection();
	        		instance.savePlayerTags(playerID);
	        		instance.saveJournal(playerID);
	        		instance.savePlayerPoints(playerID);
	        		instance.getDB().updateSQL(UpdateType.DELETE_USED_OBJECTIVES, new String[]{playerID});
	            }
	        }.runTaskAsynchronously(instance);
		} else {
			instance.getDB().openConnection();
			instance.savePlayerTags(playerID);
    		instance.saveJournal(playerID);
    		instance.savePlayerPoints(playerID);
    		instance.getDB().updateSQL(UpdateType.DELETE_USED_OBJECTIVES, new String[]{playerID});
    		instance.getDB().closeConnection();
		}
	}
}
