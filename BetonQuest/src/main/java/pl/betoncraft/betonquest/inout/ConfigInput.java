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
		switch (first) {
		case "config":
			return BetonQuest.getInstance().getConfig().get(path);
		case "conversations":
			return conversations.getConfig().get(path);
		case "objectives":
			return objectives.getConfig().get(path);
		case "conditions":
			return conditions.getConfig().get(path);
		case "events":
			return events.getConfig().get(path);
		case "messages":
			return messages.getConfig().get(path);
		case "npcs":
			return npcs.getConfig().get(path);
		case "journal":
			return journal.getConfig().get(path);
		default:
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
}