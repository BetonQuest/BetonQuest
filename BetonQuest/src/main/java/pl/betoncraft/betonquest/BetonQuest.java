package pl.betoncraft.betonquest;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import pl.betoncraft.betonquest.conditions.ExperienceCondition;
import pl.betoncraft.betonquest.conditions.PermissionCondition;
import pl.betoncraft.betonquest.conditions.TagCondition;
import pl.betoncraft.betonquest.core.Condition;
import pl.betoncraft.betonquest.core.Journal;
import pl.betoncraft.betonquest.core.Objective;
import pl.betoncraft.betonquest.core.Pointer;
import pl.betoncraft.betonquest.core.QuestEvent;
import pl.betoncraft.betonquest.events.CommandEvent;
import pl.betoncraft.betonquest.events.JournalEvent;
import pl.betoncraft.betonquest.events.MessageEvent;
import pl.betoncraft.betonquest.events.ObjectiveEvent;
import pl.betoncraft.betonquest.events.TagEvent;
import pl.betoncraft.betonquest.inout.ConfigInput;
import pl.betoncraft.betonquest.inout.JoinQuitListener;
import pl.betoncraft.betonquest.inout.JournalTestCommand;
import pl.betoncraft.betonquest.inout.NPCListener;
import pl.betoncraft.betonquest.inout.ObjectiveSaving;
import pl.betoncraft.betonquest.objectives.LocationObjective;

/**
 * Represents BetonQuest plugin
 * @author Co0sh
 */
public final class BetonQuest extends JavaPlugin {

	private static BetonQuest instance;
	private MySQL MySQL;
	
	private HashMap<String,Class<? extends Condition>> conditions = new HashMap<String,Class<? extends Condition>>();
	private HashMap<String,Class<? extends QuestEvent>> events = new HashMap<String,Class<? extends QuestEvent>>();
	private HashMap<String,Class<? extends Objective>> objectives = new HashMap<String,Class<? extends Objective>>();
	
	private HashMap<String,List<String>> playerStrings = new HashMap<String,List<String>>();
	
	private HashMap<String,Journal> journals = new HashMap<String,Journal>();
	
	private List<ObjectiveSaving> saving = new ArrayList<ObjectiveSaving>();
	
	@Override
	public void onEnable() {
		
		instance = this;

		new ConfigInput();
		
		// try to connect to database
		this.MySQL = new MySQL(this, getConfig().getString("mysql.host"),
				getConfig().getString("mysql.port"), getConfig().getString(
						"mysql.base"), getConfig().getString("mysql.user"),
				getConfig().getString("mysql.pass"));
			
		// create tables if they don't exist
		if (MySQL.openConnection() != null) {
			MySQL.updateSQL("CREATE TABLE IF NOT EXISTS objectives (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, playerID VARCHAR(256) NOT NULL, instructions VARCHAR(2048) NOT NULL, isused BOOLEAN NOT NULL DEFAULT 0);");
			MySQL.updateSQL("CREATE TABLE IF NOT EXISTS strings (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, playerID VARCHAR(256) NOT NULL, string TEXT NOT NULL, isused BOOLEAN NOT NULL DEFAULT 0);");
			MySQL.updateSQL("CREATE TABLE IF NOT EXISTS journal (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, playerID VARCHAR(256) NOT NULL, pointer VARCHAR(256) NOT NULL, date TIMESTAMP NOT NULL);");
			MySQL.closeConnection();
		} else {
			BetonQuest.getInstance().getLogger().info("Couldn't connect to database, fix this and restart the server!");
			Bukkit.getPluginManager().disablePlugin(this);
		}
		
		new JoinQuitListener();
		new NPCListener();
		
		getCommand("journal").setExecutor(new JournalTestCommand());
		
		// register conditions
		registerConditions("permission", PermissionCondition.class);
		registerConditions("experience", ExperienceCondition.class);
		registerConditions("tag", TagCondition.class);
		
		// register test events
		registerEvents("message", MessageEvent.class);
		registerEvents("objective", ObjectiveEvent.class);
		registerEvents("command", CommandEvent.class);
		registerEvents("tag", TagEvent.class);
		registerEvents("journal", JournalEvent.class);
		
		// register test objective
		registerObjectives("location", LocationObjective.class);
		
		// load objectives for all online players (in case of reload)
		for (Player player : Bukkit.getOnlinePlayers()) {
			loadObjectives(player.getName());
			loadPlayerStrings(player.getName());
			loadJournal(player.getName());
		}

		getLogger().log(Level.INFO, "BetonQuest succesfully enabled!");
	}
	
