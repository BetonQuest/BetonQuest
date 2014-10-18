package pl.betoncraft.betonquest;

import org.bukkit.plugin.java.JavaPlugin;

import pl.betoncraft.betonquest.inout.ConfigInput;

/**
 * Represents BetonQuest plugin
 * @author Co0sh
 */
public final class BetonQuest extends JavaPlugin {

	private static BetonQuest instance;
	private MySQL MySQL;
	
	@Override
	public void onEnable() {
		
		instance = this;

		// try to connect to database
		this.MySQL = new MySQL(this, getConfig().getString("mysql.host"),
				getConfig().getString("mysql.port"), getConfig().getString(
						"mysql.base"), getConfig().getString("mysql.user"),
				getConfig().getString("mysql.pass"));
		
		new ConfigInput();
	}
	
	@Override
	public void onDisable() {
		
	}

	/**
	 * @return the plugin instance
	 */
	public static BetonQuest getInstance() {
		return instance;
	}

	/**
	 * @return the mySQL object
	 */
	public MySQL getMySQL() {
		return MySQL;
	}
}
