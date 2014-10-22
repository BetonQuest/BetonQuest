/**
 * 
 */
package pl.betoncraft.betonquest.inout;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import pl.betoncraft.betonquest.BetonQuest;

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
	public void onPlayerJoin(PlayerJoinEvent event) {
		BetonQuest.getInstance().loadObjectives(event.getPlayer().getName());
		BetonQuest.getInstance().loadPlayerStrings(event.getPlayer().getName());
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		BetonQuest.getInstance().savePlayerStrings(event.getPlayer().getName());
		BetonQuest.getInstance().getMySQL().openConnection();
		BetonQuest.getInstance().getMySQL().updateSQL("DELETE FROM objectives WHERE playerID='" + event.getPlayer().getName() + "' AND isused = 1;");
		BetonQuest.getInstance().getMySQL().closeConnection();
	}
}
