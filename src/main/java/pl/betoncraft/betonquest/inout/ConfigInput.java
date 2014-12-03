/**
 * 
 */
package pl.betoncraft.betonquest.inout;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.database.ConfigAccessor;

/**
 * 
 * @author Co0sh
 */
public class ConfigInput {
	
	private static ConfigInput instance;
	
	private File folder;
	private HashMap<String,ConfigAccessor> conversationsMap = new HashMap<>();
	private ConfigAccessor conversations;
	private ConfigAccessor objectives;
	private ConfigAccessor conditions;
	private ConfigAccessor events;
	private ConfigAccessor messages;
	private ConfigAccessor npcs;
	private ConfigAccessor journal;
	
	public ConfigInput() {
		instance = this;
		// save default config if there isn't one and by the way create plugin's directory
		BetonQuest.getInstance().saveDefaultConfig();
		// conversations needs to be created first
		conversations = new ConfigAccessor(BetonQuest.getInstance(), new File(BetonQuest.getInstance().getDataFolder(), "conversations.yml"), "conversations.yml");
		// create conversations folder if there isn't one
		folder = new File(BetonQuest.getInstance().getDataFolder(), "conversations");
		if (!folder.isDirectory()) {
			folder.mkdirs();
		}
		if (folder.listFiles().length == 0) {
			File defaultConversation = new File(folder, "innkeeper.yml");
	        try {
				Files.copy(BetonQuest.getInstance().getResource("defaultConversation.yml"), defaultConversation.toPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// put conversations accessors in the hashmap
		for (File file : folder.listFiles()) {
			conversationsMap.put(file.getName().substring(0, file.getName().indexOf(".")), new ConfigAccessor(BetonQuest.getInstance(), file, file.getName()));
		}
		// put config accesors in fields
		objectives = new ConfigAccessor(BetonQuest.getInstance(), new File(BetonQuest.getInstance().getDataFolder(), "objectives.yml"), "objectives.yml");
		conditions = new ConfigAccessor(BetonQuest.getInstance(), new File(BetonQuest.getInstance().getDataFolder(), "conditions.yml"), "conditions.yml");
		events = new ConfigAccessor(BetonQuest.getInstance(), new File(BetonQuest.getInstance().getDataFolder(), "events.yml"), "events.yml");
		messages = new ConfigAccessor(BetonQuest.getInstance(), new File(BetonQuest.getInstance().getDataFolder(), "messages.yml"), "messages.yml");
		npcs = new ConfigAccessor(BetonQuest.getInstance(), new File(BetonQuest.getInstance().getDataFolder(), "npcs.yml"), "npcs.yml");
		journal = new ConfigAccessor(BetonQuest.getInstance(), new File(BetonQuest.getInstance().getDataFolder(), "journal.yml"), "journal.yml");
		// save config if there isn't one
		objectives.saveDefaultConfig();
		conditions.saveDefaultConfig();
		events.saveDefaultConfig();
		messages.saveDefaultConfig();
		npcs.saveDefaultConfig();
		journal.saveDefaultConfig();
	}
	
	public static String getString(String rawPath) {
		String[] parts = rawPath.split("\\.");
		String first = parts[0];
		String path = rawPath.substring(first.length() + 1);
		String object;
		switch (first) {
		case "config":
			object = BetonQuest.getInstance().getConfig().getString(path);
			if (object == null) {
				BetonQuest.getInstance().getLogger().severe("Error while accessing path: " + rawPath);
			}
			return object;
		case "conversations":
			object = null;
			String conversationID = path.split("\\.")[0];
			String rest = path.substring(path.indexOf(".") + 1);
			if (instance.conversationsMap.get(conversationID) != null) {
				object = instance.conversationsMap.get(conversationID).getConfig().getString(rest);
			}
			if (object == null) {
				BetonQuest.getInstance().getLogger().severe("Error while accessing path: " + rawPath);
			}
			return object;
		case "objectives":
			object = instance.objectives.getConfig().getString(path);
			if (object == null) {
				BetonQuest.getInstance().getLogger().severe("Error while accessing path: " + rawPath);
			}
			return object;
		case "conditions":
			object = instance.conditions.getConfig().getString(path);
			if (object == null) {
				BetonQuest.getInstance().getLogger().severe("Error while accessing path: " + rawPath);
			}
			return object;
		case "events":
			object = instance.events.getConfig().getString(path);
			if (object == null) {
				BetonQuest.getInstance().getLogger().severe("Error while accessing path: " + rawPath);
			}
			return object;
		case "messages":
			object = instance.messages.getConfig().getString(path);
			if (object == null) {
				BetonQuest.getInstance().getLogger().severe("Error while accessing path: " + rawPath);
			}
			return object;
		case "npcs":
			object = instance.npcs.getConfig().getString(path);
			return object;
		case "journal":
			object = instance.journal.getConfig().getString(path);
			if (object == null) {
				BetonQuest.getInstance().getLogger().severe("Error while accessing path: " + rawPath);
			}
			return object;
		default:
			BetonQuest.getInstance().getLogger().severe("Error while accessing path: " + rawPath);
			return null;
		}
	}
	
	/**
	 * reloads all config files
	 */
	public static void reload() {
		BetonQuest.getInstance().reloadConfig();
		// put conversations accessors in the hashmap
		instance.conversationsMap.clear();
		for (File file : instance.folder.listFiles()) {
			instance.conversationsMap.put(file.getName(), new ConfigAccessor(BetonQuest.getInstance(), file, file.getName()));
		}
		instance.conditions.reloadConfig();
		instance.events.reloadConfig();
		instance.journal.reloadConfig();
		instance.messages.reloadConfig();
		instance.npcs.reloadConfig();
		instance.objectives.reloadConfig();
	}
	
	public static HashMap<String,ConfigAccessor> getConfigs() {
		HashMap<String,ConfigAccessor> map = new HashMap<>();
		map.put("conversations", instance.conversations);
		map.put("conditions", instance.conditions);
		map.put("events", instance.events);
		map.put("objectives", instance.objectives);
		map.put("journal", instance.journal);
		map.put("messages", instance.messages);
		map.put("npcs", instance.npcs);
		return map;
	}
	
	public static HashMap<String,ConfigAccessor> getConversations() {
		return instance.conversationsMap;
	}
}