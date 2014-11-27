package pl.betoncraft.betonquest.database;

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
	
	private String[] knownVersionsUUID = new String[]{"", ""};
	private String[] knownVersionsName = new String[]{"", ""};

	public ConfigUpdater() {
		FileConfiguration config = BetonQuest.getInstance().getConfig();
		String version = config.getString("version", null);
		// if the version is null the plugin is updated from pre-1.3 version (which can be 1.0, 1.1 or 1.2)
		if (version == null) {
			BetonQuest.getInstance().getLogger().info("Started converting configuration files from unknown version to v1.3!");
			config.set("version", "1.3");
			String bukkitVersion = Bukkit.getServer().getVersion();
			boolean set = false;
			// if the version supports UUID then we automatically convert names to UUIDs
			for (String knownVersion : knownVersionsUUID) {
				if (bukkitVersion.equals(knownVersion)) {
					BetonQuest.getInstance().getLogger().info("Found version " + version + ", using UUID by default.");
					config.set("uuid", "true");
					convertNamesToUUID();
					set = true;
				}
			}
			// if the version does not support UUID then we set UUID to false and do nothing
			for (String knownVersion : knownVersionsName) {
				if (bukkitVersion.equals(knownVersion)) {
					BetonQuest.getInstance().getLogger().info("Found version " + version + ", using Names by default.");
					config.set("uuid", "false");
					set = true;
				}
			}
			// if we didn't recognize the version we will allow user to decide which one he wants to use.
			if (!set) {
				BetonQuest.getInstance().getLogger().info("Didn't recognize the version, using Names by for safety. If you run UUID compatible server and want to use UUID change it manually in the config file and reload the plugin.");
				config.set("uuid", "false");
				// this will alert the plugin that the conversion should be done if UUID is set to true
				config.set("convert", "true");
				set = true;
			}
			// add metrics if they are not set yet
			if (!config.isSet("metrics")) {
				config.set("metrics", "true");
			}
			// add stop to conversation if not done already
			ConfigAccessor conversations = ConfigInput.getConfigs().get("conversations");
			Set<String> convNodes = conversations.getConfig().getKeys(false);
			for (String convNode : convNodes) {
				if (!conversations.getConfig().isSet(convNode + ".stop")) {
					conversations.getConfig().set(convNode + ".stop", "false");
				}
			}
			// TODO add new languages
//			ConfigAccessor messages = ConfigInput.getConfigs().get("messages");
//			if (!messages.getConfig().isSet("de")) {
//				messages.getConfig().createSection("de", messages.getConfig().getDefaultSection().getConfigurationSection("de").getValues(false));
//			}
//			if (!messages.getConfig().isSet("fr")) {
//				messages.getConfig().createSection("fr", messages.getConfig().getDefaultSection().getConfigurationSection("fr").getValues(false));
//			}
			// end of updating to 1.3
			conversations.saveConfig();
			BetonQuest.getInstance().getLogger().info("Conversion to v1.3 finished. You're ready to go!");
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
