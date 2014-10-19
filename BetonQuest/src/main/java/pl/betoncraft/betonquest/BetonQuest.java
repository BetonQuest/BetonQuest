package pl.betoncraft.betonquest;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import pl.betoncraft.betonquest.conditions.TestCondition;
import pl.betoncraft.betonquest.core.Condition;
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
		registerConditions("test", TestCondition.class);
		
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
		conditions.put(name, (Class<? extends Condition>)conditionClass);
		Bukkit.getLogger().info("Condition " + name + " registered!");
	}
	
	/**
	 * returns Class object of condition with given name
	 * @param name
	 * @return
	 */
	public Class<? extends Condition> getCondition(String name) {
		return conditions.get(name);
	}
}