	@Override
	public void onDisable() {
		// create array and put there objectives (to avoid concurrent modification exception)
		List<ObjectiveSaving> list = new ArrayList<ObjectiveSaving>();
		// save all active objectives to database
		for (ObjectiveSaving objective : saving) {
			list.add(objective);
		}
		for (ObjectiveSaving objective : list) {
			objective.saveObjective();
		}
		for (Player player : Bukkit.getOnlinePlayers()) {
			saveJournal(player.getName());
		}
		BetonQuest.getInstance().getMySQL().openConnection();
		for (Player player : Bukkit.getOnlinePlayers()) {
			BetonQuest.getInstance().getMySQL().updateSQL("DELETE FROM objectives WHERE playerID='" + player.getName() + "' AND isused = 1;");
		}
		BetonQuest.getInstance().getMySQL().closeConnection();
		getLogger().log(Level.INFO, "BetonQuest succesfully disabled!");
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
	
	/**
	 * Registers new event classes by their names
	 * @param name
	 * @param eventClass
	 */
	public void registerEvents(String name, Class<? extends QuestEvent> eventClass) {
		events.put(name, eventClass);
		Bukkit.getLogger().info("Event " + name + " registered!");
	}
	
	/**
	 * Registers new objective classes by their names
	 * @param name
	 * @param objectiveClass
	 */
	public void registerObjectives(String name, Class<? extends Objective> objectiveClass) {
		objectives.put(name, objectiveClass);
		Bukkit.getLogger().info("Objective " + name + " registered!");
	}
	
	/**
	 * returns Class object of condition with given name
	 * @param name
	 * @return
	 */
	public Class<? extends Condition> getCondition(String name) {
		return conditions.get(name);
	}
	
	/**
	 * returns Class object of event with given name
	 * @param name
	 * @return
	 */
	public Class<? extends QuestEvent> getEvent(String name) {
		return events.get(name);
	}
	
	/**
	 * returns Class object of objective with given name
	 * @param name
	 * @return
	 */
	public Class<? extends Objective> getObjective(String name) {
		return objectives.get(name);
	}
	
	/**
	 * stores pointer to ObjectiveSaving instance in order to store it on disable
	 * @param object
	 */
	public void putObjectiveSaving(ObjectiveSaving object) {
		saving.add(object);
	}
	
	/**
	 * deleted pointer to ObjectiveSaving instance in case the objective was completed and needs to be deleted
	 * @param object
	 */
	public void deleteObjectiveSaving(ObjectiveSaving object) {
		saving.remove(object);
	}

	/**
	 * loads from database all objectives of given player
	 * @param playerID
	 */
	public void loadObjectives(String playerID) {
		try {
			MySQL.openConnection();
			ResultSet res = MySQL.querySQL("SELECT instructions FROM objectives WHERE playerID = '" + playerID + "' AND isused= 1;");
			if (res.isBeforeFirst()) {
				while (res.next()) {
					BetonQuest.objective(playerID, res.getString("instructions"));
				}
			} else {
				res = MySQL.querySQL("SELECT instructions FROM objectives WHERE playerID = '" + playerID + "' AND isused= 0;");
				while (res.next()) {
					BetonQuest.objective(playerID, res.getString("instructions"));
				}
				MySQL.updateSQL("UPDATE objectives SET isused = 1 WHERE playerID = '" + playerID + "' AND isused = 0;");
			}
			MySQL.closeConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * returns if the condition described by conditionID is met
	 * @param conditionID
	 * @return
	 */
	public static boolean condition(String playerID, String conditionID) {
		String conditionInstruction = ConfigInput.getString("conditions." + conditionID);
		String[] parts = conditionInstruction.split(" ");
		Class<? extends Condition> condition = BetonQuest.getInstance().getCondition(parts[0]);
		Condition instance = null;
		try {
			instance = condition.getConstructor(String.class, String.class).newInstance(playerID, conditionInstruction);
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
			// return false for safety
			return false;
		}
		return instance.isMet();
	}
	
	/**
	 * fires the event described by eventID
	 * @param eventID
	 */
	public static void event(String playerID, String eventID) {
		String eventInstruction = ConfigInput.getString("events." + eventID);
		String[] parts = eventInstruction.split(" ");
		Class<? extends QuestEvent> event = BetonQuest.getInstance().getEvent(parts[0]);
		try {
			event.getConstructor(String.class, String.class).newInstance(playerID, eventInstruction);
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * creates new objective for given player
	 * @param playerID
	 * @param instruction
	 */
	public static void objective(String playerID, String instruction) {
		String[] parts = instruction.split(" ");
		Class<? extends Objective> objective = BetonQuest.getInstance().getObjective(parts[0]);
		try {
			objective.getConstructor(String.class, String.class).newInstance(playerID, instruction);
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
	}
	
	public void loadPlayerStrings(String playerID) {
		try {
			MySQL.openConnection();
			ResultSet res = MySQL.querySQL("SELECT string FROM strings WHERE playerID = '" + playerID + "' AND isused = 1;");
			if (res.isBeforeFirst()) {
				while (res.next()) {
					putPlayerString(playerID, res.getString("string"));
				}
			} else {
				res = MySQL.querySQL("SELECT string FROM strings WHERE playerID = '" + playerID + "' AND isused = 0;");
				while (res.next()) {
					putPlayerString(playerID, res.getString("string"));
				}
				MySQL.updateSQL("UPDATE strings SET isused = 1 WHERE playerID = '" + playerID + "' AND isused = 0");
			}
			MySQL.closeConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Puts a string in player's list
	 * @param playerID
	 * @param string
	 */
	public void putPlayerString(String playerID, String string) {
		if (!playerStrings.containsKey(playerID)) {
			playerStrings.put(playerID, new ArrayList<String>());
		}
		playerStrings.get(playerID).add(string);
	}
	
	/**
	 * Checks if player has specified string in his list
	 * @param playerID
	 * @param string
	 * @return
	 */
	public boolean havePlayerString(String playerID, String string) {
		if (!playerStrings.containsKey(playerID)) {
			return false;
		}
		return playerStrings.get(playerID).contains(string);
	}
	
	/**
	 * Removes specified string from player's list
	 * @param playerID
	 * @param string
	 */
	public void removePlayerString(String playerID, String string) {
		if (playerStrings.containsKey(playerID)) {
			playerStrings.get(playerID).remove(string);
		}
	}
	
	/**
	 * Removes player's list from HashMap and returns it (eg. for storing in database)
	 * @param playerID
	 */
	public void savePlayerStrings(String playerID) {
		List<String> strings = playerStrings.remove(playerID);
		if (strings == null) {
			return;
		}
		MySQL.openConnection();
		MySQL.updateSQL("DELETE FROM strings WHERE playerID = '" + playerID + "'");
		for (String string : strings) {
			MySQL.updateSQL("INSERT INTO strings (playerID, string) VALUES ('" + playerID + "', '" + string + "')");
		}
		MySQL.closeConnection();
	}
	
	public void loadJournal(String playerID) {
		journals.put(playerID, new Journal(playerID));
	}
	
	public Journal getJournal(String playerID) {
		return journals.get(playerID);
	}
	
	public void saveJournal(String playerID) {
		MySQL.openConnection();
		MySQL.updateSQL("DELETE FROM journal WHERE playerID = '" + playerID + "'");
		List<Pointer> pointers = journals.remove(playerID).getPointers();
		for (Pointer pointer : pointers) {
			MySQL.updateSQL("INSERT INTO journal (playerID, pointer, date) VALUES ('" + playerID + "', '" + pointer.getPointer() + "', '" + pointer.getTimestamp() + "')");
		}
		MySQL.closeConnection();
	}
}