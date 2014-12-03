package pl.betoncraft.betonquest.database;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.core.QuestItem;
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
		if (version != null && version.equals("1.4")) {
			instance.getLogger().info("Configuration up to date!");
			return;
		} else {
			instance.getLogger().info("Backing up before conversion!");
			String outputPath = instance.getDataFolder().getAbsolutePath() + File.separator + "backup-" + version + ".zip";
			new Zipper(instance.getDataFolder().getAbsolutePath(), outputPath);
			instance.getLogger().info("Done, you can find the backup here: " + outputPath);
		}
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
			conversations.saveConfig();
			instance.getLogger().info("Done, modified " + count + " conversations!");
			// end of updating to 1.3
			config.set("version", "1.3");
			instance.getLogger().info("Conversion to v1.3 finished.");
			// start update recursively for next versions
			new ConfigUpdater();
		} else if (version.equals("1.3")) {
			instance.getLogger().info("Started converting configuration files from v1.3 to v1.4!");
			instance.getConfig().set("autoupdate", "false");
			instance.getLogger().info("Added AutoUpdate option to config. It's DISABLED by default!");
			instance.getLogger().info("Moving conversation to separate files...");
			ConfigAccessor convOld = ConfigInput.getConfigs().get("conversations");
			Set<String> keys = convOld.getConfig().getKeys(false);
			File folder = new File(instance.getDataFolder(), "conversations");
			for (File file : folder.listFiles()) {
				file.delete();
			}
			for (String convID : keys) {
				File convFile = new File(folder, convID + ".yml");
		        Map<String,Object> convSection = convOld.getConfig().getConfigurationSection(convID).getValues(true);
		        YamlConfiguration convNew = YamlConfiguration.loadConfiguration(convFile);
		        for (String key : convSection.keySet()) {
					convNew.set(key, convSection.get(key));
				}
		        try {
					convNew.save(convFile);
					instance.getLogger().info("Conversation " + convID + " moved to it's own file!");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			instance.getLogger().info("All conversations moved, deleting old file.");
			new File(instance.getDataFolder(), "conversations.yml").delete();
			
			// updating items
			instance.getLogger().info("Starting conversion of items...");
			// this map will contain all QuestItem objects extracted from
			// configs
			HashMap<String, QuestItem> items = new HashMap<>();
			// this is counter for a number in item names (in items.yml)
			int number = 0;
			// check every event
			for (String key : ConfigInput.getConfigs().get("events").getConfig().getKeys(false)) {
				String instructions = ConfigInput.getString("events." + key);
				String[] parts = instructions.split(" ");
				String type = parts[0];
				// if this event has items in it do the thing
				if (type.equals("give") || type.equals("take")) {
					// define all required variables
					String amount = "";
					String conditions = "";
					String material = null;
					int data = 0;
					Map<String, Integer> enchants = new HashMap<>();
					List<String> lore = new ArrayList<>();
					String name = null;
					// for each part of the instruction string check if it
					// contains some data and if so pu it in variables
					for (String part : parts) {
						if (part.contains("type:")) {
							material = part.substring(5);
						} else if (part.contains("data:")) {
							data = Byte.valueOf(part.substring(5));
						} else if (part.contains("enchants:")) {
							for (String enchant : part.substring(9).split(",")) {
								enchants.put(enchant.split(":")[0], Integer.decode(enchant.split(":")[1]));
							}
						} else if (part.contains("lore:")) {
							for (String loreLine : part.substring(5).split(";")) {
								lore.add(loreLine.replaceAll("_", " "));
							}
						} else if (part.contains("name:")) {
							name = part.substring(5).replaceAll("_", " ");
						} else if (part.contains("amount:")) {
							amount = part;
						} else if (part.contains("conditions:")) {
							conditions = part;
						}
					}
					// generate new name for an item
					String newItemID = "item" + number;
					number++;
					// create an item
					items.put(newItemID, new QuestItem(material, data, enchants, name, lore));
					// replace event with updated version
					ConfigInput.getConfigs().get("events").getConfig()
							.set(key, (type + " " + newItemID + " " + amount + " " + conditions).trim());
					instance.getLogger().info("Extracted " + newItemID + " from " + key + " event!");
				}
			}
			// check every condition (it's almost the same code, I didn't know how to do it better
			for (String key : ConfigInput.getConfigs().get("conditions").getConfig().getKeys(false)) {
				String instructions = ConfigInput.getString("conditions." + key);
				String[] parts = instructions.split(" ");
				String type = parts[0];
				// if this condition has items do the thing
				if (type.equals("hand") || type.equals("item")) {
					// define all variables
					String amount = "";
					String material = null;
					int data = 0;
					Map<String, Integer> enchants = new HashMap<>();
					List<String> lore = new ArrayList<>();
					String name = null;
					String inverted = "";
					// for every part check if it has some data and place it in
					// variables
					for (String part : parts) {
						if (part.contains("type:")) {
							material = part.substring(5);
						} else if (part.contains("data:")) {
							data = Byte.valueOf(part.substring(5));
						} else if (part.contains("enchants:")) {
							for (String enchant : part.substring(9).split(",")) {
								enchants.put(enchant.split(":")[0], Integer.decode(enchant.split(":")[1]));
							}
						} else if (part.contains("lore:")) {
							for (String loreLine : part.substring(5).split(";")) {
								lore.add(loreLine.replaceAll("_", " "));
							}
						} else if (part.contains("name:")) {
							name = part.substring(5).replaceAll("_", " ");
						} else if (part.contains("amount:")) {
							amount = part;
						} else if (part.equalsIgnoreCase("--inverted")) {
							inverted = part;
						}
					}
					// generate new name
					String newItemID = "item" + number;
					number++;
					// create an item
					items.put(newItemID, new QuestItem(material, data, enchants, name, lore));
					// replace condition with updated version
					ConfigInput.getConfigs().get("conditions").getConfig()
							.set(key, (type + " item:" + newItemID + " " + amount + " " + inverted).trim());
					instance.getLogger().info("Extracted " + newItemID + " from " + key + " condition!");
				}
			}
			// generated all items, now place them in items.yml
			for (String key : items.keySet()) {
				QuestItem item = items.get(key);
				String instruction = item.getMaterial().toUpperCase() + " data:" + item.getData();
				if (item.getName() != null) {
					instruction = instruction + " name:" + item.getName().replace(" ", "_");
				}
				if (!item.getLore().isEmpty()) {
					StringBuilder lore = new StringBuilder();
					for (String line : item.getLore()) {
						lore.append(line + ";");
					}
					instruction = instruction + " lore:" + (lore.substring(0, lore.length()-1).replace(" ", "_"));
				}
				if (!item.getEnchants().isEmpty()) {
					StringBuilder enchants = new StringBuilder();
					for (String enchant : item.getEnchants().keySet()) {
						enchants.append(enchant.toUpperCase() + ":" + item.getEnchants().get(enchant) + ",");
					}
					instruction = instruction + " enchants:" + enchants.substring(0, enchants.length()-1);
				}
				ConfigInput.getConfigs().get("items").getConfig().set(key, instruction);
			}
			ConfigInput.getConfigs().get("items").saveConfig();
			ConfigInput.getConfigs().get("events").saveConfig();
			ConfigInput.getConfigs().get("conditions").saveConfig();
			instance.getLogger().info("All extracted items has been successfully saved to items.yml!");

			// end of updating to 1.4
			config.set("version", "1.4");
			instance.getLogger().info("Conversion to v1.4 finished.");
		} else if (version.equals("1.4")) {
			// do nothing, we're up to date!
		}
		// add new languages
		boolean isUpdated = false;
		ConfigAccessor messages = ConfigInput.getConfigs().get("messages");
		// check every language if it exists
		for (String path : messages.getConfig().getDefaultSection().getKeys(false)) {
			if (messages.getConfig().isSet(path)) {
				// if it exists check every message if it exists
				for (String messageNode : messages.getConfig().getDefaults().getConfigurationSection(path).getKeys(false)) {
					if (!messages.getConfig().isSet(path + "." + messageNode)) {
						// if message doesn't exist then add it from defaults
						messages.getConfig().set(path + "." + messageNode, messages.getConfig().getDefaults().get(path + "." + messageNode));
						isUpdated = true;
					}
				}
			} else {
				// if language does not exist then add every message to it
				for (String messageNode : messages.getConfig().getDefaults().getConfigurationSection(path).getKeys(false)) {
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
		// when the config is up to date then check for pending names conversion
		// conversion will occur only if UUID is manually set to true, as we have never set uuid AND convert to true
		if (config.getString("uuid").equals("true") && config.getString("convert") != null && config.getString("convert").equals("true")) {
			convertNamesToUUID();
			config.set("convert", null);
		}
		instance.saveConfig();
	}
	
	/**
	 * As the name says, converts all names to UUID in database
	 */
	@SuppressWarnings("deprecation")
	private void convertNamesToUUID() {
		instance.getLogger().info("Converting names to UUID...");
		instance.getDB().openConnection();
		// loop all tables
		HashMap<String,String> list = new HashMap<>();
		String[] tables = new String[]{"OBJECTIVES","TAGS","POINTS","JOURNAL"};
		for (String table : tables) {
			ResultSet res = instance.getDB().querySQL(QueryType.valueOf("SELECT_PLAYERS_" + table), new String[]{});
			try {
				while (res.next()) {
					// and extract from them list of player names
					String playerID = res.getString("playerID");
					if (!list.containsKey(playerID)) {
						list.put(playerID, Bukkit.getOfflinePlayer(playerID).getUniqueId().toString());
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		// convert all player names in all tables
		for (String table : tables) {
			for (String playerID : list.keySet()) {
				instance.getDB().updateSQL(UpdateType.valueOf("UPDATE_PLAYERS_" + table), new String[]{list.get(playerID),playerID});
			}
		}
		instance.getDB().closeConnection();
		instance.getLogger().info("Names conversion finished!");
	}
}
