/**
 * 
 */
package pl.betoncraft.betonquest.inout;

import java.io.File;
import java.util.HashMap;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.database.ConfigAccessor;

/**
 * 
 * @author Co0sh
 */
public class ConfigInput {
	
	private static ConfigInput instance;
	
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
		// put conversations accessors in the hashmap
		for (File file : new File(BetonQuest.getInstance().getDataFolder(), "conversations").listFiles()) {
			conversationsMap.put(file.getName(), new ConfigAccessor(BetonQuest.getInstance(), file, file.getName()));
		}
		// put config accesors in fields
		conversations = new ConfigAccessor(BetonQuest.getInstance(), new File(BetonQuest.getInstance().getDataFolder(), "conversations.yml"), "conversations.yml");
		objectives = new ConfigAccessor(BetonQuest.getInstance(), new File(BetonQuest.getInstance().getDataFolder(), "objectives.yml"), "objectives.yml");
		conditions = new ConfigAccessor(BetonQuest.getInstance(), new File(BetonQuest.getInstance().getDataFolder(), "conditions.yml"), "conditions.yml");
		events = new ConfigAccessor(BetonQuest.getInstance(), new File(BetonQuest.getInstance().getDataFolder(), "events.yml"), "events.yml");
		messages = new ConfigAccessor(BetonQuest.getInstance(), new File(BetonQuest.getInstance().getDataFolder(), "messages.yml"), "messages.yml");
		npcs = new ConfigAccessor(BetonQuest.getInstance(), new File(BetonQuest.getInstance().getDataFolder(), "npcs.yml"), "npcs.yml");
		journal = new ConfigAccessor(BetonQuest.getInstance(), new File(BetonQuest.getInstance().getDataFolder(), "journal.yml"), "journal.yml");
		// save config if there isn't one
		BetonQuest.getInstance().saveDefaultConfig();
		conversations.saveDefaultConfig();
		objectives.saveDefaultConfig();
		conditions.saveDefaultConfig();
		events.saveDefaultConfig();
		messages.saveDefaultConfig();
		npcs.saveDefaultConfig();
		journal.saveDefaultConfig();
	}
	
	private Object getObject(String rawPath) {
		String[] parts = rawPath.split("\\.");
		String first = parts[0];
		String path = rawPath.substring(first.length() + 1);
		Object object;
		switch (first) {
		case "config":
			object = BetonQuest.getInstance().getConfig().get(path);
			if (object == null) {
				BetonQuest.getInstance().getLogger().severe("Error while accessing path: " + rawPath);
			}
			return object;
		case "conversations":
			object = conversations.getConfig().get(path);
			if (object == null) {
				BetonQuest.getInstance().getLogger().severe("Error while accessing path: " + rawPath);
			}
			return object;
		case "objectives":
			object = objectives.getConfig().get(path);
			if (object == null) {
				BetonQuest.getInstance().getLogger().severe("Error while accessing path: " + rawPath);
			}
			return object;
		case "conditions":
			object = conditions.getConfig().get(path);
			if (object == null) {
				BetonQuest.getInstance().getLogger().severe("Error while accessing path: " + rawPath);
			}
			return object;
		case "events":
			object = events.getConfig().get(path);
			if (object == null) {
				BetonQuest.getInstance().getLogger().severe("Error while accessing path: " + rawPath);
			}
			return object;
		case "messages":
			object = messages.getConfig().get(path);
			if (object == null) {
				BetonQuest.getInstance().getLogger().severe("Error while accessing path: " + rawPath);
			}
			return object;
		case "npcs":
			object = npcs.getConfig().get(path);
			return object;
		case "journal":
			object = journal.getConfig().get(path);
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
	 * Returns string from configuration file where path looks like "(nameOfFile).path.to.string"
	 * @param path
	 * @return
	 */
	public static String getString(String path) {
		try {
			String string = (String) instance.getObject(path);
			return string;
		} catch (ClassCastException e) {
			return null;
		}
	}
	
	/**
	 * reloads all config files
	 */
	public static void reload() {
		instance.conversations.reloadConfig();
		instance.conditions.reloadConfig();
		instance.events.reloadConfig();
		instance.journal.reloadConfig();
		instance.messages.reloadConfig();
		instance.npcs.reloadConfig();
		instance.objectives.reloadConfig();
		BetonQuest.getInstance().reloadConfig();
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
}