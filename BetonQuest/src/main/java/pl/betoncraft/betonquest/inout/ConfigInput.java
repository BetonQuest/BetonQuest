/**
 * 
 */
package pl.betoncraft.betonquest.inout;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.ConfigAccessor;

/**
 * 
 * @author Co0sh
 */
public class ConfigInput {
	
	private static ConfigInput instance;
	
	private ConfigAccessor conversations;
	private ConfigAccessor objectives;
	private ConfigAccessor conditions;
	private ConfigAccessor events;
	private ConfigAccessor messages;
	private ConfigAccessor npcs;
	private ConfigAccessor journal;
	
	public ConfigInput() {
		instance = this;
		// put config accesors in fields
		conversations = new ConfigAccessor(BetonQuest.getInstance(), "conversations.yml");
		objectives = new ConfigAccessor(BetonQuest.getInstance(), "objectives.yml");
		conditions = new ConfigAccessor(BetonQuest.getInstance(), "conditions.yml");
		events = new ConfigAccessor(BetonQuest.getInstance(), "events.yml");
		messages = new ConfigAccessor(BetonQuest.getInstance(), "messages.yml");
		npcs = new ConfigAccessor(BetonQuest.getInstance(), "npcs.yml");
		journal = new ConfigAccessor(BetonQuest.getInstance(), "journal.yml");
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
}