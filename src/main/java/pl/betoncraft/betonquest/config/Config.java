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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.database.PlayerData;
import pl.betoncraft.betonquest.utils.Debug;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Handles the configuration of the plugin
 * 
 * @author Jakub Sapalski
 */
public class Config {

	private static BetonQuest plugin;

	private static Config instance;

	private static ConfigAccessor messages;
	private static ConfigAccessor internal;

	private static HashMap<String, ConfigPackage> packages = new HashMap<>();
	private static HashMap<String, QuestCanceler> cancelers = new HashMap<>();

	private static String lang;

	private static ArrayList<String> languages;

	private File root;

	public Config() {
		this(true);
	}

	/**
	 * Creates new instance of the Config handler
	 * 
	 * @param verboose
	 *            controls if this object should log it's actions to the file
	 */
	public Config(boolean verboose) {

		instance = this;
		plugin = BetonQuest.getInstance();
		root = plugin.getDataFolder();
		lang = plugin.getConfig().getString("language");

		// save default config
		plugin.saveDefaultConfig();
		// need to be sure everything is saved
		plugin.reloadConfig();
		plugin.saveConfig();

		// if the packages are not on the config list yet, add them
		List<String> packageList = plugin.getConfig().getStringList("packages");
		if (plugin.getConfig().getStringList("packages") == null || packageList.isEmpty()) {
			ArrayList<String> allPackages = new ArrayList<>();
			for (File file : root.listFiles()) {
				if (file.isDirectory() && !file.getName().equals("logs") && !file.getName().equals("backups")
						&& !file.getName().equals("conversations")) {
					allPackages.add(file.getName());
				}
			}
			plugin.getConfig().set("packages", allPackages);
			plugin.saveConfig();
		}

		// load messages
		messages = new ConfigAccessor(plugin, new File(root, "messages.yml"), "messages.yml");
		messages.saveDefaultConfig();
		internal = new ConfigAccessor(plugin, null, "internal-messages.yml");
		languages = new ArrayList<>();
		for (String key : messages.getConfig().getKeys(false)) {
			if (!key.equals("global")) {
				if (verboose)
					Debug.info("Loaded " + key + " language");
				languages.add(key);
			}
		}

		// save example package
		createPackage("default");

		// load packages
		for (String packPath : plugin.getConfig().getStringList("packages")) {
			File file = new File(root, packPath.replace("-", File.separator));
			// get directories which can be quest packages
			if (!file.isDirectory())
				continue;
			if (file.getName().equals("logs") || file.getName().equals("backups")
					|| file.getName().equals("conversations"))
				continue;
			// initialize ConfigPackage objects and if they are valid place them
			// in the map
			ConfigPackage pack = new ConfigPackage(file, packPath);
			if (pack.isValid()) {
				packages.put(packPath, pack);
				if (verboose)
					Debug.info("Loaded " + packPath + " package");
			}
		}

		// load quest cancelers
		for (ConfigPackage pack : packages.values()) {
			ConfigurationSection s = pack.getMain().getConfig().getConfigurationSection("cancel");
			if (s == null)
				continue;
			for (String key : s.getKeys(false)) {
				String name = pack.getName() + "." + key;
				try {
					cancelers.put(name, new QuestCanceler(name));
				} catch (InstructionParseException e) {
					Debug.error("Could not load '" + name + "' quest canceler: " + e.getMessage());
				}
			}
		}
	}

	/**
	 * Creates package with the given name and populates it with default quest
	 * 
	 * @param packName
	 *            name of the new package
	 * @return true if the package was created, false if it already existed
	 */
	public static boolean createPackage(String packName) {
		File def = new File(instance.root, packName.replace("-", File.separator));
		if (!def.exists()) {
			Debug.broadcast("Deploying " + packName + " package!");
			def.mkdirs();
			saveResource(def, "main.yml");
			saveResource(def, "events.yml");
			saveResource(def, "conditions.yml");
			saveResource(def, "journal.yml");
			saveResource(def, "items.yml");
			saveResource(def, "objectives.yml");
			File conversations = new File(def, "conversations");
			conversations.mkdir();
			saveResource(conversations, "defaultConversation.yml", "innkeeper.yml");
			List<String> list = plugin.getConfig().getStringList("packages");
			if (list == null)
				list = new ArrayList<>();
			list.add(packName);
			plugin.getConfig().set("packages", list);
			plugin.saveConfig();
			return true;
		}
		return false;
	}

