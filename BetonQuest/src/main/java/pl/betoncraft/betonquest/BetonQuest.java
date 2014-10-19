package pl.betoncraft.betonquest;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import pl.betoncraft.betonquest.conditions.PermissionCondition;
import pl.betoncraft.betonquest.core.Condition;
import pl.betoncraft.betonquest.core.QuestEvent;
import pl.betoncraft.betonquest.events.MessageEvent;
import pl.betoncraft.betonquest.inout.ConfigInput;
import pl.betoncraft.betonquest.inout.TemporatyCommand;

/**
 * Represents BetonQuest plugin
 * @author Co0sh
 */
public final class BetonQuest extends JavaPlugin {

	private static BetonQuest instance;
	private MySQL MySQL;
	
	private HashMap<String,Class<? extends Condition>> conditions = new HashMap<String,Class<? extends Condition>>();
	private HashMap<String,Class<? extends QuestEvent>> events = new HashMap<String,Class<? extends QuestEvent>>();
	
	@Override
	public void onEnable() {
		
		instance = this;

		// try to connect to database
		this.MySQL = new MySQL(this, getConfig().getString("mysql.host"),
				getConfig().getString("mysql.port"), getConfig().getString(
						"mysql.base"), getConfig().getString("mysql.user"),
				getConfig().getString("mysql.pass"));
		
		new ConfigInput();
		
		this.getCommand("conv").setExecutor(new TemporatyCommand());
		
		// register our test condition
		registerConditions("permission", PermissionCondition.class);
		
		// register test event
		registerEvents("message", MessageEvent.class);
		
	}
	
	@Override
	public void onDisable() {
		
	}

	/**
	 * @return the plugin instance
	 */
	public static BetonQuest getInstance() {
		return instance;
	}

	/**
	 * @return the mySQL object
	 */
	public MySQL getMySQL() {
		return MySQL;
	}
	
	/**
	 * Registers new condition classes by their names
	 * @param name
	 * @param conditionClass
	 */
	public void registerConditions(String name, Class<? extends Condition> conditionClass) {
		conditions.put(name, conditionClass);
		Bukkit.getLogger().info("Condition " + name + " registered!");
	}
	
	public void registerEvents(String name, Class<? extends QuestEvent> eventClass) {
		events.put(name, eventClass);
		Bukkit.getLogger().info("Event " + name + " registered!");
	}
	
	/**
	 * returns Class object of condition with given name
	 * @param name
	 * @return
	 */
	public Class<? extends Condition> getCondition(String name) {
		return conditions.get(name);
	}
	
	public Class<? extends QuestEvent> getEvent(String name) {
		return events.get(name);
	}
}
