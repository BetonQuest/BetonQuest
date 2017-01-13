/**
 * BetonQuest - advanced quests for Bukkit
 * Copyright (C) 2016  Jakub "Co0sh" Sapalski
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package pl.betoncraft.betonquest.config;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.config.ConfigAccessor.AccessorType;
import pl.betoncraft.betonquest.database.Connector;
import pl.betoncraft.betonquest.database.Connector.QueryType;
import pl.betoncraft.betonquest.database.Connector.UpdateType;
import pl.betoncraft.betonquest.database.Database;
import pl.betoncraft.betonquest.database.Saver.Record;
import pl.betoncraft.betonquest.item.QuestItem;
import pl.betoncraft.betonquest.utils.Debug;
import pl.betoncraft.betonquest.utils.Utils;

/**
 * Updates configuration files to the newest version.
 * 
 * @author Jakub Sapalski
 */
public class ConfigUpdater {

	// abandon all hope, ye who enter here

	/**
	 * Error which should be displayed to the player when something goes wrong
	 */
	private final String ERROR = "There was an error during updating process! Please "
			+ "downgrade to the previous working version of the plugin and restore your "
			+ "configuration from the backup. Don't forget to send this error to the developer"
			+ ", so he can fix it! Sorry for inconvenience, here's the link:"
			+ " <https://github.com/Co0sh/BetonQuest/issues> and a cookie: <http://i.imgur.com/iR4UMH5.png>";

	/**
	 * BetonQuest's instance
	 */
	private BetonQuest instance = BetonQuest.getInstance();
	/**
	 * Main configuration instance
	 */
	private FileConfiguration config = instance.getConfig();
	/**
	 * Destination version. At the end of the updating process this will be the
	 * current version
	 */
	private final String destination = "v51";
	/**
	 * Deprecated ConfigHandler, used for updating older configuration files
	 */
	private ConfigHandler ch;

	public ConfigUpdater() {
		String version = BetonQuest.getInstance().getConfig().getString("version", null);
		Debug.info("Initializing updater with version " + version + ", destination is " + destination);
		// when the config is up to date then check for pending names
		// conversion;
		// conversion will occur only if UUID is manually set to true
		if (config.getString("uuid") != null && config.getString("uuid").equals("true")
				&& config.getString("convert") != null && config.getString("convert").equals("true")) {
			convertNamesToUUID();
			config.set("convert", null);
			instance.saveConfig();
		}
		// move backup files to backup folder
		for (File file : instance.getDataFolder().listFiles()) {
			if (file.getName().matches("^backup-.*\\.zip$")) {
				file.renameTo(new File(file.getParentFile().getAbsolutePath() + File.separator + "backups"
						+ File.separator + file.getName()));
				Debug.broadcast("File " + file.getName() + " moved to backup folder!");
			}
		}
		if (version != null && version.equals(destination)) {
			Debug.broadcast("Configuration up to date!");
			return;
		} else {
			Utils.backup();
		}
		// instantiate old configuration handler
		ch = new ConfigHandler();
		// if the version is null the plugin is updated from pre-1.3 version
		// (which can be 1.0, 1.1 or 1.2)
		if (version == null) {
			updateTo1_3();
		} else if (version.equals("1.3")) {
			updateTo1_4();
		} else if (version.equals("1.4")) {
			updateTo1_4_1();
		} else if (version.equals("1.4.1")) {
			updateTo1_4_2();
		} else if (version.equals("1.4.2")) {
			updateTo1_4_3();
		} else if (version.equals("1.4.3")) {
			updateTo1_5();
		} else if (version.equals("1.5")) {
			updateTo1_5_1();
		} else if (version.equals("1.5.1")) {
			updateTo1_5_2();
		} else if (version.equals("1.5.2")) {
			updateTo1_5_3();
		} else if (version.equals("1.5.3") || version.equals("1.5.4") || version.equals("1.6")) {
			updateTo1_6();
		} else if (version.matches("^v\\d+$")) {
			performUpdate();
		} else {
			Debug.broadcast("Something is not right with configuration version. Consider fixing this.");
		}
	}

	/**
	 * Performs full update in new updating system.
	 */
	private void performUpdate() {
		// this is new, post-1.5.3 updating system, where config versions
		// are numbered separately from plugin's releases
		Debug.broadcast("Updating configuration to version " + destination);
		update();
		updateLanguages();
		instance.saveConfig();
		// reload configuration file to apply all possible changes
		new Config(false);
		Debug.broadcast("Successfully updated configuration!");
		addChangelog();
	}

	/**
	 * Invokes method that updates config from current version to the next. It
	 * repeats itself until everything is converted.
	 */
	private void update() {
		String version = config.getString("version", null);
		// if the version is the same as destination, updating process is
		// finished
		if (version == null || version.equals(destination))
			return;
		try {
			// reload existing configuration
			new Config(false);
			config = instance.getConfig();
			// call the right updating method
			Method method = this.getClass().getDeclaredMethod("update_from_" + version);
			method.setAccessible(true);
			Debug.info("Starting update from " + version + "!");
			method.invoke(this);
			Debug.info("Update to " + config.getString("version") + " done!");
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			e.printStackTrace();
			// return, so it does not fall into an infinite loop
			return;
		}
		// update again until destination is reached
		update();
	}

	@SuppressWarnings("unused")
	private void update_from_v50() {
		Debug.info("Moving custom settings from main.yml to custom.yml");
		List<String> coreSettings = Arrays.asList(new String[]{"npcs", "variables", "static", "global_locations",
				"cancel", "journal_main_page", "compass", "enabled"});
		for (ConfigPackage pack : Config.getPackages().values()) {
			Debug.info("  Moving custom settings in package " + pack.getName());
			ConfigAccessor main = pack.getMain();
			ConfigAccessor custom = pack.getCustom();
			main:
			for (String key : main.getConfig().getKeys(false)) {
				for (String coreSetting : coreSettings) {
					if (key.equals(coreSetting)) {
						Debug.info("    Key " + key + " is core setting, skipping");
						continue main;
					}
				}
				Debug.info("    Key " + key + " is custom, moving it");
				custom.getConfig().set(key, main.getConfig().get(key));
				main.getConfig().set(key, null);
			}
			main.saveConfig();
			custom.saveConfig();
		}
		Debug.broadcast("Moved custom settings from main.yml to custom.yml file");
		config.set("version", "v51");
		instance.saveConfig();
	}

	@SuppressWarnings("unused")
	private void update_from_v49() {
		Set<String> enabledPackages = new HashSet<>(config.getStringList("packages"));
		Debug.info("Disabling packages not listed in the config");
		for (Iterator<ConfigPackage> iterator = Config.getPackages().values().iterator(); iterator.hasNext();) {
			ConfigPackage pack = iterator.next();
			Debug.info("  Looking at package " + pack.getName());
			if (!enabledPackages.contains(pack.getName())) {
				Debug.info("    Package is not enabled, removing it from the list.");
				pack.getMain().getConfig().set("enabled", false);
				pack.getMain().saveConfig();
				iterator.remove();
			} else {
				pack.getMain().getConfig().set("enabled", true);
				pack.getMain().saveConfig();
			}
		}
		Debug.info("All packages enabled/disabled, removing 'packages' section from config");
		config.set("packages", null);
		Debug.broadcast("Moved package enabling from config to main files");
		config.set("version", "v50");
		instance.saveConfig();
	}

	@SuppressWarnings("unused")
	private void update_from_v48() {
		for (ConfigPackage pack : Config.getPackages().values()) {
			String packName = pack.getName();
			List<ConfigAccessor> sections = new ArrayList<>();
			// the idea is to get index of location argument for every type
			// and use a method to replace last semicolon with a space, because
			// all range arguments are right next to location arguments
			sections.add(pack.getConditions());
			sections.add(pack.getEvents());
			sections.add(pack.getObjectives());
			for (ConfigAccessor acc : sections) {
				AccessorType type = acc.getType();
				ConfigurationSection sec = acc.getConfig();
				for (String key : sec.getKeys(false)) {
					String value = sec.getString(key);
					int i = value.indexOf(' ');
					if (i < 0) {
						continue;
					}
					String object = value.substring(0, i).toLowerCase();
					int index = -1;
					switch (type) {
					case CONDITIONS:
						switch (object) {
						case "location":
							index = 1;
							break;
						case "monsters":
							index = 2;
							break;
						}
						break;
					case EVENTS:
						switch (object) {
						case "clear":
							index = 2;
							break;
						}
						break;
					case OBJECTIVES:
						switch (object) {
						case "action":
							// action objective uses optional argument, so convert it manually
							String[] parts = value.split(" ");
							String loc = null;
							for (String part : parts) {
								if (part.startsWith("loc:")) {
									loc = part;
									break;
								}
							}
							if (loc != null) {
								int j = loc.lastIndexOf(';');
								if (j < 0 || j >= loc.length() - 1) {
									continue;
								}
								String front = loc.substring(0, j);
								String back = loc.substring(j + 1);
								String newLoc = front + " range:" + back;
								sec.set(key, value.replace(loc, newLoc));
							}
							break;
						case "arrow":
							index = 1;
							break;
						case "location":
							index = 1;
							break;
						}
						break;
					default:
						break;
					}
					if (index >= 0) {
						sec.set(key, semicolonToSpace(value, index));
					}
				}
				acc.saveConfig();
			}
		}
		Debug.broadcast("Converted additional location arguments to the new format");
		config.set("version", "v49");
		instance.saveConfig();
	}
	
	private String semicolonToSpace(String string, int argument) {
		if (string == null) {
			return null;
		}
		String[] parts = string.split(" ");
		if (parts.length <= argument) {
			return null;
		}
		String original = parts[argument];
		int lastSemicolon = original.lastIndexOf(';');
		if (lastSemicolon < 0) {
			return null;
		}
		char[] chars = original.toCharArray();
		chars[lastSemicolon] = ' ';
		String replaced = new String(chars);
		return string.replace(original, replaced);
	}

	@SuppressWarnings("unused")
	private void update_from_v47() {
		config.set("quest_items_unbreakable", "true");
		Debug.broadcast("Added option to disable quest item unbreakability");
		config.set("version", "v48");
		instance.saveConfig();
	}

	@SuppressWarnings("unused")
	private void update_from_v46() {
		config.set("journal.full_main_page", "false");
		Debug.broadcast("Added 'full_main_page' option to config");
		config.set("version", "v47");
		instance.saveConfig();
	}

	@SuppressWarnings("unused")
	private void update_from_v45() {
		config.set("hook.legendquest", "true");
		Debug.broadcast("Added compatibility with LegendQuest");
		config.set("hook.worldedit", "true");
		Debug.broadcast("Added compatibility with WorldEdit");
		config.set("version", "v46");
		instance.saveConfig();
	}