	/**
	 * Saves resource in a root directory
	 * 
	 * @param root
	 *            directory where the resource will be saved
	 * @param resource
	 *            resource name, also name of the file
	 */
	private static void saveResource(File root, String resource) {
		saveResource(root, resource, resource);
	}

	/**
	 * Saves the resource with the name in a root directory
	 * 
	 * @param root
	 *            directory where the resource will be saved
	 * @param resource
	 *            resource name
	 * @param name
	 *            file name
	 */
	private static void saveResource(File root, String resource, String name) {
		if (!root.isDirectory())
			return;
		File file = new File(root, name);
		if (!file.exists()) {
			try {
				file.createNewFile();
				InputStream in = plugin.getResource(resource);
				OutputStream out = new FileOutputStream(file);
				byte[] buffer = new byte[1024];
				int len = in.read(buffer);
				while (len != -1) {
					out.write(buffer, 0, len);
					len = in.read(buffer);
				}
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @return the current instance of the Config handler
	 */
	public static Config getInstance() {
		return instance;
	}

	/**
	 * Returns a quest canceler object, ready to cancel players' quests.
	 * 
	 * @param name
	 *            name of the object (package.name)
	 * @return the QuestCanceler object with given name
	 */
	public static HashMap<String, QuestCanceler> getCancelers() {
		return cancelers;
	}

	/**
	 * Retrieves the message from the configuration in specified language and
	 * replaces the variables
	 * 
	 * @param lang
	 *            language in which the message should be retrieved
	 * @param message
	 *            name of the message to retrieve
	 * @param variables
	 *            array of variables to replace
	 * @return message in that language, or message in English, or null if it
	 *         does not exist
	 */
	public static String getMessage(String lang, String message, String[] variables) {
		String result = messages.getConfig().getString(lang + "." + message);
		if (result == null) {
			result = messages.getConfig().getString(Config.getLanguage() + "." + message);
		}
		if (result == null) {
			result = messages.getConfig().getString("en." + message);
		}
		if (result == null) {
			result = internal.getConfig().getString(lang + "." + message);
		}
		if (result == null) {
			result = internal.getConfig().getString("en." + message);
		}
		if (result != null) {
			if (variables != null)
				for (int i = 0; i < variables.length; i++) {
					result = result.replace("{" + (i + 1) + "}", variables[i]);
				}
			result = result.replace('&', '§');
		}
		return result;
	}

	/**
	 * Retrieves the message from the configuration in specified language
	 * 
	 * @param message
	 *            name of the message to retrieve
	 * @param lang
	 *            language in which the message should be retrieved
	 * @return message in that language, or message in English, or null if it
	 *         does not exist
	 */
	public static String getMessage(String lang, String message) {
		return getMessage(lang, message, null);
	}

	/**
	 * Retrieves the ConfigPackage object for specified package
	 * 
	 * @param name
	 *            name of the package which needs to be retrieved
	 * @return the ConfigPackage object representing this package
	 */
	public static ConfigPackage getPackage(String name) {
		return packages.get(name);
	}

	/**
	 * @return the set of names of valid packages
	 */
	public static Set<String> getPackageNames() {
		return packages.keySet();
	}

	/**
	 * Retrieves the string from across all configuration. The variables are not
	 * replaced! To replace variables automatically just call getString() method
	 * on ConfigPackage.
	 * 
	 * @param address
	 *            address of the string
	 * @return the requested string
	 */
	public static String getString(String address) {
		if (address == null)
			return null;
		String[] parts = address.split("\\.");
		if (parts.length < 2)
			return null;
		String main = parts[0];
		if (main.equals("config")) {
			return plugin.getConfig().getString(address.substring(7));
		} else if (main.equals("messages")) {
			return messages.getConfig().getString(address.substring(9));
		} else {
			ConfigPackage pack = packages.get(main);
			if (pack == null)
				return null;
			return pack.getRawString(address.substring(main.length() + 1));
		}
	}

	/**
	 * Sets the string at specified address
	 * 
	 * @param address
	 *            address of the variable
	 * @param value
	 *            value that needs to be set
	 * @return true if it was set, false otherwise
	 */
	public static boolean setString(String address, String value) {
		if (address == null)
			return false;
		;
		String[] parts = address.split("\\.");
		if (parts.length < 2)
			return false;
		String main = parts[0];
		if (main.equals("config")) {
			plugin.getConfig().set(address.substring(7), value);
			plugin.saveConfig();
			return true;
		} else if (main.equals("messages")) {
			messages.getConfig().set(address.substring(9), value);
			messages.saveConfig();
			return true;
		} else {
			ConfigPackage pack = packages.get(main);
			if (pack == null)
				return false;
			return pack.setString(address.substring(main.length() + 1), value);
		}
	}

	/**
	 * @return messages configuration
	 */
	public static ConfigAccessor getMessages() {
		return messages;
	}

	/**
	 * @return the default language
	 */
	public static String getLanguage() {
		return lang;
	}

	/**
	 * @return the ID of the conversation assigned to this NPC or null if there
	 *         isn't one
	 */
	public static String getNpc(String value) {
		// load npc assignments from all packages
		for (String packName : packages.keySet()) {
			ConfigPackage pack = packages.get(packName);
			ConfigurationSection assignemnts = pack.getMain().getConfig().getConfigurationSection("npcs");
			for (String assignment : assignemnts.getKeys(false)) {
				if (assignment.equalsIgnoreCase(value)) {
					return packName + "." + assignemnts.getString(assignment);
				}
			}
		}
		return null;
	}

	/**
	 * Sends a message to player in his chosen language or default or English
	 * (if previous not found).
	 * 
	 * @param playerID
	 *            ID of the player
	 * @param messageName
	 *            ID of the message
	 */
	public static void sendMessage(String playerID, String messageName) {
		sendMessage(playerID, messageName, null, null, null, null);
	}

	/**
	 * Sends a message to player in his chosen language or default or English
	 * (if previous not found). It will replace all {x} sequences with the
	 * variables.
	 * 
	 * @param playerID
	 *            ID of the player
	 * @param messageName
	 *            ID of the message
	 * @param variables
	 *            array of variables which will be inserted into the string
	 */
	public static void sendMessage(String playerID, String messageName, String[] variables) {
		sendMessage(playerID, messageName, variables, null, null, null);
	}

	/**
	 * Sends a message to player in his chosen language or default or English
	 * (if previous not found). It will replace all {x} sequences with the
	 * variables and play the sound.
	 * 
	 * @param playerID
	 *            ID of the player
	 * @param messageName
	 *            ID of the message
	 * @param variables
	 *            array of variables which will be inserted into the string
	 * @param soundName
	 *            name of the sound to play to the player
	 */
	public static void sendMessage(String playerID, String messageName, String[] variables, String soundName) {
		sendMessage(playerID, messageName, variables, soundName, null, null);
	}

	/**
	 * Sends a message to player in his chosen language or default or English
	 * (if previous not found). It will replace all {x} sequences with the
	 * variables and play the sound. It will also add a prefix to the message.
	 * 
	 * @param playerID
	 *            ID of the player
	 * @param messageName
	 *            ID of the message
	 * @param variables
	 *            array of variables which will be inserted into the message
	 * @param soundName
	 *            name of the sound to play to the player
	 * @param prefixName
	 *            ID of the prefix
	 * @param prefixVariables
	 *            array of variables which will be inserted into the prefix
	 */
	public static void sendMessage(String playerID, String messageName, String[] variables, String soundName,
			String prefixName, String[] prefixVariables) {
		Player player = PlayerConverter.getPlayer(playerID);
		PlayerData playerData = BetonQuest.getInstance().getPlayerData(playerID);
		if (player == null || playerData == null)
			return;
		String language = playerData.getLanguage();
		String message = getMessage(language, messageName, variables);
		if (message == null || message.length() == 0)
			return;
		if (prefixName != null) {
			String prefix = getMessage(language, prefixName, prefixVariables);
			if (prefix.length() > 0) {
				message = prefix + message;
			}
		}
		player.sendMessage(message);
		if (soundName != null) {
			String rawSound = BetonQuest.getInstance().getConfig().getString("sounds." + soundName);
			if (!rawSound.equalsIgnoreCase("false")) {
				try {
					player.playSound(player.getLocation(), Sound.valueOf(rawSound), 1F, 1F);
				} catch (IllegalArgumentException e) {
					Debug.error("Unknown sound type: " + rawSound);
				}
			}
		}
	}

	/**
	 * @return the languages defined for this plugin
	 */
	public static ArrayList<String> getLanguages() {
		return languages;
	}
	
	public static String getDefaultPackage() {
		return plugin.getConfig().getString("default_package");
	}
}
