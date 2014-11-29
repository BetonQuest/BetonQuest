package pl.betoncraft.betonquest.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.inout.ConfigInput;

/**
 * Updates configuration files to newest version.
 * @author co0sh
 */
public class ConfigUpdater {
	
	private BetonQuest instance = BetonQuest.getInstance();

	public ConfigUpdater() {
		FileConfiguration config = instance.getConfig();
		String version = config.getString("version", null);
		// if the version is null the plugin is updated from pre-1.3 version (which can be 1.0, 1.1 or 1.2)
		if (version == null) {
			instance.getLogger().info("Started converting configuration files from unknown version to v1.3!");
			// add conversion options
			instance.getLogger().info("Using Names by for safety. If you run UUID compatible server and want to use UUID, change it manually in the config file and reload the plugin.");
			config.set("uuid", "false");
			// this will alert the plugin that the conversion should be done if UUID is set to true
			config.set("convert", "true");
			// add metrics if they are not set yet
			if (!config.isSet("metrics")) {
				instance.getLogger().info("Added metrics option.");
				config.set("metrics", "true");
			}
			// add stop to conversation if not done already
			instance.getLogger().info("Adding stop nodes to conversations...");
			int count = 0;
			ConfigAccessor conversations = ConfigInput.getConfigs().get("conversations");
			Set<String> convNodes = conversations.getConfig().getKeys(false);
			for (String convNode : convNodes) {
				if (!conversations.getConfig().isSet(convNode + ".stop")) {
					conversations.getConfig().set(convNode + ".stop", "false");
					count++;
				}
			}
			instance.getLogger().info("Done, modified " + count + " conversations!");
			// end of updating to 1.3
			config.set("version", "1.3");
			conversations.saveConfig();
			instance.getLogger().info("Conversion to v1.3 finished.");
			// start update recursively for next versions
			new ConfigUpdater();
		} else if (version.equals("1.3")) {
			// do nothing, we're up to date!
		}
		// add new languages
		boolean isUpdated = false;
		ConfigAccessor messages = ConfigInput.getConfigs().get("messages");
		// check every language if it exists
		for (String path : messages.getConfig().getDefaultSection().getKeys(false)) {
			instance.getLogger().info("found "+path);
			if (messages.getConfig().isSet(path)) {
				// if it exists check every message if it exists
				instance.getLogger().info("entered "+path);
				for (String messageNode : messages.getConfig().getDefaults().getConfigurationSection(path).getKeys(false)) {
					instance.getLogger().info("found "+messageNode);
					if (!messages.getConfig().isSet(path + "." + messageNode)) {
						// if message doesn't exist then add it from defaults
						instance.getLogger().info("entered "+messageNode);
						messages.getConfig().set(path + "." + messageNode, messages.getConfig().getDefaults().get(path + "." + messageNode));
						isUpdated = true;
					}
				}
			} else {
				// if language does not exist then add every message to it
				for (String messageNode : messages.getConfig().getDefaults().getConfigurationSection(path).getKeys(false)) {
					instance.getLogger().info("set "+messageNode);
					messages.getConfig().set(path + "." + messageNode, messages.getConfig().getDefaults().get(path + "." + messageNode));
					isUpdated = true;
				}
			}
		}
		// if we updated config filse then print the message
		if (isUpdated) {
			messages.saveConfig();
			instance.getLogger().info("Updated language files!");
		}
		// when the config is up to date then check for pending conversions
		// conversion will occur only if UUID is manually set to true, as we have never set uuid AND convert to true
		if (config.getString("uuid").equals("true") && config.getString("convert") != null && config.getString("convert").equals("true")) {
			convertNamesToUUID();
			config.set("convert", null);
		}
		instance.saveConfig();
	}
	
	@SuppressWarnings("deprecation")
	private void convertNamesToUUID() {
		instance.getLogger().info("Converting names to UUID...");
		instance.getDB().openConnection();
		HashMap<String,String> list = new HashMap<>();
		String[] tables = new String[]{"OBJECTIVES","TAGS","POINTS","JOURNAL"};
		for (String table : tables) {
			ResultSet res = instance.getDB().querySQL(QueryType.valueOf("SELECT_PLAYERS_" + table), new String[]{});
			try {
				while (res.next()) {
					String playerID = res.getString("playerID");
					if (!list.containsKey(playerID)) {
						list.put(playerID, Bukkit.getOfflinePlayer(playerID).getUniqueId().toString());
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		for (String table : tables) {
			for (String playerID : list.keySet()) {
				instance.getDB().updateSQL(UpdateType.valueOf("UPDATE_PLAYERS_" + table), new String[]{list.get(playerID),playerID});
			}
		}
		instance.getLogger().info("Names conversion finished!");
	}
}