	@SuppressWarnings("unused")
	private void update_from_v44() {
		try {
			Debug.info("Translating items in 'potion' objectives");
			for (ConfigPackage pack : Config.getPackages().values()) {
				String packName = pack.getName();
				Debug.info("  Handling " + packName + " package");
				FileConfiguration objectives = pack.getObjectives().getConfig();
				FileConfiguration items = pack.getItems().getConfig();
				for (String key : objectives.getKeys(false)) {
					String instruction = objectives.getString(key);
					if (!instruction.startsWith("potion ")) {
						continue;
					}
					Debug.info("    Found potion objective: '" + instruction + "'");
					String[] parts = instruction.split(" ");
					if (parts.length < 2) {
						Debug.info("    It's incorrect.");
						continue;
					}
					int data;
					try {
						data = Integer.parseInt(parts[1]);
					} catch (NumberFormatException e) {
						Debug.info("    It's incorrect");
						continue;
					}
					ItemStack itemStack = new QuestItem("potion data:" + data).generate(1);
					{
						// it doesn't work without actually spawning the item in-game...
						World world = Bukkit.getWorlds().get(0);
						Location loc = new Location(world, 0, 254, 0);
						Item item = world.dropItem(loc, itemStack);
						itemStack = item.getItemStack();
						item.remove();
					}
					String updatedInstruction = QuestItem.itemToString(itemStack);
					Debug.info("    Potion instruction: '" + updatedInstruction + "'");
					String item = null;
					for (String itemKey : items.getKeys(false)) {
						if (items.getString(itemKey).equals(updatedInstruction)) {
							item = itemKey;
						}
					}
					if (item == null) {
						if (items.contains("potion")) {
							int index = 2;
							while (items.contains("potion" + index)) {
								index++;
							}
							item = "potion" + index;
						} else {
							item = "potion";
						}
					}
					Debug.info("    The item with this instruction has key " + item);
					items.set(item, updatedInstruction);
					objectives.set(key, instruction.replace(String.valueOf(data), item));
				}
				pack.getItems().saveConfig();
				pack.getObjectives().saveConfig();
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.error(ERROR);
		}
		Debug.broadcast("Translated items in 'potion' objective");
		config.set("display_chat_after_conversation", "false");
		Debug.broadcast("Added an option to display chat messages after the conversation");
		config.set("version", "v45");
		instance.saveConfig();
	}

	@SuppressWarnings("unused")
	private void update_from_v43() {
		try {
			Debug.info("Translating potion instructions");

			for (ConfigPackage pack : Config.getPackages().values()) {
				String packName = pack.getName();
				Debug.info("  Handling " + packName + " package");
				FileConfiguration items = pack.getItems().getConfig();
				for (String key : items.getKeys(false)) {
					String instruction = items.getString(key);
					if (!instruction.toLowerCase().startsWith("potion ") && !instruction.startsWith("splash_potion ")) {
						continue;
					}
					Debug.info("    Found " + key + " potion with instruction '" + instruction + "'");
					try {
						QuestItem questItem = new QuestItem(instruction);
						ItemStack itemStack = questItem.generate(1);
						{
							// it doesn't work without actually spawning the item in-game...
							World world = Bukkit.getWorlds().get(0);
							Location loc = new Location(world, 0, 254, 0);
							Item item = world.dropItem(loc, itemStack);
							itemStack = item.getItemStack();
							item.remove();
							// lol
						}
						String updatedInstruction = QuestItem.itemToString(itemStack);
						Debug.info("    New instruction: '" + updatedInstruction + "'");
						items.set(key, updatedInstruction);
					} catch (InstructionParseException e) {
						Debug.info("Item " + packName + "." + key + " was incorrect, skipping.");
					}
				}
				pack.getItems().saveConfig();
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.error(ERROR);
		}
		Debug.broadcast("Translated potions to a new format");
		config.set("hook.racesandclasses", "true");
		Debug.broadcast("Added compatibility with RacesAndClasses");
		config.set("version", "v44");
		instance.saveConfig();
	}

	@SuppressWarnings("unused")
	private void update_from_v42() {
		config.set("hook.holographicdisplays", "true");
		Debug.broadcast("Added compatibility with HolographicDisplays");
		config.set("version", "v43");
		instance.saveConfig();
	}

	@SuppressWarnings("unused")
	private void update_from_v41() {
		try {
			// change raw material names in craft objectives to items from items.yml
			for (ConfigPackage pack : Config.getPackages().values()) {
				String packName = pack.getName();
				ConfigAccessor objectives = pack.getObjectives();
				ConfigAccessor items = pack.getItems();
				ArrayList<String> materials = new ArrayList<>();
				// get a list of materials and their data values
				for (String key : objectives.getConfig().getKeys(false)) {
					String objective = objectives.getConfig().getString(key);
					if (objective.startsWith("craft ")) {
						String[] parts = objective.split(" ");
						if (parts.length > 1) {
							materials.add(parts[1]);
						}
					}
				}
				// translate materials to item instructions
				ArrayList<String> itemInstructions = new ArrayList<>();
				for (String material : materials) {
					if (material.contains(":")) {
						String[] parts = material.split(":");
						String materialName = parts[0];
						String data = parts[1];
						itemInstructions.add(materialName + " data:" + data);
					} else {
						itemInstructions.add(material);
					}
				}
				// find items with the same instruction and store them in map (material, itemID)
				HashMap<String, String> itemIDs = new HashMap<>();
				for (int i = 0; i < materials.size(); i++) {
					String material = materials.get(i);
					String itemInstruction = itemInstructions.get(i);
					String itemID = null;
					// look for existing items
					for (String key : items.getConfig().getKeys(false)) {
						if (items.getConfig().getString(key).equalsIgnoreCase(itemInstruction)) {
							itemID = key;
							break;
						}
					}
					// if there are no such items, create them
					if (itemID == null) {
						String materialName = material.contains(":") ? material.split(":")[0] : material;
						if (items.getConfig().contains(materialName)) {
							int index = 2;
							while (items.getConfig().contains(materialName + index)) {
								index++;
							}
							items.getConfig().set(materialName + index, itemInstruction);
							itemID = materialName + index;
						} else {
							items.getConfig().set(materialName, itemInstruction);
							itemID = materialName;
						}
					}
					itemIDs.put(material, itemID);
				}
				items.saveConfig();
				// replace materials in craft objectives
				for (String key : objectives.getConfig().getKeys(false)) {
					String objective = objectives.getConfig().getString(key);
					if (objective.startsWith("craft ")) {
						String[] parts = objective.split(" ");
						if (parts.length > 1) {
							objectives.getConfig().set(key, objective.replace(parts[1], itemIDs.get(parts[1])));
						}
					}
				}
				objectives.saveConfig();
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.error(ERROR);
		}
		Debug.broadcast("Changed 'craft' objective to use items.yml");
		config.set("version", "v42");
		instance.saveConfig();
	}

	@SuppressWarnings("unused")
	private void update_from_v40() {
		config.set("hook.placeholderapi", "true");
		Debug.broadcast("Added compatibility with PlaceholderAPI");
		config.set("version", "v41");
		instance.saveConfig();
	}

	@SuppressWarnings("unused")
	private void update_from_v39() {
		config.set("hook.shopkeepers", "true");
		Debug.broadcast("Added compatibility with Shopkeepers");
		config.set("version", "v40");
		instance.saveConfig();
	}

	@SuppressWarnings("unused")
	private void update_from_v38() {
		boolean enabled = config.getString("autoupdate").equalsIgnoreCase("true");
		config.set("autoupdate", null);
		config.set("update.enabled", enabled);
		config.set("update.download_bugfixes", true);
		config.set("update.notify_new_release", true);
		Debug.broadcast("Modified autoupdater");
		config.set("version", "v39");
		instance.saveConfig();
	}

	@SuppressWarnings("unused")
	private void update_from_v37() {
		try {
			Debug.info("Updating global location tags in the database");
			Debug.info("    oiienwfiu wenfiu nweiufn weiunf iuwenf iuw");
			for (ConfigPackage pack : Config.getPackages().values()) {
				String packName = pack.getName();
				String locList = pack.getMain().getConfig().getString("global_locations");
				Debug.info("  Handling package '" + packName + "': " + locList);
				if (locList == null) {
					continue;
				}
				for (String locName : locList.split(",")) {
					Debug.info("Adding '" + packName + "' prefix to '" + locName + "' global location tags.");
					instance.getSaver().add(new Record(UpdateType.RENAME_ALL_TAGS,
							new String[] { packName + ".global_" + locName, "global_" + locName }));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.error(ERROR);
		}
		Debug.broadcast("Updated tags of global locations with package names");
		config.set("version", "v38");
		instance.saveConfig();
	}

	@SuppressWarnings("unused")
	private void update_from_v36() {
		config.set("hook.quests", "true");
		Debug.broadcast("Added compatibility with Quests");
		config.set("version", "v37");
		instance.saveConfig();
	}

	@SuppressWarnings("unused")
	private void update_from_v35() {
		config.set("hook.denizen", "true");
		Debug.broadcast("Added compatibility with Denizen");
		config.set("hook.skillapi", "true");
		Debug.broadcast("Added compatibility with SkillAPI");
		config.set("version", "v36");
		instance.saveConfig();
	}

	@SuppressWarnings("unused")
	private void update_from_v34() {
		config.set("hook.magic", "true");
		Debug.broadcast("Added compatibility with Magic");
		config.set("version", "v35");
		instance.saveConfig();
	}

	@SuppressWarnings("unused")
	private void update_from_v33() {
		config.set("hook.heroes", "true");
		Debug.broadcast("Added compatibility with Heroes");
		config.set("version", "v34");
		instance.saveConfig();
	}

	@SuppressWarnings("unused")
	private void update_from_v32() {
		config.set("hook.playerpoints", "true");
		Debug.broadcast("Added compatibility with PlayerPoints");
		config.set("version", "v33");
		instance.saveConfig();
	}

	@SuppressWarnings("unused")
	private void update_from_v31() {
		config.set("hook.effectlib", "true");
		config.set("effectlib_npc_effect.class", "VortexEffect");
		config.set("effectlib_npc_effect.iterations", 20);
		config.set("effectlib_npc_effect.particle", "crit_magic");
		config.set("effectlib_npc_effect.helixes", 3);
		config.set("effectlib_npc_effect.circles", 1);
		config.set("effectlib_npc_effect.grow", 0.1);
		config.set("effectlib_npc_effect.radius", 0.5);
		config.set("effectlib_npc_effect.delay", 5);
		Debug.broadcast("Added compatibility with EffectLib");
		config.set("version", "v32");
		instance.saveConfig();
	}

	@SuppressWarnings("unused")
	private void update_from_v30() {
		try {
			Debug.info("Converting cancelers to a new format");
			for (ConfigPackage pack : Config.getPackages().values()) {
				String packName = pack.getName();
				Debug.info("Searching " + packName + " package");
				ConfigurationSection s = pack.getMain().getConfig().getConfigurationSection("cancel");
				if (s == null)
					continue;
				for (String key : s.getKeys(false)) {
					String instruction = s.getString(key);
					Debug.info("  Converting " + key + " canceler: " + instruction);
					String[] parts = instruction.split(" ");
					HashMap<String, String> names = new HashMap<>();
					String events = null, conditions = null, tags = null, points = null, objectives = null,
							journal = null, loc = null;
					for (String part : parts) {
						Debug.info("    Checking part " + part);
						if (part.startsWith("name:")) {
							Debug.info("    Found general name: " + part.substring(5));
							names.put(Config.getLanguage(), part.substring(5));
						} else if (part.startsWith("name_")) {
							int colonIndex = part.indexOf(':');
							if (colonIndex < 0)
								continue;
							String lang = part.substring(5, colonIndex);
							Debug.info("    Found " + lang + " name: " + part.substring(colonIndex));
							names.put(lang, part.substring(colonIndex));
						} else if (part.startsWith("events:")) {
							Debug.info("    Found events: " + part.substring(7));
							events = part.substring(7);
						} else if (part.startsWith("conditions:")) {
							Debug.info("    Found conditions: " + part.substring(11));
							conditions = part.substring(11);
						} else if (part.startsWith("tags:")) {
							Debug.info("    Found tags: " + part.substring(5));
							tags = part.substring(5);
						} else if (part.startsWith("points:")) {
							Debug.info("    Found points: " + part.substring(7));
							points = part.substring(7);
						} else if (part.startsWith("objectives:")) {
							Debug.info("    Found objectives: " + part.substring(11));
							objectives = part.substring(11);
						} else if (part.startsWith("journal:")) {
							Debug.info("    Found journal entries: " + part.substring(8));
							journal = part.substring(8);
						} else if (part.startsWith("loc:")) {
							Debug.info("    Found location: " + part.substring(4));
							loc = part.substring(4);
						}
					}
					Debug.info("  - Setting the values");
					s.set(key, null);
					for (String lang : names.keySet()) {
						s.set(key + ".name." + lang, names.get(lang));
					}
					s.set(key + ".events", events);
					s.set(key + ".conditions", conditions);
					s.set(key + ".tags", tags);
					s.set(key + ".points", points);
					s.set(key + ".objectives", objectives);
					s.set(key + ".journal", journal);
					s.set(key + ".loc", loc);
					pack.getMain().saveConfig();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.error(ERROR);
		}
		Debug.broadcast("Made quest cancelers more convenient to define");
		config.set("version", "v31");
		instance.saveConfig();
	}

	@SuppressWarnings("unused")
	private void update_from_v29() {
		try {
			for (ConfigPackage pack : Config.getPackages().values()) {
				String packName = pack.getName();
				ConfigurationSection section = pack.getMain().getConfig().getConfigurationSection("variables");
				for (String key : section.getKeys(true)) {
					String variable = section.getString(key);
					if (variable.matches(
							"^\\$[a-zA-Z0-9]+\\$->\\(\\-?\\d+\\.?\\d*,\\-?\\d+\\.?\\d*,\\-?\\d+\\.?\\d*\\)$")) {
						section.set(key, variable.replace(',', ';'));
					}
				}
				pack.getMain().saveConfig();
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.error(ERROR);
		}
		Debug.broadcast("Changed commas to semicolons in vector variables");
		config.set("version", "v30");
		instance.saveConfig();
	}

	@SuppressWarnings("unused")
	private void update_from_v28() {
		String globalName = "global";
		try {
			HashMap<String, ArrayList<String>> tags = new HashMap<>();
			HashMap<String, ArrayList<String>> points = new HashMap<>();
			// this will ensure that there is no "global" package already
			// defined
			int i = 1;
			while (Config.getPackages().get(globalName) != null) {
				i++;
				globalName = "global-" + i;
			}
			Debug.info("Global package will be called '" + globalName + "'");
			// create lists for tags/points that are duplicated across multiple
			// packages
			// these will be "global", the rest will be converted to their local
			// packages
			ArrayList<String> globalTagList = new ArrayList<>();
			ArrayList<String> globalPointList = new ArrayList<>();
			tags.put(globalName, globalTagList);
			points.put(globalName, globalPointList);
			ArrayList<ConfigPackage> packages = new ArrayList<>();
			for (ConfigPackage pack : Config.getPackages().values()) {
				String packName = pack.getName();
				Debug.info("  Checking '" + packName + "' package");
				// skip packages that already use prefixes
				String prefixOption = pack.getString("main.tag_point_prefix");
				if (prefixOption != null && prefixOption.equalsIgnoreCase("true"))
					continue;
				Debug.info("  - It's outdated, extracting tags and points from events");
				packages.add(pack);
				// create array lists
				ArrayList<String> tagList = new ArrayList<>();
				ArrayList<String> pointList = new ArrayList<>();
				tags.put(packName, tagList);
				points.put(packName, pointList);
				// handle all tags/points in events
				for (String key : pack.getEvents().getConfig().getKeys(false)) {
					Debug.info("    Checking '" + key + "' event");
					String rawInstruction = pack.getEvents().getConfig().getString(key);
					ArrayList<String> instructions = new ArrayList<>();
					// run event also needs to be checked in case it contained
					// any tags
					if (rawInstruction.startsWith("run ")) {
						Debug.info("    - It's \"run\" event, extracting additional instructions");
						// this part is copied from run event
						String[] parts = rawInstruction.substring(3).trim().split(" ");
						StringBuilder builder = new StringBuilder();
						for (String part : parts) {
							if (part.startsWith("^")) {
								if (builder.length() != 0) {
									instructions.add(builder.toString().trim());
									builder = new StringBuilder();
								}
								builder.append(part.substring(1) + " ");
							} else {
								builder.append(part + " ");
							}
						}
						instructions.add(builder.toString().trim());
					} else {
						// if it's not run event, add whole instruction string
						instructions.add(rawInstruction);
					}
					// check every instruction that was specified
					for (String instruction : instructions) {
						if (instruction.startsWith("tag ")) {
							Debug.info("      Found tag event, extracting tag");
							String[] parts = instruction.split(" ");
							// check if it contains the tag, if not - continue
							if (parts.length < 3) {
								Debug.info("      - Could not find tags");
								continue;
							}
							// add tag to the list if it does not contain a
							// package
							for (String tag : parts[2].split(",")) {
								if (!tag.contains("."))
									tagList.add(tag);
							}
						} else if (instruction.startsWith("point ")) {
							Debug.info("      Found point event, extracting points");
							String[] parts = instruction.split(" ");
							// check if the point has defined a category
							if (parts.length < 2) {
								Debug.info("      - Could not find the category");
								continue;
							}
							// add point to the list if it does not contain a
							// package
							if (!parts[1].contains("."))
								pointList.add(parts[1]);
						}
					}
					// done, all tags in events are extracted
				}
				Debug.info("  All tags and points extracted from events, moving to conditions");
				// handle all tags/points in conditions
				for (String key : pack.getConditions().getConfig().getKeys(false)) {
					Debug.info("    Checking '" + key + "' condition");
					String rawInstruction = pack.getConditions().getConfig().getString(key);
					ArrayList<String> instructions = new ArrayList<>();
					// check condition also needs to be checked in case it
					// contained any tags
					if (rawInstruction.startsWith("check ")) {
						Debug.info("    - It's \"check\" condition, extracting additional instructions");
						// this part is copied from run event
						String[] parts = rawInstruction.substring(5).trim().split(" ");
						StringBuilder builder = new StringBuilder();
						for (String part : parts) {
							if (part.startsWith("^")) {
								if (builder.length() != 0) {
									instructions.add(builder.toString().trim());
									builder = new StringBuilder();
								}
								builder.append(part.substring(1) + " ");
							} else {
								builder.append(part + " ");
							}
						}
						instructions.add(builder.toString().trim());
					} else {
						// if it's not check condition, add whole instruction
						// string
						instructions.add(rawInstruction);
					}
					// check every instruction that was specified
					for (String instruction : instructions) {
						if (instruction.startsWith("tag ")) {
							Debug.info("      Found tag condition, extracting tag");
							String[] parts = instruction.split(" ");
							// check if it contains the tag, if not - continue
							if (parts.length < 2) {
								Debug.info("      - Could not find the tag");
								continue;
							}
							// add tag to the list if it does not contain a
							// package
							if (!parts[1].contains("."))
								tagList.add(parts[1]);
						} else if (instruction.startsWith("point ")) {
							Debug.info("      Found point condition, extracting points");
							String[] parts = instruction.split(" ");
							// check if the point has defined a category
							if (parts.length < 2) {
								Debug.info("      - Could not find the category");
								continue;
							}
							// add point to the list if it does not contain a
							// package
							if (!parts[1].contains("."))
								pointList.add(parts[1]);
						}
					}
					// done, all tags in conditions are extracted
				}
				Debug.info("  All tags and points extracted from conditions");
				// done, events and conditions in package extracted
			}
			Debug.info("All tags and points in all packages extracted, checking tags for duplicates");
			// find tags/points that are duplicated in package hashMaps,
			// put them to global package and remove from those packages
			// first tags in each package
			for (int j = 0; j < packages.size(); j++) {
				Debug.info("  Checking list '" + packages.get(j).getName() + "'");
				// get a list
				ArrayList<String> list = tags.get(packages.get(j).getName());
				// and for each element
				for (int k = 0; k < list.size(); k++) {
					String checked = list.get(k);
					Debug.info("    Checking tag '" + checked + "'");
					// go to each next package
					for (int l = j + 1; l < packages.size(); l++) {
						ArrayList<String> nextList = tags.get(packages.get(l).getName());
						// and check if it contains that element
						if (nextList.contains(checked)) {
							Debug.info("    - list '" + packages.get(l).getName() + "' contains this tag, removing");
							nextList.remove(checked);
							if (!globalTagList.contains(checked)) {
								globalTagList.add(checked);
								Debug.info("      Tag was added to the global list");
							}
						}
					}
				}
			}
			Debug.info("List of global tags is filled, checking points");
			// now points in each package
			for (int j = 0; j < packages.size(); j++) {
				Debug.info("  Checking list '" + packages.get(j).getName() + "'");
				// get a list
				ArrayList<String> list = points.get(packages.get(j).getName());
				// and for each element
				for (int k = 0; k < list.size(); k++) {
					String checked = list.get(k);
					Debug.info("    Checking point '" + checked + "'");
					// go to each next package
					for (int l = j + 1; l < packages.size(); l++) {
						ArrayList<String> nextList = points.get(packages.get(l).getName());
						// and check if it contains that element
						if (nextList.contains(checked)) {
							Debug.info("    - list '" + packages.get(l).getName() + "' contains this point, removing");
							nextList.remove(checked);
							if (!globalPointList.contains(checked)) {
								globalPointList.add(checked);
								Debug.info("      Point was added to the global list");
							}
						}
					}
				}
			}
			Debug.info("List of global points is filled, now adding \"global\" prefix in configuration files");
			// done, global lists are filled
			for (ConfigPackage pack : packages) {
				Debug.info("  Replacing in '" + pack.getName() + "' package");
				// update tags/points in events
				for (String key : pack.getEvents().getConfig().getKeys(false)) {
					Debug.info("    Replacing tags/points in '" + key + "' event");
					String instruction = pack.getEvents().getConfig().getString(key);
					if (instruction.startsWith("tag ")) {
						Debug.info("      Found tag event, replacing tags");
						String[] parts = instruction.split(" ");
						// check if it contains the tag, if not - continue
						if (parts.length < 3) {
							Debug.info("      - Could not find tags");
							continue;
						}
						// replace tags
						String[] localTags = parts[2].split(",");
						for (int j = 0; j < localTags.length; j++)
							if (globalTagList.contains(localTags[j])) {
								String replaced = globalName + "." + localTags[j];
								Debug.info("        Replacing '" + localTags[j] + "' with '" + replaced + "'");
								localTags[j] = replaced;
							}
						pack.getEvents().getConfig().set(key,
								instruction.replace(parts[2], StringUtils.join(Arrays.asList(localTags), ',')));
					} else if (instruction.startsWith("point ")) {
						Debug.info("      Found point event, replacing points");
						String[] parts = instruction.split(" ");
						// check if the point has defined a category
						if (parts.length < 2) {
							Debug.info("      - Could not find the category");
							continue;
						}
						// replace points category
						if (globalPointList.contains(parts[1])) {
							String replaced = globalName + "." + parts[1];
							Debug.info("        Replacing '" + parts[1] + "' with '" + replaced + "'");
							pack.getEvents().getConfig().set(key,
									StringUtils.replaceOnce(instruction, parts[1], replaced));
						}
					} else if (instruction.startsWith("run ")) {
						Debug.info("      Found run event, looking for tags and points");
						String[] parts = instruction.split(" ");
						for (int j = 0; j < parts.length; j++) {
							// if the part is beginning of the "tag" instruction
							// and it contains a tag
							if (parts[j].equals("^tag") && j + 2 < parts.length) {
								Debug.info("        There is a tag event, replacing tags");
								String[] localTags = parts[j + 2].split(",");
								for (int k = 0; k < localTags.length; k++)
									if (globalTagList.contains(localTags[k])) {
										String replaced = globalName + "." + localTags[k];
										Debug.info("        Replacing '" + localTags[k] + "' with '" + replaced + "'");
										localTags[k] = replaced;
									}
								parts[j + 2] = StringUtils.join(Arrays.asList(localTags), ',');
							} else if (parts[j].equals("^point") && j + 1 < parts.length) {
								Debug.info("        There is a point event, replacing points");
								if (globalTagList.contains(parts[j + 1])) {
									String replaced = globalName + "." + parts[j + 1];
									Debug.info("        Replacing '" + parts[j + 1] + "' with '" + replaced + "'");
									parts[j + 1] = replaced;
								}
							}
						}
						pack.getEvents().getConfig().set(key, StringUtils.join(Arrays.asList(parts), ' '));
					}
				}
				pack.getEvents().saveConfig();
				Debug.info("  All tags/points replaced in all events");
				// done, everything replaced in events
				// replacing tags/points in conditions
				for (String key : pack.getConditions().getConfig().getKeys(false)) {
					Debug.info("    Replacing tags/points in '" + key + "' condition");
					String instruction = pack.getConditions().getConfig().getString(key);
					if (instruction.startsWith("tag ")) {
						Debug.info("      Found tag condition, replacing the tag");
						String[] parts = instruction.split(" ");
						// check if it contains the tag, if not - continue
						if (parts.length < 2) {
							Debug.info("      - Could not find tags");
							continue;
						}
						// replace tag
						if (globalTagList.contains(parts[1])) {
							String replaced = globalName + "." + parts[1];
							Debug.info("        Replacing '" + parts[1] + "' with '" + replaced + "'");
							pack.getConditions().getConfig().set(key,
									StringUtils.replaceOnce(instruction, parts[1], replaced));
						}

					} else if (instruction.startsWith("point ")) {
						Debug.info("      Found point condition, replacing points");
						String[] parts = instruction.split(" ");
						// check if the point has defined a category
						if (parts.length < 2) {
							Debug.info("      - Could not find the category");
							continue;
						}
						// replace points category
						if (globalPointList.contains(parts[1])) {
							String replaced = globalName + "." + parts[1];
							Debug.info("        Replacing '" + parts[1] + "' with '" + replaced + "'");
							pack.getConditions().getConfig().set(key,
									StringUtils.replaceOnce(instruction, parts[1], replaced));
						}
					} else if (instruction.startsWith("check ")) {
						Debug.info("      Found check condition, looking for tags and points");
						String[] parts = instruction.split(" ");
						for (int j = 0; j < parts.length; j++) {
							// if the part is beginning of the "tag" instruction
							// and it contains a tag
							if (parts[j].equals("^tag") && j + 1 < parts.length) {
								Debug.info("        There is a tag condition, replacing tags");
								if (globalTagList.contains(parts[j + 1])) {
									String replaced = globalName + "." + parts[j + 1];
									Debug.info("        Replacing '" + parts[j + 1] + "' with '" + replaced + "'");
									parts[j + 1] = replaced;
								}
							} else if (parts[j].equals("^point") && j + 1 < parts.length) {
								Debug.info("        There is a point condition, replacing points");
								if (globalTagList.contains(parts[j + 1])) {
									String replaced = globalName + "." + parts[j + 1];
									Debug.info("        Replacing '" + parts[j + 1] + "' with '" + replaced + "'");
									parts[j + 1] = replaced;
								}
							}
						}
						pack.getConditions().getConfig().set(key, StringUtils.join(Arrays.asList(parts), ' '));
					}
				}
				pack.getConditions().saveConfig();
				Debug.info("  All tags/points replaced in all conditions, time for quest cancelers");
				// done, everything replaced in conditions
				// time for quest cancelers
				for (String key : pack.getMain().getConfig().getConfigurationSection("cancel").getKeys(false)) {
					Debug.info("    Replacing tags/points in '" + key + "' canceler");
					String instruction = pack.getMain().getConfig().getString("cancel." + key);
					String[] parts = instruction.split(" ");
					for (int j = 0; j < parts.length; j++) {
						if (parts[j].startsWith("tags:")) {
							String[] localTags = parts[j].substring(5).split(",");
							for (int k = 0; k < localTags.length; k++) {
								if (globalTagList.contains(localTags[k])) {
									String replaced = globalName + "." + localTags[k];
									Debug.info("      Replaced  tag '" + localTags[k] + "' to '" + replaced + "'");
									localTags[k] = replaced;
								}
							}
							parts[j] = "tags:" + StringUtils.join(Arrays.asList(localTags), ',');
						} else if (parts[j].startsWith("points:")) {
							String[] localPoints = parts[j].substring(5).split(",");
							for (int k = 0; k < localPoints.length; k++) {
								if (globalPointList.contains(localPoints[k])) {
									String replaced = globalName + "." + localPoints[k];
									Debug.info("      Replaced  point '" + localPoints[k] + "' to '" + replaced + "'");
									localPoints[k] = replaced;
								}
							}
							parts[j] = "points:" + StringUtils.join(Arrays.asList(localPoints), ',');
						}
					}
					pack.getMain().getConfig().set("cancel." + key, StringUtils.join(Arrays.asList(parts), " "));
				}
				Debug.info("  All tags/points replaced in quest cancelers");
				pack.getMain().saveConfig();
			}
			// done, all packages have replaced tags and points
			Debug.info(
					"Done, all global tags and points are prefixed as global everywhere in every package. Updating the database.");
			for (String packName : tags.keySet()) {
				for (String tag : tags.get(packName)) {
					instance.getSaver()
							.add(new Record(UpdateType.RENAME_ALL_TAGS, new String[] { packName + "." + tag, tag }));
				}
			}
			for (String packName : points.keySet()) {
				for (String point : points.get(packName)) {
					instance.getSaver().add(
							new Record(UpdateType.RENAME_ALL_POINTS, new String[] { packName + "." + point, point }));
				}
			}
			// remove "tag_point_prefix" option from main.yml files
			for (ConfigPackage pack : Config.getPackages().values()) {
				String packName = pack.getName();
				ConfigAccessor main = pack.getMain();
				main.getConfig().set("tag_point_prefix", null);
				main.saveConfig();
			}
			Debug.info("Done, all cross-package tags and points are now global, the rest is local.");
		} catch (Exception e) {
			e.printStackTrace();
			Debug.error(ERROR);
		}
		Debug.broadcast("Moved all package-less cross-package tags and points to \"" + globalName
				+ "\" package (you probably won't notice this change)");
		config.set("version", "v29");
		instance.saveConfig();
	}

	@SuppressWarnings("unused")
	private void update_from_v27() {
		try {
			config.set("journal.chars_per_page", "245");
			config.set("journal.one_entry_per_page", "false");
			config.set("journal.reversed_order", "false");
			config.set("journal.hide_date", "false");
		} catch (Exception e) {
			e.printStackTrace();
			Debug.error(ERROR);
		}
		Debug.broadcast("Added journal options");
		config.set("version", "v28");
		instance.saveConfig();
	}

	@SuppressWarnings("unused")
	private void update_from_v26() {
		try {
			for (ConfigPackage pack : Config.getPackages().values()) {
				String packName = pack.getName();
				for (String convName : pack.getConversationNames()) {
					FileConfiguration conv = pack.getConversation(convName).getConfig();
					ConfigurationSection playerSection = conv.getConfigurationSection("player_options");
					if (playerSection != null) {
						for (String playerKey : playerSection.getKeys(false)) {
							if (conv.isConfigurationSection("player_options." + playerKey + ".text")) {
								for (String langKey : conv
										.getConfigurationSection("player_options." + playerKey + ".text")
										.getKeys(false)) {
									conv.set("player_options." + playerKey + ".text." + langKey,
											conv.getString("player_options." + playerKey + ".text." + langKey)
													.replace("%quester%", "%npc%"));
								}
							} else {
								conv.set("player_options." + playerKey + ".text",
										conv.getString("player_options." + playerKey + ".text").replace("%quester%",
												"%npc%"));
							}
						}
					}
					ConfigurationSection npcSection = conv.getConfigurationSection("NPC_options");
					if (npcSection != null) {
						for (String npcKey : npcSection.getKeys(false)) {
							if (conv.isConfigurationSection("NPC_options." + npcKey + ".text")) {
								for (String langKey : conv.getConfigurationSection("NPC_options." + npcKey + ".text")
										.getKeys(false)) {
									conv.set("NPC_options." + npcKey + ".text." + langKey,
											conv.getString("NPC_options." + npcKey + ".text." + langKey)
													.replace("%quester%", "%npc%"));
								}
							} else {
								conv.set("NPC_options." + npcKey + ".text", conv
										.getString("NPC_options." + npcKey + ".text").replace("%quester%", "%npc%"));
							}
						}
					}
					pack.getConversation(convName).saveConfig();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.error(ERROR);
		}
		Debug.broadcast("Changed %quester% variables to %npc%");
		config.set("version", "v27");
		instance.saveConfig();
	}

	@SuppressWarnings("unused")
	private void update_from_v25() {
		try {
			for (ConfigPackage pack : Config.getPackages().values()) {
				String packName = pack.getName();
				FileConfiguration events = pack.getEvents().getConfig();
				for (String key : events.getKeys(false)) {
					String event = events.getString(key);
					if (event.startsWith("journal ")) {
						events.set(key, "journal add " + event.substring(8));
					}
				}
				pack.getEvents().saveConfig();
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.error(ERROR);
		}
		Debug.broadcast("Added \"add\" keyword to journal events");
		config.set("version", "v26");
		instance.saveConfig();
	}

	@SuppressWarnings("unused")
	private void update_from_v24() {
		Debug.broadcast("Added prefix to language files");
		config.set("version", "v25");
		instance.saveConfig();
	}

	@SuppressWarnings("unused")
	private void update_from_v23() {
		try {
			Debug.info("Adding option to disable mcMMO hooking to the config");
			config.set("hook.mcmmo", "true");
		} catch (Exception e) {
			e.printStackTrace();
			Debug.error(ERROR);
		}
		Debug.broadcast("Added mcMMO compatibility");
		config.set("version", "v24");
		instance.saveConfig();
	}

	@SuppressWarnings("unused")
	private void update_from_v22() {
		Debug.broadcast("Added Dutch translation");
		config.set("version", "v23");
		instance.saveConfig();
	}

	@SuppressWarnings("unused")
	private void update_from_v21() {
		try {
			Debug.info("Updating the database");
			Connection con = instance.getDB().getConnection();
			String prefix = Config.getString("config.mysql.prefix");
			// update database format
			Debug.info("Adding conversation column to player table");
			if (instance.isMySQLUsed()) {
				con.prepareStatement(
						"ALTER TABLE " + prefix + "player ADD conversation VARCHAR(512) AFTER language;")
						.executeUpdate();
			} else {
				con.prepareStatement("BEGIN TRANSACTION").executeUpdate();
				con.prepareStatement("ALTER TABLE " + prefix + "player RENAME TO " + prefix + "player_old")
						.executeUpdate();
				con.prepareStatement("CREATE TABLE IF NOT EXISTS " + prefix
						+ "player (id INTEGER PRIMARY KEY AUTOINCREMENT, playerID"
						+ " VARCHAR(256) NOT NULL, language VARCHAR(16) NOT NULL, conversation VARCHAR(512));")
						.executeUpdate();
				con.prepareStatement("INSERT INTO " + prefix + "player SELECT id, playerID, language, 'null'"
						+ " FROM " + prefix + "player_old").executeUpdate();
				con.prepareStatement("COMMIT").executeUpdate();
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.error(ERROR);
		}
		Debug.broadcast("Added conversations to database format");
		config.set("version", "v22");
		instance.saveConfig();
	}

	@SuppressWarnings("unused")
	private void update_from_v20() {
		try {
			ArrayList<ChatColor> npcColors = new ArrayList<>();
			ArrayList<ChatColor> textColors = new ArrayList<>();
			ArrayList<ChatColor> numberColors = new ArrayList<>();
			ArrayList<ChatColor> optionColors = new ArrayList<>();
			ArrayList<ChatColor> playerColors = new ArrayList<>();
			ArrayList<ChatColor> answerColors = new ArrayList<>();
			// get npc message format
			String npcFormat = config.getString("conversation.quester_line_format");
			String[] npcParts = npcFormat.split("%quester%");
			if (npcParts.length != 2) {
				Debug.info("Could not parse NPC text format, saving defaults");
				npcColors.add(ChatColor.DARK_RED);
				textColors.add(ChatColor.GREEN);
				textColors.add(ChatColor.ITALIC);
			} else {
				try {
					for (String code : npcParts[0].split("&")) {
						if (code.length() < 1)
							continue;
						npcColors.add(ChatColor.getByChar(code.charAt(0)));
					}
					for (String code : npcParts[1].split("&")) {
						if (code.length() < 1)
							continue;
						textColors.add(ChatColor.getByChar(code.charAt(0)));
					}
				} catch (Exception e) {
					e.printStackTrace();
					Debug.info("Could not parse NPC text format, saving defaults");
					npcColors.add(ChatColor.DARK_RED);
					textColors.add(ChatColor.GREEN);
					textColors.add(ChatColor.ITALIC);
				}
			}
			// get player option format
			String optionFormat = config.getString("conversation.quester_reply_format");
			String[] optionParts = optionFormat.split("%number%");
			if (optionParts.length != 2) {
				Debug.info("Could not parse player option format, saving defaults");
				numberColors.add(ChatColor.YELLOW);
				optionColors.add(ChatColor.AQUA);
			} else {
				try {
					for (String code : optionParts[0].split("&")) {
						if (code.length() < 1)
							continue;
						numberColors.add(ChatColor.getByChar(code.charAt(0)));
					}
					for (String code : optionParts[1].split("&")) {
						if (code.length() < 1)
							continue;
						optionColors.add(ChatColor.getByChar(code.charAt(0)));
					}
				} catch (Exception e) {
					e.printStackTrace();
					Debug.info("Could not parse player option format, saving defaults");
					numberColors.add(ChatColor.YELLOW);
					optionColors.add(ChatColor.AQUA);
				}
			}
			// get player answer format
			String answerFormat = config.getString("conversation.player_reply_format");
			String[] answerParts = answerFormat.split("%player%");
			if (answerParts.length != 2) {
				Debug.info("Could not parse player answer format, saving defaults");
				playerColors.add(ChatColor.DARK_GREEN);
				answerColors.add(ChatColor.GRAY);
			} else {
				try {
					for (String code : answerParts[0].split("&")) {
						if (code.length() < 1)
							continue;
						playerColors.add(ChatColor.getByChar(code.charAt(0)));
					}
					for (String code : answerParts[1].split("&")) {
						if (code.length() < 1)
							continue;
						answerColors.add(ChatColor.getByChar(code.charAt(0)));
					}
				} catch (Exception e) {
					e.printStackTrace();
					Debug.info("Could not parse player answer format, saving defaults");
					playerColors.add(ChatColor.DARK_GREEN);
					answerColors.add(ChatColor.GRAY);
				}
			}
			StringBuilder npc = new StringBuilder();
			StringBuilder text = new StringBuilder();
			StringBuilder number = new StringBuilder();
			StringBuilder option = new StringBuilder();
			StringBuilder player = new StringBuilder();
			StringBuilder answer = new StringBuilder();
			for (ChatColor color : npcColors) {
				if (color == null)
					continue;
				npc.append(color.name().toLowerCase() + ",");
			}
			config.set("conversation_colors.npc", npc.substring(0, npc.length() - 1));
			for (ChatColor color : textColors) {
				if (color == null)
					continue;
				text.append(color.name().toLowerCase() + ",");
			}
			config.set("conversation_colors.text", text.substring(0, text.length() - 1));
			for (ChatColor color : numberColors) {
				if (color == null)
					continue;
				number.append(color.name().toLowerCase() + ",");
			}
			config.set("conversation_colors.number", number.substring(0, number.length() - 1));
			for (ChatColor color : optionColors) {
				if (color == null)
					continue;
				option.append(color.name().toLowerCase() + ",");
			}
			config.set("conversation_colors.option", option.substring(0, option.length() - 1));
			for (ChatColor color : playerColors) {
				if (color == null)
					continue;
				player.append(color.name().toLowerCase() + ",");
			}
			config.set("conversation_colors.player", player.substring(0, player.length() - 1));
			for (ChatColor color : answerColors) {
				if (color == null)
					continue;
				answer.append(color.name().toLowerCase() + ",");
			}
			config.set("conversation_colors.answer", answer.substring(0, answer.length() - 1));
			config.set("conversation", null);
		} catch (Exception e) {
			e.printStackTrace();
			Debug.error(ERROR);
		}
		Debug.broadcast("Converted conversation format strings to colors");
		config.set("version", "v21");
		instance.saveConfig();
	}

	@SuppressWarnings("unused")
	private void update_from_v19() {
		try {
			if (config.getString("tellraw").equalsIgnoreCase("true")) {
				config.set("default_conversation_IO", "tellraw");
			} else {
				config.set("default_conversation_IO", "simple");
			}
			config.set("tellraw", null);
			FileConfiguration messages = Config.getMessages().getConfig();
			String message;
			message = messages.getString("global.quester_line_format");
			if (message == null)
				message = "&4%quester%&f: &a&o";
			config.set("conversation.quester_line_format", message);
			message = messages.getString("global.quester_reply_format");
			if (message == null)
				message = "&e%number%. &b";
			config.set("conversation.quester_reply_format", message);
			message = messages.getString("global.player_reply_format");
			if (message == null)
				message = "&2%player%&f: &7";
			config.set("conversation.player_reply_format", message);
			message = messages.getString("global.date_format");
			if (message == null)
				message = "dd.MM.yyyy HH:mm";
			config.set("date_format", message);
			String cancel_color = messages.getString("global.cancel_color", "&2");
			messages.set("global", null);
			Debug.broadcast("Moved 'global' messages to main config.");
			Config.getMessages().saveConfig();
			for (ConfigPackage pack : Config.getPackages().values()) {
				String packName = pack.getName();
				Debug.info("Processing " + packName + " package");
				ConfigurationSection cancelers = pack.getMain().getConfig().getConfigurationSection("cancel");
				for (String key : cancelers.getKeys(false)) {
					String canceler = cancelers.getString(key);
					StringBuilder string = new StringBuilder();
					for (String part : canceler.split(" ")) {
						if (part.startsWith("name")) {
							string.append(part.replace(":", ":" + cancel_color) + " ");
						} else {
							string.append(part + " ");
						}
					}
					cancelers.set(key, string.toString().trim());
					Debug.info("  Updated " + key + " canceler name color");
				}
				pack.getMain().saveConfig();
				for (String convName : pack.getConversationNames()) {
					ConfigAccessor conv = pack.getConversation(convName);
					conv.getConfig().set("unknown", null);
					conv.saveConfig();
					Debug.info("  Removed 'unknown' messages from " + convName + " conversation");
				}
			}
			Debug.broadcast("Removed no longer used 'unknown' message from conversations.");
		} catch (Exception e) {
			e.printStackTrace();
			Debug.error(ERROR);
		}
		config.set("version", "v20");
		instance.saveConfig();
	}

	@SuppressWarnings("unused")
	private void update_from_v18() {
		try {
			ConfigAccessor confMessages = Config.getMessages();
			FileConfiguration messages = confMessages.getConfig();
			for (String lang : messages.getKeys(false)) {
				if (lang.equalsIgnoreCase("global"))
					continue;
				Debug.info("Updating " + lang + " language");
				try {
					messages.set(lang + ".purged", messages.getString(lang + ".purged").replace("%player%", "{1}"));
					messages.set(lang + ".item_created",
							messages.getString(lang + ".item_created").replace("%item%", "{1}"));
					messages.set(lang + ".player_event",
							messages.getString(lang + ".player_event").replace("%event%", "{1}"));
					messages.set(lang + ".player_condition", messages.getString(lang + ".player_condition")
							.replace("%condition%", "{1}").replace("%outcome%", "{2}"));
					messages.set(lang + ".quest_canceled",
							messages.getString(lang + ".quest_canceled").replace("%quest%", "{1}"));
					messages.set(lang + ".items_given", messages.getString(lang + ".items_given")
							.replace("%name%", "{1}").replace("%amount%", "{2}"));
					messages.set(lang + ".items_taken", messages.getString(lang + ".items_taken")
							.replace("%name%", "{1}").replace("%amount%", "{2}"));
					messages.set(lang + ".blocks_to_break",
							messages.getString(lang + ".blocks_to_break").replace("%amount%", "{1}"));
					messages.set(lang + ".blocks_to_place",
							messages.getString(lang + ".blocks_to_place").replace("%amount%", "{1}"));
					messages.set(lang + ".mobs_to_kill",
							messages.getString(lang + ".mobs_to_kill").replace("%amount%", "{1}"));
					messages.set(lang + ".conversation_start",
							messages.getString(lang + ".conversation_start").replace("%quester%", "{1}"));
					messages.set(lang + ".conversation_end",
							messages.getString(lang + ".conversation_end").replace("%quester%", "{1}"));
				} catch (NullPointerException e) {
					Debug.error("The language " + lang + " is not present in the defaults, please update it manually.");
				}
			}
			confMessages.saveConfig();
			Debug.broadcast("Updated messages to new replace format");
		} catch (Exception e) {
			e.printStackTrace();
			Debug.error(ERROR);
		}
		config.set("version", "v19");
		instance.saveConfig();
	}

	@SuppressWarnings("unused")
	private void update_from_v17() {
		try {
			for (ConfigPackage pack : Config.getPackages().values()) {
				String packName = pack.getName();
				ConfigAccessor main = pack.getMain();
				main.getConfig().set("tag_point_prefix", "false");
				main.saveConfig();
			}
			Debug.broadcast("Added prefix option to all packages.");
		} catch (Exception e) {
			e.printStackTrace();
			Debug.error(ERROR);
		}
		config.set("version", "v18");
		instance.saveConfig();
	}

	@SuppressWarnings("unused")
	private void update_from_v16() {
		try {
			// move objectives from events.yml to objectives.yml
			Debug.info("Moving objectives to objectives.yml");
			for (ConfigPackage pack : Config.getPackages().values()) {
				String packName = pack.getName();
				Debug.info("  Package " + packName);
				ConfigAccessor events = pack.getEvents();
				ConfigAccessor objectives = pack.getObjectives();
				ConfigAccessor main = pack.getMain();
				for (String event : events.getConfig().getKeys(false)) {
					// extract label and build the new instruction
					int i = 0; // counts unnamed objectives
					String instruction = events.getConfig().getString(event);
					if (instruction.startsWith("objective ")) {
						Debug.info("    Starting event " + event);
						String[] parts = instruction.substring(10).split(" ");
						StringBuilder string = new StringBuilder();
						String label = null;
						String conditions = "";
						for (String part : parts) {
							if (part.startsWith("label:")) {
								label = part.substring(6);
							} else if (part.startsWith("event_conditions:")) {
								conditions = part;
							} else if (parts[0].equalsIgnoreCase("delay") && part.startsWith("delay:")) {
								string.append(part.substring(6));
								string.append(' ');
							} else {
								string.append(part);
								string.append(' ');
							}
						}
						String newInstruction = string.toString().trim();
						// if label is not present, skip this one
						if (label == null) {
							Debug.info("      There is no label, generating one");
							label = "objective" + i;
							i++;
						}
						Debug.info("      Saving the objective as " + label + ", instruction: " + newInstruction);
						// save objective and generate label
						objectives.getConfig().set(label, newInstruction);
						events.getConfig().set(event, ("objective start " + label + " " + conditions).trim());
					} else if (instruction.startsWith("delete ")) {
						// update delete events
						Debug.info("    Delete event " + event);
						events.getConfig().set(event, "objective " + instruction);
					}
				}
				// rename event_conditions to conditions
				for (String event : events.getConfig().getKeys(false)) {
					String instruction = events.getConfig().getString(event);
					events.getConfig().set(event, instruction.replace("event_conditions:", "conditions:"));
				}
				// update global locations
				String raw = main.getConfig().getString("global_locations");
				if (raw != null && !raw.equals("")) {
					StringBuilder string = new StringBuilder();
					String[] parts = raw.split(",");
					for (String event : parts) {
						String inst = events.getConfig().getString(event);
						if (inst == null) {
							continue;
						}
						String[] instParts = inst.split(" ");
						if (instParts.length > 2 && inst.startsWith("objective start ")) {
							string.append(instParts[2] + ",");
						}
					}
					main.getConfig().set("global_locations", string.substring(0, string.length() - 1));
				}
				events.saveConfig();
				objectives.saveConfig();
				main.saveConfig();
			}
			Debug.broadcast("Moved objectives to a separate file and renamed"
					+ " 'event_conditions:' argument to 'conditions:'");
			Debug.info("Updating the database");
			Connection con = instance.getDB().getConnection();
			String prefix = Config.getString("config.mysql.prefix");
			// update database format
			Debug.info("Updating the database format");
			if (instance.isMySQLUsed()) {
				con.prepareStatement(
						"ALTER TABLE " + prefix + "objectives ADD objective VARCHAR(512) NOT NULL AFTER playerID;")
						.executeUpdate();
			} else {
				con.prepareStatement("BEGIN TRANSACTION").executeUpdate();
				con.prepareStatement("ALTER TABLE " + prefix + "objectives RENAME TO " + prefix + "objectives_old")
						.executeUpdate();
				con.prepareStatement(
						"CREATE TABLE IF NOT EXISTS " + prefix + "objectives (id INTEGER PRIMARY KEY AUTOINCREMENT,"
								+ " playerID VARCHAR(256) NOT NULL, objective VARCHAR(512)"
								+ " NOT NULL, instructions VARCHAR(2048) NOT NULL);")
						.executeUpdate();
				con.prepareStatement("INSERT INTO " + prefix + "objectives"
						+ " SELECT id, playerID, 'null', instructions FROM " + prefix + "objectives_old")
						.executeUpdate();
				con.prepareStatement("COMMIT").executeUpdate();
			}
			// update each entry
			Debug.info("Updating entries");
			ResultSet res = con.prepareStatement("SELECT * FROM " + prefix + "objectives").executeQuery();
			while (res.next()) {
				String oldInst = res.getString("instructions");
				Debug.info("  Loaded instruction: " + oldInst);
				String label = null;
				String[] parts = oldInst.split(" ");
				String newInst;
				for (String part : parts) {
					if (part.startsWith("label:")) {
						label = part.substring(6);
						break;
					}
				}
				if (label == null) {
					Debug.info("    The objective without label, removing");
					PreparedStatement stmt = con.prepareStatement("DELETE FROM " + prefix + "objectives WHERE id = ?");
					stmt.setInt(1, res.getInt("id"));
					stmt.executeUpdate();
					continue;
				}
				// attack correct package in front of the label
				for (ConfigPackage pack : Config.getPackages().values()) {
					String packName = pack.getName();
					if (pack.getObjectives().getConfig().contains(label)) {
						label = packName + "." + label;
						break;
					}
				}
				try {
					switch (parts[0].toLowerCase()) {
					case "tame":
					case "block":
					case "smelt":
					case "craft":
					case "mobkill":
						newInst = parts[2];
						break;
					case "delay":
						newInst = parts[1].substring(6);
						break;
					case "npckill":
					case "mmobkill":
						newInst = parts[2].substring(7);
						break;
					default:
						newInst = "";
					}
				} catch (ArrayIndexOutOfBoundsException e) {
					Debug.info("    Could not read data from objective " + label + ", removing");
					PreparedStatement stmt = con
							.prepareStatement("DELETE FROM " + prefix + "objectives WHERE id = ?");
					stmt.setInt(1, res.getInt("id"));
					stmt.executeUpdate();
					continue;
				}
				Debug.info("    Updating the " + label + " objective: '" + newInst + "'");
				PreparedStatement stmt = con.prepareStatement(
						"UPDATE " + prefix + "objectives SET objective=?, instructions=? WHERE id = ?");
				stmt.setString(1, label);
				stmt.setString(2, newInst);
				stmt.setInt(3, res.getInt("id"));
				stmt.executeUpdate();
			}
			Debug.broadcast("Updated objective instruction strings in the database");
		} catch (Exception e) {
			e.printStackTrace();
			Debug.error(ERROR);
		}
		config.set("version", "v17");
		instance.saveConfig();
	}

	@SuppressWarnings("unused")
	private void update_from_v15() {
		try {
			config.set("remove_items_after_respawn", "true");
		} catch (Exception e) {
			e.printStackTrace();
			Debug.error(ERROR);
		}
		config.set("version", "v16");
		instance.saveConfig();
	}

	@SuppressWarnings("unused")
	private void update_from_v14() {
		try {
			if (config.getString("uuid").equals("false")) {
				convertNamesToUUID();
			}
			config.set("default_package", "default");
			config.set("cmd_blacklist", new String[] { "spawn" });
			config.set("uuid", null);
			config.set("metrics", null);
			config.set("hook.citizens", "true");
			config.set("hook.mythicmobs", "true");
			config.set("hook.vault", "true");
			config.set("hook.worldguard", "true");
			config.set("hook.skript", "true");
			Debug.broadcast("Added default_package, hook and cmd_blacklist"
					+ " options to main config, removed metrics and uuid!");
		} catch (Exception e) {
			e.printStackTrace();
			Debug.error(ERROR);
		}
		config.set("version", "v15");
		instance.saveConfig();
	}

	@SuppressWarnings("unused")
	private void update_from_v13() {
		try {
			Debug.info("Removing empty lines in conversation files");
			for (ConfigPackage pack : Config.getPackages().values()) {
				String packName = pack.getName();
				Debug.info("  Package " + packName);
				for (String convName : pack.getConversationNames()) {
					Debug.info("    Conversation " + convName);
					ConfigAccessor conv = pack.getConversation(convName);
					for (String key : conv.getConfig().getKeys(true)) {
						if (conv.getConfig().getString(key).equals("")) {
							Debug.info("      Key removed: " + key);
							conv.getConfig().set(key, null);
						}
					}
					conv.saveConfig();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.error(ERROR);
		}
		Debug.broadcast("Removed unnecessary empty lines in conversation config files.");
		config.set("version", "v14");
		instance.saveConfig();
	}

	@SuppressWarnings("unused")
	private void update_from_v12() {
		try {
			Debug.info("Moving all configuration to \"default\" package");
			// clear the default package, which contains only default quest
			File defPkg = Config.getPackages().get("default").getFolder();
			Debug.info("  Deleting default files");
			for (File file : defPkg.listFiles()) {
				file.delete();
			}
			// move files that can be moved without modifications
			File root = instance.getDataFolder();
			String[] filesToMove = new String[] { "events", "conditions", "items", "journal" };
			for (String fileToMove : filesToMove) {
				Debug.info("  Moving " + fileToMove + ".yml file");
				new File(root, fileToMove + ".yml").renameTo(new File(defPkg, fileToMove + ".yml"));
			}
			// move all conversations
			File newConversationFolder = new File(defPkg, "conversations");
			File oldConversationFolder = new File(root, "conversations");
			newConversationFolder.mkdir();
			for (File conversation : oldConversationFolder.listFiles()) {
				Debug.info("  Moving " + conversation.getName() + " conversation file");
				conversation.renameTo(new File(newConversationFolder, conversation.getName()));
			}
			// generate main.yml file
			Debug.info("  Generating main.yml file");
			File mainFile = new File(defPkg, "main.yml");
			mainFile.createNewFile();
			FileConfiguration main = YamlConfiguration.loadConfiguration(mainFile);
			// copy the data
			String globalLocations = config.getString("global_locations");
			ConfigurationSection staticEvents = config.getConfigurationSection("static");
			ConfigurationSection npcs = ch.getConfigs().get("npcs").getConfig().getRoot();
			main.set("global_locations", globalLocations);
			if (staticEvents != null) {
				for (String key : staticEvents.getKeys(false)) {
					main.set("static." + key, staticEvents.getString(key));
				}
			}
			if (npcs != null) {
				for (String key : npcs.getKeys(false)) {
					main.set("npcs." + key, npcs.getString(key));
				}
				for (File conv : newConversationFolder.listFiles()) {
					main.set("npcs." + conv.getName().replace(".yml", ""), conv.getName().replace(".yml", ""));
				}
			}
			main.save(mainFile);
			// remove old values from configuration
			Debug.info("  Removing old files and config values");
			oldConversationFolder.delete();
			config.set("global_locations", null);
			config.set("static", null);
			new File(root, "npcs.yml").delete();
			Debug.info("Configuration updated!");
			Debug.broadcast("Updating the database, it may take a long time!");
			Connection con = instance.getDB().getConnection();
			String prefix = instance.getConfig().getString("mysql.prefix", "");
			ResultSet res = con.createStatement().executeQuery("SELECT * FROM " + prefix + "objectives");
			ArrayList<String[]> objectives = new ArrayList<>();
			// iterate over every objective string in the database
			while (res.next()) {
				String[] parts = res.getString("instructions").split(" ");
				StringBuilder newInstruction = new StringBuilder();
				for (String part : parts) {
					if (part.startsWith("events:")) {
						newInstruction.append("events:");
						String[] events = part.substring(7).split(",");
						for (String event : events) {
							newInstruction.append("default." + event + ",");
						}
						newInstruction.deleteCharAt(newInstruction.length() - 1);
					} else if (part.startsWith("conditions:")) {
						newInstruction.append("conditions:");
						String[] conditions = part.substring(11).split(",");
						for (String condition : conditions) {
							newInstruction.append("default." + condition + ",");
						}
						newInstruction.deleteCharAt(newInstruction.length() - 1);
					} else {
						newInstruction.append(part);
					}
					newInstruction.append(' ');
				}
				objectives.add(new String[] { res.getString("playerID"), newInstruction.toString().trim() });
			}
			res = con.createStatement().executeQuery("SELECT * FROM " + prefix + "journal");
			ArrayList<String[]> pointers = new ArrayList<>();
			// iterate over every journal pointer in the database
			while (res.next()) {
				pointers.add(new String[] { res.getString("playerID"), "default." + res.getString("pointer"),
						res.getString("date") });
			}
			con.createStatement().executeUpdate("DELETE FROM " + prefix + "objectives");
			con.createStatement().executeUpdate("DELETE FROM " + prefix + "journal");
			for (String[] objective : objectives) {
				PreparedStatement stmt = con.prepareStatement(
						"INSERT INTO " + prefix + "objectives (playerID, instructions) VALUES (?,?)");
				stmt.setString(1, objective[0]);
				stmt.setString(2, objective[1]);
				stmt.executeUpdate();
			}
			for (String[] pointer : pointers) {
				PreparedStatement stmt = con.prepareStatement(
						"INSERT INTO " + prefix + "journal (playerID, pointer, date) VALUES (?,?,?)");
				stmt.setString(1, pointer[0]);
				stmt.setString(2, pointer[1]);
				stmt.setString(3, pointer[2]);
				stmt.executeUpdate();
			}
			Debug.info("Done! Everything converted.");
		} catch (Exception e) {
			e.printStackTrace();
			Debug.error(ERROR);
		}
		Debug.broadcast("Introduced new packaging system and moved configuration to \"default\" package!");
		config.set("version", "v13");
		instance.saveConfig();
	}

	@SuppressWarnings("unused")
	private void update_from_v11() {
		try {
			Debug.info("Updating objectives in configuration");
			ConfigAccessor events = ch.getConfigs().get("events");
			ArrayList<String> labels = new ArrayList<>();
			boolean notified = false;
			// for every event check if it's objective
			for (String key : events.getConfig().getKeys(false)) {
				String value = events.getConfig().getString(key);
				if (value.startsWith("objective ")) {
					Debug.info("  Found " + key + " objective event");
					// replace "tag:" with "label:" in all found objectives
					String[] parts = value.split(" ");
					StringBuilder builder = new StringBuilder();
					for (int i = 0; i < parts.length; i++) {
						if (parts[i].startsWith("tag:")) {
							String label = parts[i].substring(4);
							if (!notified && labels.contains(label)) {
								notified = true;
								Debug.error("You have multiple objectives with the same label!"
										+ " That is an error, because the player cannot have"
										+ " active more than one objective with the same label");
							}
							labels.add(label);
							parts[i] = "label:" + label;
						}
						builder.append(parts[i]);
						builder.append(" ");
					}
					String newValue = builder.toString().trim();
					Debug.info("    After processing: " + newValue);
					events.getConfig().set(key, newValue);
				}
			}
			events.saveConfig();
			Debug.info("Converted all objectives in configuration");
			// update all objectives in the database
			Debug.broadcast("Converting objectives in the database, it may take a long time");
			Connection con = instance.getDB().getConnection();
			String prefix = instance.getConfig().getString("mysql.prefix", "");
			ResultSet res = con.createStatement().executeQuery("SELECT * FROM " + prefix + "objectives");
			HashMap<String, ArrayList<String>> objectives = new HashMap<>();
			HashMap<String, ArrayList<String>> labels2 = new HashMap<>();
			// iterate over every objective string in the database
			while (res.next()) {
				String playerID = res.getString("playerID");
				String objective = res.getString("instructions");
				String label = null;
				for (String part : objective.split(" ")) {
					if (part.startsWith("tag:")) {
						label = part.substring(4);
					}
				}
				if (label == null) {
					Debug.info("  Found objective without a label, that's strange... Anyway, skipping. Player: "
							+ playerID);
					continue;
				}
				Debug.info("  Found objective for player " + playerID + " with label " + label);
				ArrayList<String> oList = objectives.get(playerID);
				ArrayList<String> lList = labels2.get(playerID);
				if (oList == null) {
					oList = new ArrayList<>();
					lList = new ArrayList<>();
				}
				// cannot have two objectives with the same tag
				if (lList.contains(label)) {
					Debug.info("    Label already exists, skipping this one!");
					continue;
				}
				String converted = convertObjective(objective);
				Debug.info("    Objective converted: " + converted);
				oList.add(converted);
				lList.add(label);
				objectives.put(playerID, oList);
				labels2.put(playerID, lList);
			}
			// everything is extracted from the database and converted
			// time to put it back
			Debug.info("Inserting everything into the database...");
			con.createStatement().executeUpdate("DELETE FROM " + prefix + "objectives");
			for (String playerID : objectives.keySet()) {
				for (String objective : objectives.get(playerID)) {
					PreparedStatement stmt = con.prepareStatement(
							"INSERT INTO " + prefix + "objectives (playerID, instructions) VALUES (?,?);");
					stmt.setString(1, playerID);
					stmt.setString(2, objective);
					stmt.executeUpdate();
				}
			}
			Debug.info("Done! Everything converted");
		} catch (Exception e) {
			e.printStackTrace();
			Debug.error(ERROR);
		}
		Debug.broadcast("Changed keyword \"tag:\" to \"label:\" in all objectives!");
		config.set("version", "v12");
		instance.saveConfig();
	}

	@SuppressWarnings("unused")
	private void update_from_v10() {
		try {
			Debug.info("Updating instruction strings");
			Debug.info("  Updating conditions");
			ConfigAccessor conditions = ch.getConfigs().get("conditions");
			conditions: for (String key : conditions.getConfig().getKeys(false)) {
				Debug.info("    Processing " + key + " condition");
				String instruction = conditions.getConfig().getString(key).trim();
				String[] parts = instruction.split(" ");
				String type = parts[0].toLowerCase();
				ArrayList<String> newParts = new ArrayList<>();
				newParts.add(type);
				switch (type) {
				case "hand":
					Debug.info("      Found hand type");
					String item = null;
					for (String part : parts) {
						if (part.startsWith("item:")) {
							item = part.substring(5);
						}
					}
					if (item != null) {
						newParts.add(item);
					} else {
						Debug.info("      There is no item defined, skipping");
						continue conditions;
					}
					break;
				case "or":
				case "and":
					Debug.info("      Found or/and type");
					String orAndConditions = null;
					for (String part : parts) {
						if (part.startsWith("conditions:")) {
							orAndConditions = part.substring(11);
						}
					}
					if (orAndConditions != null) {
						newParts.add(orAndConditions);
					} else {
						Debug.info("      There are no conditions defined, skipping");
						continue conditions;
					}
					break;
				case "location":
					Debug.info("      Found location type");
					String location = null;
					for (String part : parts) {
						if (part.startsWith("loc:")) {
							location = part.substring(4);
						}
					}
					if (location != null) {
						newParts.add(location);
					} else {
						Debug.info("      There is no location defined, skipping");
						continue conditions;
					}
					break;
				case "health":
					Debug.info("      Found health type");
					String health = null;
					for (String part : parts) {
						if (part.startsWith("health:")) {
							health = part.substring(7);
						}
					}
					if (health != null) {
						newParts.add(health);
					} else {
						Debug.info("      There is no health amount defined, skipping");
						continue conditions;
					}
					break;
				case "experience":
					Debug.info("      Found experience type");
					String exp = null;
					for (String part : parts) {
						if (part.startsWith("exp:")) {
							exp = part.substring(4);
						}
					}
					if (exp != null) {
						newParts.add(exp);
					} else {
						Debug.info("      There is no experience level defined, skipping");
						continue conditions;
					}
					break;
				case "permission":
					Debug.info("      Found permission type");
					String perm = null;
					for (String part : parts) {
						if (part.contains("perm:")) {
							perm = part.substring(5);
						}
					}
					if (perm != null) {
						newParts.add(perm);
					} else {
						Debug.info("      There is no permission defined, skipping");
						continue conditions;
					}
					break;
				case "point":
					Debug.info("      Found point type");
					String category = null;
					String amount = null;
					for (String part : parts) {
						if (part.startsWith("category:")) {
							category = part.substring(9);
						} else if (part.startsWith("count:")) {
							amount = part.substring(6);
						}
					}
					if (category != null && amount != null) {
						newParts.add(category);
						newParts.add(amount);
					} else {
						Debug.info("      There is no category/amount defined, skipping");
						continue conditions;
					}
					break;
				case "tag":
					Debug.info("      Found tag type");
					String tag = null;
					for (String part : parts) {
						if (part.startsWith("tag:")) {
							tag = part.substring(4);
						}
					}
					if (tag != null) {
						newParts.add(tag);
					} else {
						Debug.info("      There is no tag defined, skipping");
						continue conditions;
					}
					break;
				case "armor":
					Debug.info("      Found armor type");
					String material = null;
					String armorType = null;
					String enchants = null;
					for (String part : parts) {
						if (part.startsWith("material:")) {
							material = part.substring(9);
						}
						if (part.startsWith("type:")) {
							armorType = part.substring(5);
						}
						if (part.startsWith("enchants:")) {
							enchants = part;
						}
					}
					if (material != null && type != null) {
						Material armor = null;
						try {
							armor = Material.matchMaterial(material + "_" + armorType);
						} catch (Exception e) {
							Debug.info("      Could not read armor type, skipping");
							continue conditions;
						}
						String itemInstruction = armor.toString();
						if (enchants != null) {
							itemInstruction = itemInstruction + " " + enchants;
						}
						ConfigAccessor itemsConfig = ch.getConfigs().get("items");
						int i = 0;
						while (itemsConfig.getConfig().contains("armor" + i)) {
							i++;
						}
						itemsConfig.getConfig().set("armor" + i, itemInstruction);
						itemsConfig.saveConfig();
						newParts.add("armor" + i);
					} else {
						Debug.info("      There is no armor defined, skipping");
						continue conditions;
					}
					break;
				case "effect":
					Debug.info("      Found effect type");
					String effect = null;
					for (String part : parts) {
						if (part.startsWith("type:")) {
							effect = part.substring(5);
						}
					}
					if (effect != null) {
						newParts.add(effect);
					} else {
						Debug.info("      There is no effect defined, skipping");
						continue conditions;
					}
					break;
				case "time":
					Debug.info("      Found time type");
					String time = null;
					for (String part : parts) {
						if (part.startsWith("time:")) {
							time = part.substring(5);
						}
					}
					if (time != null) {
						newParts.add(time);
					} else {
						Debug.info("      There is no time defined, skipping");
						continue conditions;
					}
					break;
				case "weather":
					Debug.info("      Found weather type");
					String weather = null;
					for (String part : parts) {
						if (part.startsWith("type:")) {
							weather = part.substring(5);
						}
					}
					if (weather != null) {
						newParts.add(weather);
					} else {
						Debug.info("      There is no weather defined, skipping");
						continue conditions;
					}
					break;
				case "height":
					Debug.info("      Found height type");
					String height = null;
					for (String part : parts) {
						if (part.startsWith("height:")) {
							height = part.substring(7);
						}
					}
					if (height != null) {
						newParts.add(height);
					} else {
						Debug.info("      There is no height defined, skipping");
						continue conditions;
					}
					break;
				case "rating":
					Debug.info("      Found rating type");
					String rating = null;
					for (String part : parts) {
						if (part.startsWith("rating:")) {
							rating = part.substring(7);
						}
					}
					if (rating != null) {
						newParts.add(rating);
					} else {
						Debug.info("      There is no rating defined, skipping");
						continue conditions;
					}
					break;
				case "random":
					Debug.info("      Found random type");
					String random = null;
					for (String part : parts) {
						if (part.startsWith("random:")) {
							random = part.substring(7);
						}
					}
					if (random != null) {
						newParts.add(random);
					} else {
						Debug.info("      There is no random defined, skipping");
						continue conditions;
					}
					break;
				case "money":
					Debug.info("      Found money type");
					String money = null;
					for (String part : parts) {
						if (part.startsWith("money:")) {
							money = part.substring(6);
						}
					}
					if (money != null) {
						newParts.add(money);
					} else {
						Debug.info("      There is no amount defined, skipping");
						continue conditions;
					}
					break;
				default:
					Debug.info("      This one does not need updating");
					continue conditions;
				}
				StringBuilder builder = new StringBuilder();
				for (String part : newParts) {
					builder.append(part);
					builder.append(' ');
				}
				String newInstruction = builder.toString().trim();
				Debug.info("      Processing done, instruction: '" + newInstruction + "'");
				conditions.getConfig().set(key, newInstruction);
			}
			Debug.info("  All conditions updated successfully, saving to the file");
			conditions.saveConfig();

			Debug.info("  Updating events");
			ConfigAccessor events = ch.getConfigs().get("events");
			events: for (String key : events.getConfig().getKeys(false)) {
				Debug.info("    Processing " + key + " event");
				String instruction = events.getConfig().getString(key).trim();
				String[] parts = instruction.split(" ");
				String type = parts[0].toLowerCase();
				ArrayList<String> newParts = new ArrayList<>();
				newParts.add(type);
				switch (type) {
				case "folder":
					Debug.info("      Found folder type");
					String folderEvents = null;
					String delay = null;
					String random = null;
					for (String part : parts) {
						if (part.startsWith("events:")) {
							folderEvents = part.substring(7);
						}
						if (part.startsWith("delay:")) {
							delay = part;
						}
						if (part.startsWith("random:")) {
							random = part;
						}
					}
					if (events != null) {
						newParts.add(folderEvents);
						if (delay != null) {
							newParts.add(delay);
						}
						if (random != null) {
							newParts.add(random);
						}
					} else {
						Debug.info("      There are no events defined, skipping");
						continue events;
					}
					break;
				case "setblock":
					Debug.info("      Found setblock type");
					String block = null;
					String loc = null;
					String data = null;
					for (String part : parts) {
						if (part.startsWith("block:")) {
							block = part.substring(6);
						}
						if (part.startsWith("loc:")) {
							loc = part.substring(4);
						}
						if (part.startsWith("data:")) {
							data = part;
						}
					}
					if (block != null && loc != null) {
						newParts.add(block);
						newParts.add(loc);
						if (data != null) {
							newParts.add(data);
						}
					} else {
						Debug.info("      There is no block/location defined, skipping");
						continue events;
					}
					break;
				default:
					Debug.info("      This one does not need updating");
					continue events;
				}
				StringBuilder builder = new StringBuilder();
				for (String part : newParts) {
					builder.append(part);
					builder.append(' ');
				}
				String newInstruction = builder.toString().trim();
				Debug.info("      Processing done, instruction: '" + newInstruction + "'");
				events.getConfig().set(key, newInstruction);
			}
			Debug.info("  All events updated successfully, saving to the file");
			events.saveConfig();

		} catch (Exception e) {
			e.printStackTrace();
			Debug.error(ERROR);
		}
		Debug.broadcast("Made instruction strings more beautiful! Please read the documentation again.");
		config.set("version", "v11");
		instance.saveConfig();
	}

	@SuppressWarnings("unused")
	private void update_from_v9() {
		config.set("combat_delay", "10");
		config.set("notify_pullback", "false");
		Debug.broadcast("Added combat delay and pullback notify options!");
		config.set("version", "v10");
		instance.saveConfig();
	}

	@SuppressWarnings("unused")
	private void update_from_v8() {
		config.set("version", "v9");
		instance.saveConfig();
	}

	@SuppressWarnings("unused")
	private void update_from_v7() {
		ConfigAccessor messages = ch.getConfigs().get("messages");
		messages.getConfig().set("global.date_format", "dd.MM.yyyy HH:mm");
		messages.saveConfig();
		Debug.broadcast("Added date format line to messages.yml");
		config.set("version", "v8");
		instance.saveConfig();
	}

	@SuppressWarnings("unused")
	private void update_from_v6() {
		Debug.broadcast("Added backpacks to the database!");
		config.set("version", "v7");
		instance.saveConfig();
	}

	@SuppressWarnings("unused")
	private void update_from_v5() {
		try {
			// delete isused column from tables objectives and tags
			Database database = instance.getDB();
			Connection connection = database.getConnection();
			String[] tables = new String[] { "objectives", "tags" };
			String prefix = instance.getConfig().getString("mysql.prefix", "");
			if (instance.isMySQLUsed()) {
				connection.prepareStatement("ALTER TABLE " + prefix + "objectives DROP COLUMN isused;").executeUpdate();
				connection.prepareStatement("ALTER TABLE " + prefix + "tags DROP COLUMN isused;").executeUpdate();
			} else {
				// drop column from objectives
				connection.prepareStatement("BEGIN TRANSACTION").executeUpdate();
				connection
						.prepareStatement("ALTER TABLE " + prefix + "objectives RENAME TO " + prefix + "objectives_old")
						.executeUpdate();
				connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + prefix + "objectives"
						+ " (id INTEGER PRIMARY KEY AUTOINCREMENT, playerID VARCHAR(256) NOT NULL, "
						+ "instructions VARCHAR(2048) NOT NULL);").executeUpdate();
				connection.prepareStatement("INSERT INTO " + prefix + "objectives SELECT id, "
						+ "playerID, instructions FROM " + prefix + "objectives_old").executeUpdate();
				connection.prepareStatement("DROP TABLE " + prefix + "objectives_old").executeUpdate();
				connection.prepareStatement("COMMIT").executeUpdate();
				// drop column from tags
				connection.prepareStatement("BEGIN TRANSACTION").executeUpdate();
				connection.prepareStatement("ALTER TABLE " + prefix + "tags RENAME TO " + prefix + "tags_old")
						.executeUpdate();
				connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + prefix + "tags"
						+ " (id INTEGER PRIMARY KEY AUTOINCREMENT, playerID VARCHAR(256) NOT NULL, "
						+ "tag TEXT NOT NULL);").executeUpdate();
				connection.prepareStatement(
						"INSERT INTO " + prefix + "tags SELECT id, playerID, tag FROM " + prefix + "tags_old")
						.executeUpdate();
				connection.prepareStatement("DROP TABLE " + prefix + "tags_old").executeUpdate();
				connection.prepareStatement("COMMIT").executeUpdate();
			}
			Debug.broadcast("Updated database format to better one.");
		} catch (Exception e) {
			e.printStackTrace();
			Debug.error(ERROR);
		}
		config.set("version", "v6");
		instance.saveConfig();
	}

	@SuppressWarnings("unused")
	private void update_from_v4() {
		try {
			// update all give/take events and item condition to match new
			// parser
			ConfigAccessor eventsAccessor = ch.getConfigs().get("events");
			FileConfiguration eventsConfig = eventsAccessor.getConfig();
			Debug.info("Updating events!");
			// check every event in configuration
			for (String key : eventsConfig.getKeys(false)) {
				Debug.info("  Processing " + key);
				String instruction = eventsConfig.getString(key);
				// if the event is of type "give" or "take" then proceed
				if (instruction.startsWith("give ") || instruction.startsWith("take ")) {
					String[] parts = instruction.split(" ");
					Debug.info("    Found " + parts[0] + " event");
					// get item's amount
					int amount = 1;
					for (String part : parts) {
						if (part.startsWith("amount:")) {
							amount = Integer.parseInt(part.substring(7));
							Debug.info("    Amount is set to " + amount);
						}
					}
					// generate new instruction
					String newInstruction = parts[0] + " " + parts[1] + ((amount != 1) ? ":" + amount : "");
					Debug.info("    Saving instruction '" + newInstruction + "'");
					// save it
					eventsConfig.set(key, newInstruction);
				}
			}
			// when all events are converted, save the file
			eventsAccessor.saveConfig();
			// update all item conditions
			ConfigAccessor conditionsAccessor = ch.getConfigs().get("conditions");
			FileConfiguration conditionsConfig = conditionsAccessor.getConfig();
			Debug.info("Updatng conditions!");
			// check every condition in configuration
			for (String key : conditionsConfig.getKeys(false)) {
				Debug.info("  Processing " + key);
				String instruction = conditionsConfig.getString(key);
				// if the condition is of type "item" then proceed
				if (instruction.startsWith("item ")) {
					String[] parts = instruction.split(" ");
					Debug.info("    Found item condition");
					// get item name and amount
					String name = null;
					int amount = 1;
					for (String part : parts) {
						if (part.startsWith("item:")) {
							name = part.substring(5);
							Debug.info("    Name is " + name);
						} else if (part.startsWith("amount:")) {
							amount = Integer.parseInt(part.substring(7));
							Debug.info("    Amount is " + amount);
						}
					}
					// generate new instruction
					String newInstruction = "item " + name + ((amount != 1) ? ":" + amount : "");
					Debug.info("    Saving instruction '" + newInstruction + "'");
					// save it
					conditionsConfig.set(key, newInstruction);
				}
			}
			// when all conditions are converted, save the file
			conditionsAccessor.saveConfig();
			Debug.broadcast("Converted give/take events and item conditions to new format!");
		} catch (Exception e) {
			e.printStackTrace();
			Debug.error(ERROR);
		}
		config.set("version", "v5");
		instance.saveConfig();
	}

	@SuppressWarnings("unused")
	private void update_from_v3() {
		config.set("mysql.prefix", "");
		Debug.broadcast("Added prefix option to MySQL settings!");
		config.set("version", "v4");
		instance.saveConfig();
	}

	@SuppressWarnings("unused")
	private void update_from_v2() {
		try {
			// start time counting, because why not?
			long time = new Date().getTime();
			// Get all conditions with --inverted tag into the map
			// <name,instruction> without --inverted tag and remove them form
			// config
			ConfigAccessor conditionsAccessor = ch.getConfigs().get("conditions");
			FileConfiguration conditionsConfig = conditionsAccessor.getConfig();
			// at the beginning trim all conditions, so they won't get
			// confused later on
			for (String path : conditionsConfig.getKeys(false)) {
				conditionsConfig.set(path, conditionsConfig.getString(path).trim());
			}
			HashMap<String, String> conditionsInverted = new HashMap<>();
			Debug.info("Extracting conditions to a map");
			// for each condition
			for (String name : conditionsConfig.getKeys(false)) {
				// get instruction
				String condition = conditionsConfig.getString(name);
				boolean wasInverted = false;
				int i = 1;
				Debug.info("  Checking condition " + name);
				// if it is --inverted
				while (condition.contains("--inverted")) {
					Debug.info("    Loop " + i);
					i++;
					Debug.info("      Instruction: '" + condition + "'");
					// get starting index of --inverted
					int startingIndex = condition.indexOf(" --inverted");
					Debug.info("      First occurence of --inverted tag: " + startingIndex);
					// get first half (to cut --inverted)
					String firstHalf = condition.substring(0, startingIndex);
					Debug.info("      First half is '" + firstHalf + "'");
					// get last half (from the end of --inverted string)
					String lastHalf = condition.substring(startingIndex + 11);
					Debug.info("      Last half is '" + lastHalf + "'");
					// get new condition string without --inverted tag
					condition = firstHalf + lastHalf;
					wasInverted = true;
					Debug.info("      And the whole new condition is '" + condition + "'");
				}
				if (wasInverted) {
					Debug.info("  Removing from config and putting into a map!");
					// remove it from config
					conditionsConfig.set(name, null);
					// put it into the map
					conditionsInverted.put(name, condition);
				}
			}
			// for each, check for duplicates
			Debug.info("Checking for duplicates in config");
			HashMap<String, String> nameChanging = new HashMap<>();
			for (String invertedName : conditionsInverted.keySet()) {
				// check every condition from the map
				Debug.info("  Checking condition " + invertedName);
				String duplicateName = null;
				for (String normalName : conditionsConfig.getKeys(false)) {
					// against every condition that is still in the config
					if (conditionsConfig.getString(normalName).equals(conditionsInverted.get(invertedName))) {
						// if it is the same, then we have a match; we need to
						// mark it as a duplicate
						Debug.info("    Found a duplicate: " + normalName);
						duplicateName = normalName;
					}
				}
				if (duplicateName != null) {
					// if it still exists in config, put it into map <old
					// name, new name> as duplicate and !original
					Debug.info("    Inserting into name changing map, from " + invertedName + " to !" + duplicateName);
					nameChanging.put(invertedName, "!" + duplicateName);
				} else {
					// if it doesn't, put into a map as original and !original,
					// and reinsert into config
					Debug.info("    Inserting into name changing map, from " + invertedName + " to !" + invertedName);
					Debug.info("    Readding to configuration!");
					nameChanging.put(invertedName, "!" + invertedName);
					conditionsConfig.set(invertedName, conditionsInverted.get(invertedName));
				}
			}
			Debug.info("Starting conditions updating!");
			for (String key : conditionsConfig.getKeys(false)) {
				String instruction = conditionsConfig.getString(key).trim();
				Debug.info("  Processing condition " + key);
				if (instruction.startsWith("or ") || instruction.startsWith("and ")) {
					String type = instruction.substring(0, instruction.indexOf(" "));
					Debug.info("    Found " + type + " condition!");
					int index = instruction.indexOf(" conditions:") + 12;
					String firstPart = instruction.substring(0, index);
					Debug.info("    First part is '" + firstPart + "'");
					int secondIndex = index + instruction.substring(index).indexOf(" ");
					if (secondIndex <= index) {
						secondIndex = instruction.length();
					}
					String conditionList = instruction.substring(index, secondIndex);
					Debug.info("    List of conditions is '" + conditionList + "'");
					String lastPart = instruction.substring(secondIndex);
					Debug.info("    Last part is '" + lastPart + "'");
					String[] parts = conditionList.split(",");
					for (int i = 0; i < parts.length; i++) {
						// check each of them if it should be replaced
						String replacement = nameChanging.get(parts[i]);
						if (replacement != null) {
							Debug.info("        Replacing " + parts[i] + " with " + replacement);
							parts[i] = replacement;
						}
					}
					StringBuilder newConditionsList = new StringBuilder();
					for (String part : parts) {
						newConditionsList.append(part + ",");
					}
					String newInstruction = firstPart
							+ newConditionsList.toString().substring(0, newConditionsList.length() - 1) + lastPart;
					Debug.info("    New instruction is '" + newInstruction + "'");
					conditionsConfig.set(key, newInstruction);
				}
			}
			// save conditions so the changes persist
			conditionsAccessor.saveConfig();
			// now we have a map with names which need to be changed across all
			// configuration; for each conversation, for each NPC option and
			// player option, replace old names from the map with new names
			Debug.info("Starting conversation updating");
			// get every conversation accessor
			HashMap<String, ConfigAccessor> conversations = ch.getConversations();
			for (String conversationName : conversations.keySet()) {
				Debug.info("  Processing conversation " + conversationName);
				ConfigAccessor conversation = conversations.get(conversationName);
				// this list will store every path to condition list in this
				// conversation
				List<String> paths = new ArrayList<>();
				// for every npc option, check if it contains conditions
				// variable and add it to the list
				Debug.info("    Extracting conditions from NPC options");
				ConfigurationSection npcOptions = conversation.getConfig().getConfigurationSection("NPC_options");
				for (String npcPath : npcOptions.getKeys(false)) {
					String conditionPath = "NPC_options." + npcPath + ".conditions";
					if (conversation.getConfig().isSet(conditionPath)
							&& !conversation.getConfig().getString(conditionPath).equals("")) {
						Debug.info("      Adding " + conditionPath + " to the list");
						paths.add(conditionPath);
					}
				}
				// for every player option, check if it contains conditions
				// variable and add it to the list
				Debug.info("    Extracting conditions from player options");
				ConfigurationSection playerOptions = conversation.getConfig().getConfigurationSection("player_options");
				for (String playerPath : playerOptions.getKeys(false)) {
					String conditionPath = "player_options." + playerPath + ".conditions";
					if (conversation.getConfig().isSet(conditionPath)
							&& !conversation.getConfig().getString(conditionPath).equals("")) {
						Debug.info("      Adding " + conditionPath + " to the list");
						paths.add(conditionPath);
					}
				}
				// now we have a list of valid paths to condition variables
				// in this conversation
				for (String path : paths) {
					Debug.info("    Processing path " + path);
					// get the list of conditions (as a single string, separated
					// by commas)
					String list = conversation.getConfig().getString(path);
					Debug.info("      Original conditions list is: " + list);
					// split it into an array
					String[] conditionArr = list.split(",");
					for (int i = 0; i < conditionArr.length; i++) {
						// for every condition name in array check if it should
						// be replaced
						String replacement = nameChanging.get(conditionArr[i]);
						if (replacement != null) {
							// and replace it
							Debug.info("      Replacing " + conditionArr[i] + " with " + replacement);
							conditionArr[i] = replacement;
						}
					}
					// now when everything is replaced generate new list (as a
					// single string)
					StringBuilder newListBuilder = new StringBuilder();
					for (String condition : conditionArr) {
						newListBuilder.append(condition + ",");
					}
					String newList = newListBuilder.toString().substring(0, newListBuilder.length() - 1);
					Debug.info("      Saving new list: " + newList);
					// and set it
					conversation.getConfig().set(path, newList);
				}
				// save conversation so the changes persist
				conversation.saveConfig();
			}
			// now every conversation is processed, time for events
			// for each event_conditions: and conditions: in events.yml, replace
			// old names from the map with new names
			Debug.info("Starting events updating");
			ConfigAccessor eventsAccessor = ch.getConfigs().get("events");
			for (String eventName : eventsAccessor.getConfig().getKeys(false)) {
				Debug.info("  Processing event " + eventName);
				// extract event's instruction
				String instruction = eventsAccessor.getConfig().getString(eventName);
				// check if it contains event conditions
				if (instruction.contains(" event_conditions:")) {
					Debug.info("    Found event conditions!");
					// extract first half (to the start of condition list
					int index = instruction.indexOf(" event_conditions:") + 18;
					String firstHalf = instruction.substring(0, index);
					Debug.info("      First half is '" + firstHalf + "'");
					// extract condition list
					int secondIndex = index + instruction.substring(index).indexOf(" ");
					if (secondIndex <= index) {
						secondIndex = instruction.length();
					}
					String conditionList = instruction.substring(index, secondIndex);
					Debug.info("      Condition list is '" + conditionList + "'");
					// extract last half (from the end of condition list)
					String lastHalf = instruction.substring(secondIndex, instruction.length());
					Debug.info("      Last half is '" + lastHalf + "'");
					// split conditions into an array
					String[] parts = conditionList.split(",");
					for (int i = 0; i < parts.length; i++) {
						// check each of them if it should be replaced
						String replacement = nameChanging.get(parts[i]);
						if (replacement != null) {
							Debug.info("        Replacing " + parts[i] + " with " + replacement);
							parts[i] = replacement;
						}
					}
					// put it all together
					StringBuilder newListBuilder = new StringBuilder();
					for (String part : parts) {
						newListBuilder.append(part + ",");
					}
					String newList = newListBuilder.toString().substring(0, newListBuilder.length() - 1);
					Debug.info("      New condition list is '" + newList + "'");
					// put the event together and save it
					String newEvent = firstHalf + newList + lastHalf;
					Debug.info("      Saving instruction '" + newEvent + "'");
					eventsAccessor.getConfig().set(eventName, newEvent);
				}
				// read the instruction again, it could've changed
				instruction = eventsAccessor.getConfig().getString(eventName);
				// check if it containt objective conditions
				if (instruction.contains(" conditions:")) {
					Debug.info("    Found objective conditions!");
					// extract first half (to the start of condition list
					int index = instruction.indexOf(" conditions:") + 12;
					String firstHalf = instruction.substring(0, index);
					Debug.info("      First half is '" + firstHalf + "'");
					// extract condition list
					int secondIndex = index + instruction.substring(index).indexOf(" ");
					String conditionList = instruction.substring(index, secondIndex);
					Debug.info("      Condition list is '" + conditionList + "'");
					// extract last half (from the end of condition list)
					String lastHalf = instruction.substring(secondIndex, instruction.length());
					Debug.info("      Last half is '" + lastHalf + "'");
					// split conditions into an array
					String[] parts = conditionList.split(",");
					for (int i = 0; i < parts.length; i++) {
						// check each of them if it should be replaced
						String replacement = nameChanging.get(parts[i]);
						if (replacement != null) {
							Debug.info("        Replacing " + parts[i] + " with " + replacement);
							parts[i] = replacement;
						}
					}
					// put it all together
					StringBuilder newListBuilder = new StringBuilder();
					for (String part : parts) {
						newListBuilder.append(part + ",");
					}
					String newList = newListBuilder.toString().substring(0, newListBuilder.length() - 1);
					Debug.info("      New condition list is '" + newList + "'");
					// put the event together and save it
					String newEvent = firstHalf + newList + lastHalf;
					Debug.info("      Saving instruction '" + newEvent + "'");
					eventsAccessor.getConfig().set(eventName, newEvent);
				}
				// at this point we finished modifying this one event
			}
			// at this point we finished modifying every event, need to save
			// events
			eventsAccessor.saveConfig();
			// every place where conditions are is now updated, finished!
			Debug.broadcast("Converted inverted conditions to a new format using exclamation marks!");
			Debug.info("Converting took " + (new Date().getTime() - time) + "ms");
		} catch (Exception e) {
			// try-catch block is required - if there is some exception,
			// the version wouldn't get changed and updater would fall into
			// an infinite loop of endless exceptiorns
			e.printStackTrace();
			Debug.error(ERROR);
		}
		// set v3 version
		config.set("version", "v3");
		instance.saveConfig();
		// done
	}

	@SuppressWarnings("unused")
	private void update_from_v1() {
		config.set("debug", "false");
		Debug.broadcast("Added debug option to configuration!");
		config.set("version", "v2");
		instance.saveConfig();
	}

	private void updateTo1_6() {
		config.set("version", "v1");
		instance.saveConfig();
		performUpdate();
	}

	private void updateTo1_5_3() {
		// nothing to update
		config.set("version", "1.5.3");
		updateTo1_6();
	}

	private void updateTo1_5_2() {
		// nothing to update
		config.set("version", "1.5.2");
		updateTo1_5_3();
	}

	private void updateTo1_5_1() {
		// nothing to update
		config.set("version", "1.5.1");
		updateTo1_5_2();
	}

	private void updateTo1_5() {
		Debug.broadcast("Started converting configuration files from v1.4 to v1.5!");
		// add sound settings
		String[] array1 = new String[] { "start", "end", "journal", "update", "full" };
		for (String string : array1) {
			config.set("sounds." + string, config.getDefaults().getString("sounds." + string));
		}
		Debug.broadcast("Added new sound options!");
		// add colors for journal
		String[] array2 = new String[] { "date.day", "date.hour", "line", "text" };
		for (String string : array2) {
			config.set("journal_colors." + string, config.getDefaults().getString("journal_colors." + string));
		}
		Debug.broadcast("Added new journal color options!");
		// convert conditions in events to event_condition: format
		Debug.info("Starting updating 'conditions:' argument to 'event_conditions:' in events.yml");
		ConfigAccessor events = ch.getConfigs().get("events");
		for (String key : events.getConfig().getKeys(false)) {
			Debug.info("  Processing event " + key);
			if (events.getConfig().getString(key).contains("conditions:")) {
				StringBuilder parts = new StringBuilder();
				for (String part : events.getConfig().getString(key).split(" ")) {
					if (part.startsWith("conditions:")) {
						parts.append("event_conditions:" + part.substring(11) + " ");
					} else {
						parts.append(part + " ");
					}
				}
				Debug.info("    Found 'conditions:' option, replacing!");
				events.getConfig().set(key, parts.substring(0, parts.length() - 1));
			}
		}
		Debug.broadcast("Events now use 'event_conditions:' for conditioning.");
		// convert objectives to new format
		Debug.info("Converting objectives to new format...");
		ConfigAccessor objectives = ch.getConfigs().get("objectives");
		for (String key : events.getConfig().getKeys(false)) {
			Debug.info("  Processing objective " + key);
			if (events.getConfig().getString(key).split(" ")[0].equalsIgnoreCase("objective")) {
				events.getConfig().set(key, "objective "
						+ objectives.getConfig().getString(events.getConfig().getString(key).split(" ")[1]));
				Debug.info("      Event " + key + " converted!");
			}
		}
		Debug.broadcast("Objectives converted to new, event-powered format!");
		// convert global locations
		String globalLocations = config.getString("global_locations");
		if (globalLocations != null && !globalLocations.equals("")) {
			StringBuilder configGlobalLocs = new StringBuilder();
			Debug.broadcast("Converting global locations to use events...");
			int i = 0;
			for (String globalLoc : config.getString("global_locations").split(",")) {
				i++;
				events.getConfig().set("global_location_" + i,
						"objective " + objectives.getConfig().getString(globalLoc));
				configGlobalLocs.append("global_location_" + i + ",");
				Debug.broadcast("Converted " + globalLoc + " objective.");
			}
			config.set("global_locations", configGlobalLocs.substring(0, configGlobalLocs.length() - 1));
			Debug.broadcast("All " + i + " global locations have been converted.");
		}
		events.saveConfig();
		Debug.broadcast("Removing old file.");
		new File(instance.getDataFolder(), "objectives.yml").delete();
		// convert books to new format
		Debug.broadcast("Converting books to new format!");
		ConfigAccessor items = ch.getConfigs().get("items");
		for (String key : items.getConfig().getKeys(false)) {
			String string = items.getConfig().getString(key);
			if (string.split(" ")[0].equalsIgnoreCase("WRITTEN_BOOK")) {
				String text = null;
				LinkedList<String> parts = new LinkedList<String>(Arrays.asList(string.split(" ")));
				for (Iterator<String> iterator = parts.iterator(); iterator.hasNext();) {
					String part = (String) iterator.next();
					if (part.startsWith("text:")) {
						text = part.substring(5);
						iterator.remove();
						break;
					}
				}
				if (text != null) {
					StringBuilder pages = new StringBuilder();
					for (String page : Utils.pagesFromString(text.replace("_", " "))) {
						pages.append(page.replaceAll(" ", "_") + "|");
					}
					parts.add("text:" + pages.substring(0, pages.length() - 2));
					StringBuilder instruction = new StringBuilder();
					for (String part : parts) {
						instruction.append(part + " ");
					}
					items.getConfig().set(key, instruction.toString().trim().replaceAll("\\n", "\\\\n"));
					Debug.broadcast("Converted book " + key + ".");
				}
			}
		}
		items.saveConfig();
		Debug.broadcast("All books converted!");
		// JournalBook.pagesFromString(questItem.getText(), false);
		config.set("tellraw", "false");
		Debug.broadcast("Tellraw option added to config.yml!");
		config.set("autoupdate", "true");
		Debug.broadcast("AutoUpdater is now enabled by default! You can change this if you"
				+ " want and reload the plugin, nothing will be downloaded in that case.");
		// end of update
		config.set("version", "1.5");
		Debug.broadcast("Conversion to v1.5 finished.");
		updateTo1_5_1();
	}

	private void updateTo1_4_3() {
		// nothing to update
		config.set("version", "1.4.3");
		updateTo1_5();
	}

	private void updateTo1_4_2() {
		// nothing to update
		config.set("version", "1.4.2");
		updateTo1_4_3();
	}

	private void updateTo1_4_1() {
		// nothing to update
		config.set("version", "1.4.1");
		updateTo1_4_2();
	}

	private void updateTo1_4() {
		Debug.broadcast("Started converting configuration files from v1.3 to v1.4!");
		instance.getConfig().set("autoupdate", "false");
		Debug.broadcast("Added AutoUpdate option to config. It's DISABLED by default!");
		Debug.broadcast("Moving conversation to separate files...");
		ConfigAccessor convOld = ch.getConfigs().get("conversations");
		Set<String> keys = convOld.getConfig().getKeys(false);
		File folder = new File(instance.getDataFolder(), "conversations");
		if (folder.exists() && folder.isDirectory())
			for (File file : folder.listFiles()) {
				file.delete();
			}
		for (String convID : keys) {
			File convFile = new File(folder, convID + ".yml");
			Map<String, Object> convSection = convOld.getConfig().getConfigurationSection(convID).getValues(true);
			YamlConfiguration convNew = YamlConfiguration.loadConfiguration(convFile);
			for (String key : convSection.keySet()) {
				convNew.set(key, convSection.get(key));
			}
			try {
				convNew.save(convFile);
				Debug.broadcast("Conversation " + convID + " moved to it's own file!");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Debug.broadcast("All conversations moved, deleting old file.");
		new File(instance.getDataFolder(), "conversations.yml").delete();

		// updating items
		Debug.broadcast("Starting conversion of items...");
		// this map will contain all QuestItem objects extracted from
		// configs
		HashMap<String, QuestItem> items = new HashMap<>();
		// this is counter for a number in item names (in items.yml)
		int number = 0;
		// check every event
		for (String key : ch.getConfigs().get("events").getConfig().getKeys(false)) {
			String instructions = ch.getString("events." + key);
			String[] parts = instructions.split(" ");
			String type = parts[0];
			// if this event has items in it do the thing
			if (type.equals("give") || type.equals("take")) {
				// define all required variables
				String amount = "";
				String conditions = "";
				String material = null;
				int data = 0;
				Map<String, Integer> enchants = null;
				List<String> lore = null;
				String name = null;
				// for each part of the instruction string check if it
				// contains some data and if so pu it in variables
				for (String part : parts) {
					if (part.contains("type:")) {
						material = part.substring(5);
					} else if (part.contains("data:")) {
						data = Byte.valueOf(part.substring(5));
					} else if (part.contains("enchants:")) {
						enchants = new HashMap<>();
						for (String enchant : part.substring(9).split(",")) {
							enchants.put(enchant.split(":")[0], Integer.decode(enchant.split(":")[1]));
						}
					} else if (part.contains("lore:")) {
						lore = new ArrayList<>();
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
				// create an item
				String newItemID = null;
				@SuppressWarnings("deprecation")
				QuestItem item = new QuestItem(material, data, enchants, name, lore);
				boolean contains = false;
				for (String itemKey : items.keySet()) {
					if (items.get(itemKey).equals(item)) {
						contains = true;
						break;
					}
				}
				if (!contains) {
					// generate new name for an item
					newItemID = "item" + number;
					number++;
					items.put(newItemID, item);
				} else {
					for (String itemName : items.keySet()) {
						if (items.get(itemName).equals(item)) {
							newItemID = itemName;
						}
					}
				}
				ch.getConfigs().get("events").getConfig().set(key,
						(type + " " + newItemID + " " + amount + " " + conditions).trim());

				// replace event with updated version
				Debug.broadcast("Extracted " + newItemID + " from " + key + " event!");
			}
		}
		// check every condition (it's almost the same code, I didn't know how
		// to do
		// it better
		for (String key : ch.getConfigs().get("conditions").getConfig().getKeys(false)) {
			String instructions = ch.getString("conditions." + key);
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
				// create an item
				String newItemID = null;
				@SuppressWarnings("deprecation")
				QuestItem item = new QuestItem(material, data, enchants, name, lore);
				boolean contains = false;
				for (String itemKey : items.keySet()) {
					if (items.get(itemKey).equals(item)) {
						contains = true;
						break;
					}
				}
				if (!contains) {
					// generate new name for an item
					newItemID = "item" + number;
					number++;
					items.put(newItemID, item);
				} else {
					for (String itemName : items.keySet()) {
						if (items.get(itemName).equals(item)) {
							newItemID = itemName;
						}
					}
				}
				ch.getConfigs().get("conditions").getConfig().set(key,
						(type + " item:" + newItemID + " " + amount + " " + inverted).trim());
				Debug.broadcast("Extracted " + newItemID + " from " + key + " condition!");
			}
		}
		// generated all items, now place them in items.yml
		for (String key : items.keySet()) {
			QuestItem item = items.get(key);
			String instruction = item.getMaterial() + " data:" + item.getData();
			if (item.getName() != null) {
				instruction = instruction + " name:" + item.getName().replace(" ", "_");
			}
			if (item.getLore() != null && !item.getLore().isEmpty()) {
				StringBuilder lore = new StringBuilder();
				for (String line : item.getLore()) {
					lore.append(line + ";");
				}
				instruction = instruction + " lore:" + (lore.substring(0, lore.length() - 1).replace(" ", "_"));
			}
			if (item.getEnchants() != null && !item.getEnchants().isEmpty()) {
				StringBuilder enchants = new StringBuilder();
				for (Enchantment enchant : item.getEnchants().keySet()) {
					enchants.append(enchant.toString() + ":" + item.getEnchants().get(enchant) + ",");
				}
				instruction = instruction + " enchants:" + enchants.substring(0, enchants.length() - 1);
			}
			ch.getConfigs().get("items").getConfig().set(key, instruction);
		}
		ch.getConfigs().get("items").saveConfig();
		ch.getConfigs().get("events").saveConfig();
		ch.getConfigs().get("conditions").saveConfig();
		Debug.broadcast("All extracted items has been successfully saved to items.yml!");
		// end of updating to 1.4
		instance.getConfig().set("version", "1.4");
		Debug.broadcast("Conversion to v1.4 finished.");
		updateTo1_4_1();
	}

	private void updateTo1_3() {
		Debug.broadcast("Started converting configuration files from unknown version to v1.3!");
		// add conversion options
		Debug.broadcast("Using Names by for safety. If you run UUID compatible server and "
				+ "want to use UUID, change it manually in the config file and reload the plugin.");
		config.set("uuid", "false");
		// this will alert the plugin that the conversion should be done if UUID
		// is
		// set to true
		config.set("convert", "true");
		// add metrics if they are not set yet
		if (!config.isSet("metrics")) {
			Debug.broadcast("Added metrics option.");
			config.set("metrics", "true");
		}
		// add stop to conversation if not done already
		Debug.broadcast("Adding stop nodes to conversations...");
		int count = 0;
		ConfigAccessor conversations = ch.getConfigs().get("conversations");
		Set<String> convNodes = conversations.getConfig().getKeys(false);
		for (String convNode : convNodes) {
			if (!conversations.getConfig().isSet(convNode + ".stop")) {
				conversations.getConfig().set(convNode + ".stop", "false");
				count++;
			}
		}
		conversations.saveConfig();
		Debug.broadcast("Done, modified " + count + " conversations!");
		// end of updating to 1.3
		config.set("version", "1.3");
		Debug.broadcast("Conversion to v1.3 finished.");
		updateTo1_4();
	}

	/**
	 * Updates language file, so it contains all required messages.
	 */
	private void updateLanguages() {
		// add new languages
		boolean isUpdated = false;
		ConfigAccessor messages = Config.getMessages();
		// check every language if it exists
		for (String path : messages.getConfig().getDefaultSection().getKeys(false)) {
			if (messages.getConfig().isSet(path)) {
				// if it exists check every message if it exists
				for (String messageNode : messages.getConfig().getDefaults().getConfigurationSection(path)
						.getKeys(false)) {
					if (!messages.getConfig().isSet(path + "." + messageNode)) {
						// if message doesn't exist then add it from defaults
						messages.getConfig().set(path + "." + messageNode,
								messages.getConfig().getDefaults().get(path + "." + messageNode));
						isUpdated = true;
					}
				}
			} else {
				// if language does not exist then add every message to it
				for (String messageNode : messages.getConfig().getDefaults().getConfigurationSection(path)
						.getKeys(false)) {
					messages.getConfig().set(path + "." + messageNode,
							messages.getConfig().getDefaults().get(path + "." + messageNode));
					isUpdated = true;
				}
			}
		}
		// if we updated config filse then print the message
		if (isUpdated) {
			messages.saveConfig();
			Debug.broadcast("Updated language files!");
		}
	}

	/**
	 * As the name says, converts all names to UUID in database
	 */
	@SuppressWarnings("deprecation")
	private void convertNamesToUUID() {
		Debug.broadcast("Converting names to UUID...");
		// loop all tables
		HashMap<String, String> list = new HashMap<>();
		String[] tables = new String[] { "OBJECTIVES", "TAGS", "POINTS", "JOURNAL", "BACKPACK" };
		Connector con = new Connector();
		for (String table : tables) {
			ResultSet res = con.querySQL(QueryType.valueOf("SELECT_PLAYERS_" + table), new String[] {});
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
				con.updateSQL(UpdateType.valueOf("UPDATE_PLAYERS_" + table),
						new String[] { list.get(playerID), playerID });
			}
		}
		Debug.broadcast("Names conversion finished!");
	}

	/**
	 * Adds the changelog file.
	 */
	private void addChangelog() {
		try {
			File changelog = new File(BetonQuest.getInstance().getDataFolder(), "changelog.txt");
			if (changelog.exists()) {
				changelog.delete();
			}
			Files.copy(BetonQuest.getInstance().getResource("changelog.txt"), changelog.toPath());
			Debug.broadcast("Changelog added!");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private String convertObjective(String obj) {
		StringBuilder builder = new StringBuilder();
		for (String part : obj.split(" ")) {
			if (part.startsWith("tag:")) {
				builder.append("label:" + part.substring(4));
			} else {
				builder.append(part);
			}
			builder.append(' ');
		}
		return builder.toString().trim();
	}

	/**
	 * Deprecated config handler, used only for configuration updating process
	 * 
	 * @author Jakub Sapalski
	 */
	private class ConfigHandler {

		/**
		 * Map containing accessors for every conversation.
		 */
		private HashMap<String, ConfigAccessor> conversationsMap = new HashMap<>();
		/**
		 * Deprecated accessor for single conversations file, used only for
		 * updating configuration.
		 */
		private ConfigAccessor conversations;
		/**
		 * Deprecated accessor for objectives file, used only for updating
		 * configuration.
		 */
		private ConfigAccessor objectives;
		/**
		 * Accessor for conditions file.
		 */
		private ConfigAccessor conditions;
		/**
		 * Accessor for events file.
		 */
		private ConfigAccessor events;
		/**
		 * Accessor for messages file.
		 */
		private ConfigAccessor messages;
		/**
		 * Accessor for npcs file.
		 */
		private ConfigAccessor npcs;
		/**
		 * Accessor for journal file.
		 */
		private ConfigAccessor journal;
		/**
		 * Accessor for items file.
		 */
		private ConfigAccessor items;

		/**
		 * Legacy configuration handler, only used for updating purposes. Do not
		 * use!!!
		 */
		public ConfigHandler() {
			// put config accesors in fields
			conversations = new ConfigAccessor(new File(BetonQuest.getInstance().getDataFolder(), "conversations.yml"), "conversations.yml", AccessorType.CONVERSATION);
			objectives = new ConfigAccessor(new File(BetonQuest.getInstance().getDataFolder(), "objectives.yml"), "objectives.yml", AccessorType.OBJECTIVES);
			conditions = new ConfigAccessor(new File(BetonQuest.getInstance().getDataFolder(), "conditions.yml"), "conditions.yml", AccessorType.CONDITIONS);
			events = new ConfigAccessor(new File(BetonQuest.getInstance().getDataFolder(), "events.yml"), "events.yml", AccessorType.EVENTS);
			npcs = new ConfigAccessor(new File(BetonQuest.getInstance().getDataFolder(), "npcs.yml"), "npcs.yml", AccessorType.MAIN);
			journal = new ConfigAccessor(new File(BetonQuest.getInstance().getDataFolder(), "journal.yml"), "journal.yml", AccessorType.JOURNAL);
			items = new ConfigAccessor(new File(BetonQuest.getInstance().getDataFolder(), "items.yml"), "items.yml", AccessorType.ITEMS);
			messages = new ConfigAccessor(new File(BetonQuest.getInstance().getDataFolder(), "messages.yml"), "messages.yml", AccessorType.OTHER);
			if (new File(BetonQuest.getInstance().getDataFolder(), "conversations").exists()) {
				// put conversations accessors in the hashmap
				for (File file : new File(BetonQuest.getInstance().getDataFolder(), "conversations").listFiles()) {
					conversationsMap.put(file.getName().substring(0, file.getName().indexOf(".")),
							new ConfigAccessor(file, file.getName(), AccessorType.CONVERSATION));
				}
			}
		}

		/**
		 * Retireves from configuration the string at supplied path. The path
		 * should follow this syntax:
		 * "filename.branch.(moreBranches).branch.variable". For example getting
		 * color for day in journal date would be
		 * "config.journal_colors.date.day". Everything should be handled as a
		 * string for simplicity's sake.
		 *
		 * @param rawPath
		 *            path for the variable
		 * @return the String object representing requested variable
		 */
		public String getString(String rawPath) {

			// get parts of path
			String[] parts = rawPath.split("\\.");
			String first = parts[0];
			String path = rawPath.substring(first.length() + 1);
			String object;
			// for every possible file try to access the path and return String
			// object
			switch (first) {
			case "config":
				object = BetonQuest.getInstance().getConfig().getString(path);
				if (object == null) {
					// if object is null then there is no such variable at
					// specified path
					Debug.info("Error while accessing path: " + rawPath);
				}
				return object;
			case "conversations":
				object = null;
				// conversations should be handled with one more level, as they
				// are in
				// multiple files
				String conversationID = path.split("\\.")[0];
				String rest = path.substring(path.indexOf(".") + 1);
				if (conversationsMap.get(conversationID) != null) {
					object = conversationsMap.get(conversationID).getConfig().getString(rest);
				}
				if (object == null) {
					Debug.info("Error while accessing path: " + rawPath);
				}
				return object;
			case "objectives":
				object = objectives.getConfig().getString(path);
				if (object == null) {
					Debug.info("Error while accessing path: " + rawPath);
				}
				return object;
			case "conditions":
				object = conditions.getConfig().getString(path);
				if (object == null) {
					Debug.info("Error while accessing path: " + rawPath);
				}
				return object;
			case "events":
				object = events.getConfig().getString(path);
				if (object == null) {
					Debug.info("Error while accessing path: " + rawPath);
				}
				return object;
			case "messages":
				object = messages.getConfig().getString(path);
				if (object == null) {
					Debug.info("Error while accessing path: " + rawPath);
				}
				return object;
			case "npcs":
				object = npcs.getConfig().getString(path);
				return object;
			case "journal":
				object = journal.getConfig().getString(path);
				if (object == null) {
					Debug.info("Error while accessing path: " + rawPath);
				}
				return object;
			case "items":
				object = items.getConfig().getString(path);
				if (object == null) {
					Debug.info("Error while accessing path: " + rawPath);
				}
				return object;
			default:
				Debug.info("Fatal error while accessing path: " + rawPath + " (there is no such file)");
				return null;
			}
		}

		/**
		 * Retrieves a map containing all config accessors. Should be used for
		 * more advanced tasks than simply getting a String. Note that
		 * conversations are not included in this map. See
		 * {@link #getConversations() getConversations} method for that.
		 * Conversations accessor included in this map is just a deprecated old
		 * conversations file. The same situation is with unused objectives
		 * accessor.
		 *
		 * @return HashMap containing all config accessors
		 */
		public HashMap<String, ConfigAccessor> getConfigs() {
			HashMap<String, ConfigAccessor> map = new HashMap<>();
			map.put("conversations", conversations);
			map.put("conditions", conditions);
			map.put("events", events);
			map.put("objectives", objectives);
			map.put("journal", journal);
			map.put("messages", messages);
			map.put("npcs", npcs);
			map.put("items", items);
			return map;
		}

		/**
		 * Retrieves map containing all conversation accessors.
		 *
		 * @return HashMap containing conversation accessors
		 */
		public HashMap<String, ConfigAccessor> getConversations() {
			return conversationsMap;
		}
	}
}
