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
		BetonQuest.getInstance().loadAllPlayerData(event.getName());
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		BetonQuest.getInstance().loadObjectives(event.getPlayer().getName());
		BetonQuest.getInstance().loadPlayerStrings(event.getPlayer().getName());
		BetonQuest.getInstance().loadJournal(event.getPlayer().getName());
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		final String playerID = event.getPlayer().getName();
		JournalBook.removeJournal(playerID);
		new BukkitRunnable() {
            @Override
            public void run() {
        		BetonQuest.getInstance().savePlayerStrings(playerID);
        		BetonQuest.getInstance().saveJournal(playerID);
        		BetonQuest.getInstance().getMySQL().updateSQL("DELETE FROM objectives WHERE playerID='" + playerID + "' AND isused = 1;");
            }
        }.runTaskAsynchronously(BetonQuest.getInstance());
	}
}
