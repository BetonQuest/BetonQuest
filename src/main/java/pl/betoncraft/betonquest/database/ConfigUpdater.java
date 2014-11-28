package pl.betoncraft.betonquest.database;

import java.util.Set;

import org.bukkit.configuration.file.FileConfiguration;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.inout.ConfigInput;

/**
 * Updates configuration files to newest version.
 * @author co0sh
 */
public class ConfigUpdater {

	public ConfigUpdater() {
		FileConfiguration config = BetonQuest.getInstance().getConfig();
		String version = config.getString("version", null);
		// if the version is null the plugin is updated from pre-1.3 version (which can be 1.0, 1.1 or 1.2)
		if (version == null) {
			BetonQuest.getInstance().getLogger().info("Started converting configuration files from unknown version to v1.3!");
			// add conversion options
			BetonQuest.getInstance().getLogger().info("Using Names by for safety. If you run UUID compatible server and want to use UUID, change it manually in the config file and reload the plugin.");
			config.set("uuid", "false");
			// this will alert the plugin that the conversion should be done if UUID is set to true
			config.set("convert", "true");
			// add metrics if they are not set yet
			if (!config.isSet("metrics")) {
				BetonQuest.getInstance().getLogger().info("Added metrics option.");
				config.set("metrics", "true");
			}
			// add stop to conversation if not done already
			BetonQuest.getInstance().getLogger().info("Adding stop nodes to conversations...");
			int count = 0;
			ConfigAccessor conversations = ConfigInput.getConfigs().get("conversations");
			Set<String> convNodes = conversations.getConfig().getKeys(false);
			for (String convNode : convNodes) {
				if (!conversations.getConfig().isSet(convNode + ".stop")) {
					conversations.getConfig().set(convNode + ".stop", "false");
					count++;
				}
			}
			BetonQuest.getInstance().getLogger().info("Done, modified " + count + " conversations!");
			// add new languages
			ConfigAccessor messages = ConfigInput.getConfigs().get("messages");
			for (String path : messages.getConfig().getDefaultSection().getKeys(false)) {
				BetonQuest.getInstance().getLogger().info("found "+path);
				if (messages.getConfig().isSet(path)) {
					BetonQuest.getInstance().getLogger().info("entered "+path);
					for (String messageNode : messages.getConfig().getDefaults().getConfigurationSection(path).getKeys(false)) {
						BetonQuest.getInstance().getLogger().info("found "+messageNode);
						if (!messages.getConfig().isSet(path + "." + messageNode)) {
							BetonQuest.getInstance().getLogger().info("entered "+messageNode);
							messages.getConfig().set(path + "." + messageNode, messages.getConfig().getDefaults().get(path + "." + messageNode));
						}
					}
				} else {
					for (String messageNode : messages.getConfig().getDefaults().getConfigurationSection(path).getKeys(false)) {
						BetonQuest.getInstance().getLogger().info("set "+messageNode);
						messages.getConfig().set(path + "." + messageNode, messages.getConfig().getDefaults().get(path + "." + messageNode));
					}
				}
			}
			messages.saveConfig();
			BetonQuest.getInstance().getLogger().info("Updated language files!");
			// end of updating to 1.3
			config.set("version", "1.3");
			conversations.saveConfig();
			BetonQuest.getInstance().getLogger().info("Conversion to v1.3 finished.");
			// start update recursively for next versions
			new ConfigUpdater();
		} else if (version.equals("1.3")) {
			// do nothing, we're up to date!
		}
		// when the config is up to date then check for pending conversions
		// conversion will occur only if UUID is manually set to true, as we have never set uuid AND convert to true
		if (config.getString("uuid").equals("true") && config.getString("convert") != null && config.getString("convert").equals("true")) {
			convertNamesToUUID();
			config.set("convert", null);
		}
		BetonQuest.getInstance().saveConfig();
	}
	
	private void convertNamesToUUID() {
		BetonQuest.getInstance().getLogger().info("Converting names to UUID...");
		// TODO convert names to UUID
		BetonQuest.getInstance().getLogger().info("Names conversion finished!");
	}
}
