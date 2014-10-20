/**
 * 
 */
package pl.betoncraft.betonquest.inout;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import pl.betoncraft.betonquest.BetonQuest;

/**
 * 
 * @author Co0sh
 */
public class JoinListener implements Listener {

	public JoinListener() {
		Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		try {
			ResultSet res = BetonQuest.getInstance().getMySQL().openConnection().createStatement().executeQuery("SELECT instructions FROM objectives WHERE playerID = '" + event.getPlayer().getName() + "'");
			while (res.next()) {
				BetonQuest.objective(event.getPlayer().getName(), res.getString("instructions"));
			}
			BetonQuest.getInstance().getMySQL().updateSQL("DELETE FROM objectives WHERE playerID = '" + event.getPlayer().getName() + "'");
			BetonQuest.getInstance().getMySQL().closeConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
